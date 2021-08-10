package com.arnold.router.compiler.util;

/**
 * Some consts used in processors
 *
 * @author Alex <a href="mailto:zhilong.liu@aliyun.com">Contact me.</a>
 * @version 1.0
 * @since 16/8/24 20:18
 */
public class Consts {
    // Generate
    public static final String SEPARATOR = "$$";
    public static final String PROJECT = "PGDRouter";
    public static final String WARNING_TIPS = "DO NOT EDIT THIS FILE!!! IT WAS GENERATED BY PGDROUTER.";
    public static final String METHOD_LOAD_INTO = "onCreate";
    public static final String METHOD_STOP = "onStop";



    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR;


    public static final String PACKAGE_OF_GENERATE_FILE = "com.arnold.android.routes";

    // Java type
    private static final String LANG = "java.lang";
    public static final String BYTE = LANG + ".Byte";
    public static final String SHORT = LANG + ".Short";
    public static final String INTEGER = LANG + ".Integer";
    public static final String LONG = LANG + ".Long";
    public static final String FLOAT = LANG + ".Float";
    public static final String DOUBEL = LANG + ".Double";
    public static final String BOOLEAN = LANG + ".Boolean";
    public static final String CHAR = LANG + ".Character";
    public static final String STRING = LANG + ".String";
    public static final String SERIALIZABLE = "java.io.Serializable";

    // Custom interface
    private static final String FACADE_PACKAGE = "com.arnold.router";


    public static final String BASE_ROUTER_PATH = FACADE_PACKAGE+".base";
    public static final String BASE_ROUTER_MAP = "BaseBizRouter";
    public static final String BASE_SPI_SERVICE_LOADER = "BaseSpiServiceLoader";
    public static final String IRESISTER_MODULE = "com.arnold.router.base.IResisterModule";
    public static final String IROUTER_PROTOCOL = "com.arnold.router.base.IRouterProtocol";


    // Log
    static final String PREFIX_OF_LOGGER = PROJECT + "::Compiler ";
    public static final String NO_MODULE_NAME_TIPS = "These no module name, at 'build.gradle', like :\n" +
            "android {\n" +
            "    defaultConfig {\n" +
            "        ...\n" +
            "        javaCompileOptions {\n" +
            "            annotationProcessorOptions {\n" +
            "                arguments = [AROUTER_MODULE_NAME: project.getName()]\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n";

    // Options of processor
    public static final String KEY_MODULE_NAME = "PGDROUTER_MODULE_NAME";

    // Annotation type
    public static final String ANNOTATION_TYPE_ROUTE = FACADE_PACKAGE + ".annotation.Route";
}