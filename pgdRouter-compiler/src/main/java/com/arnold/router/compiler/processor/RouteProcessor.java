package com.arnold.router.compiler.processor;


import static com.arnold.router.compiler.util.Consts.ANNOTATION_TYPE_ROUTE;
import static com.arnold.router.compiler.util.Consts.BASE_ROUTER_MAP;
import static com.arnold.router.compiler.util.Consts.BASE_ROUTER_PATH;
import static com.arnold.router.compiler.util.Consts.BASE_SPI_SERVICE_LOADER;
import static com.arnold.router.compiler.util.Consts.IRESISTER_MODULE;
import static com.arnold.router.compiler.util.Consts.METHOD_LOAD_INTO;
import static com.arnold.router.compiler.util.Consts.METHOD_STOP;
import static com.arnold.router.compiler.util.Consts.NAME_OF_GROUP;
import static com.arnold.router.compiler.util.Consts.PACKAGE_OF_GENERATE_FILE;
import static com.arnold.router.compiler.util.Consts.WARNING_TIPS;
import static javax.lang.model.element.Modifier.PUBLIC;

import com.google.auto.service.AutoService;
import com.arnold.router.annotation.Route;
import com.arnold.router.compiler.model.RouteMeta;
import com.arnold.router.compiler.util.Consts;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
@SupportedAnnotationTypes({ANNOTATION_TYPE_ROUTE})
public class RouteProcessor extends BaseProcessor {
    private Map<String, Set<RouteMeta>> groupMap = new HashMap<>();

    private TypeMirror iRouterProtocol = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        iRouterProtocol = elementUtils.getTypeElement(Consts.IROUTER_PROTOCOL).asType();
        logger.info(">>> RouteProcessor init. <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
            try {
                logger.info(">>> Found routes, start... <<<");
                this.parseRoutes(routeElements);

            } catch (Exception e) {
                logger.error(e);
            }
            return true;
        }

        return false;
    }

    private void parseRoutes(Set<? extends Element> routeElements) throws IOException {
        if (CollectionUtils.isNotEmpty(routeElements)) {
            // prepare the type an so on.
            logger.info(">>> Found routes, size is " + routeElements.size() + " <<<");

            TypeElement type_IResisterModule = elementUtils.getTypeElement(IRESISTER_MODULE);

//            ParameterizedTypeName inputBaseRouterMap = ParameterizedTypeName.get(ClassName.get(BASE_ROUTER_PATH, BASE_ROUTER_MAP));
//            ParameterizedTypeName inputBaseSpiServiceLoader = ParameterizedTypeName.get(ClassName.get(BASE_ROUTER_PATH, BASE_SPI_SERVICE_LOADER));

            ParameterSpec baseRouterMapParamSpec = ParameterSpec.builder(ClassName.get(BASE_ROUTER_PATH, BASE_ROUTER_MAP), "appBizRouter").build();
            ParameterSpec baseSpiServiceLoaderParamSpec = ParameterSpec.builder(ClassName.get(BASE_ROUTER_PATH, BASE_SPI_SERVICE_LOADER), "appSpiServiceLoader").build();  //


            for (Element element : routeElements) {
                TypeMirror tm = element.asType();
                if (!types.isSubtype(tm, iRouterProtocol)) {
                    throw new RuntimeException("@Route类需要实现[IRouterProtocol]接口");
                }

                Route route = element.getAnnotation(Route.class);
                String key = route.key();
                if (StringUtils.isEmpty(key) && element instanceof TypeElement) {
                    List<? extends TypeMirror> interfaces = ((TypeElement) element).getInterfaces();
                    if (interfaces == null || interfaces.size() == 0) {
                        throw new RuntimeException("The @Route is marked on unsupported class");
                    }
                    for (TypeMirror superinterface : interfaces) {
                        if (superinterface instanceof DeclaredType && types.isSubtype(superinterface, iRouterProtocol)) {
                            TypeElement asElement = (TypeElement) ((DeclaredType) superinterface).asElement();
                            ClassName className = ClassName.get(asElement);
                            key = className.reflectionName();
                            break;
                        }
                    }
                }
                RouteMeta routeMeta = new RouteMeta(key, element);
                categories(routeMeta);
            }


            for (Map.Entry<String, Set<RouteMeta>> entry : groupMap.entrySet()) {
                String groupName = entry.getKey();

                MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(baseRouterMapParamSpec)
                        .addParameter(baseSpiServiceLoaderParamSpec);

                MethodSpec.Builder stopMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_STOP)
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(baseRouterMapParamSpec)
                        .addParameter(baseSpiServiceLoaderParamSpec);

                for (RouteMeta routeMeta : entry.getValue()) {
                    ClassName className = ClassName.get((TypeElement) routeMeta.getRawType());
                    loadIntoMethodOfGroupBuilder.addStatement(
                            "appBizRouter.registerRouter($S, new $T())",
                            routeMeta.getKey(),
                            className);

                    stopMethodOfGroupBuilder.addStatement(
                            "appBizRouter.unregisterRouter($S)",
                            routeMeta.getKey());
                }

                String groupFileName = NAME_OF_GROUP + groupName;
                JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                        TypeSpec.classBuilder(groupFileName)
                                .addJavadoc(WARNING_TIPS)
                                .addSuperinterface(ClassName.get(type_IResisterModule))
                                .addModifiers(PUBLIC)
                                .addMethod(loadIntoMethodOfGroupBuilder.build())
                                .addMethod(stopMethodOfGroupBuilder.build())
                                .build()
                ).build().writeTo(mFiler);
            }
        }
    }


    /**
     * Sort metas in group.
     *
     * @param routeMete metas.
     */
    private void categories(RouteMeta routeMete) {

        logger.info(">>> Start categories， key = " + routeMete.getKey() + " <<<");
        Set<RouteMeta> routeMetas = groupMap.get(moduleName);
        if (CollectionUtils.isEmpty(routeMetas)) {
            Set<RouteMeta> routeMetaSet = new TreeSet<>(new Comparator<RouteMeta>() {
                @Override
                public int compare(RouteMeta r1, RouteMeta r2) {
                    try {
                        return r1.getKey().compareTo(r2.getKey());
                    } catch (NullPointerException npe) {
                        logger.error(npe.getMessage());
                        return 0;
                    }
                }
            });
            routeMetaSet.add(routeMete);
            groupMap.put(moduleName, routeMetaSet);
        } else {
            routeMetas.add(routeMete);
        }

    }
}
