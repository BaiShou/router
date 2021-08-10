package com.arnold.router.base;



import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.arnold.router.utils.ClassUtils;

import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * 业务路由基类,页面相关
 * <p>
 * Created by waylenw on 2020/5/13.
 */
public class BaseBizRouter {

    private static BaseBizRouter sBaseBizRouter;

    public static BaseBizRouter getInstance() {
        if (sBaseBizRouter == null) {
            sBaseBizRouter = new BaseBizRouter();
        }
        return sBaseBizRouter;
    }

    private BaseBizRouter() {
    }

    public Map<String, IRouterProtocol> routerMap = new ArrayMap<>();
    public Map<String, Object> proxyMap = new ArrayMap<>();

    public void registerRouter(IRouterProtocol object) {
        registerRouter(ClassUtils.getClassName(object.getClass()), object);
    }

    public void registerRouter(Class routerClassZZ, IRouterProtocol object) {
        registerRouter(ClassUtils.getClassName(routerClassZZ), object);
    }

    public void registerRouter(String routerName, IRouterProtocol object) {
        routerMap.put(routerName, object);
    }

    public void unregisterRouter(Class routerClassZZ) {
        routerMap.remove(ClassUtils.getClassName(routerClassZZ));
    }

    public void unregisterRouter(String routerName) {
        routerMap.remove(routerName);
    }

    protected <T> T getRouter(@NonNull Class<T> routerClassZZ) {
        return (T) routerMap.get(ClassUtils.getClassName(routerClassZZ));
    }

//    public void registerRouter(AppBizRouterParams... appBizRouterParams) {
//        for (AppBizRouterParams appBizRouterParam : appBizRouterParams) {
//            registerRouter(appBizRouterParam.classZZ, appBizRouterParam.iRouterProtocol);
//        }
//    }

    protected <T extends IRouterProtocol> boolean checkRouterEmpty(@NonNull Class<T> routerClassZZ, String errorMessage) {
        if (getRouter(routerClassZZ) == null) {
            handlerRouterError(errorMessage);
        }
        return getRouter(routerClassZZ) == null;
    }

    public <T extends IRouterProtocol> T getRouterProtocol(@NonNull Class<T> routerClassZZ, String errMsg) {
        checkRouterEmpty(routerClassZZ, errMsg);
        return getRouterProxyProtocol(this, routerClassZZ);
    }

    //=================================以下为动态代理============================================

    protected <T extends IRouterProtocol> boolean checkRouterProxyEmpty(@NonNull Class<T> routerClassZZ) {
        return getRouterProxy(routerClassZZ) == null;
    }

    protected <T extends IRouterProtocol> T getRouterProxy(@NonNull Class<T> routerClassZZ) {
        return (T) proxyMap.get(routerClassZZ.getClass().getCanonicalName());
    }

    protected <T extends IRouterProtocol> void putRouterProxy(@NonNull Class<T> routerClassZZ, Object object) {
        proxyMap.put(ClassUtils.getClassName(routerClassZZ), object);
    }

    /**
     * 获取动态代理的路由对象(兼容为注册相关服务,报错问题)
     *
     * @param proxyObject
     * @param routerClassZZ
     * @param <T>
     * @return
     */
    public <T extends IRouterProtocol> T getRouterProxyProtocol(Object proxyObject, @NonNull Class<T> routerClassZZ) {
        //先去原始栈中查询
        if (!checkRouterEmpty(routerClassZZ, "")) {
            return getRouter(routerClassZZ);
        }
        if (checkRouterProxyEmpty(routerClassZZ)) {
            T t = null;
            try {
                t = (T) Proxy.newProxyInstance(proxyObject.getClass().getClassLoader(),
                    new Class<?>[]{routerClassZZ},
                    (proxy, method, args) -> {
                        if (proxy == null) {
                            return null;
                        }
                        return null;
                    });
            } catch (Exception e) {
                handlerRouterError("路由模块构建出错");
            }

            putRouterProxy(routerClassZZ, t);
            return t;
        } else {
            return getRouterProxy(routerClassZZ);
        }
    }

    protected void handlerRouterError(String errorMessage) {
//        if (!TextUtils.isEmpty(errorMessage)) {
//            if (BuildConfig.DEBUG) {
//                AppToast.I.shortToast(errorMessage);
//            }
//        }
    }
}
