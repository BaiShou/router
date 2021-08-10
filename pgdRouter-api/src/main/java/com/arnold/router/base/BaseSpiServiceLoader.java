package com.arnold.router.base;


import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.arnold.router.utils.ClassUtils;

import java.util.Map;

/**
 * 业务服务基类，提供数据和功能相关的支撑
 * <p>
 * Created by waylenw on 2020/5/13.
 */
public class BaseSpiServiceLoader {

    private static BaseSpiServiceLoader sBaseSpiServiceLoader;

    public static BaseSpiServiceLoader getInstance() {
        if (sBaseSpiServiceLoader == null) {
            sBaseSpiServiceLoader = new BaseSpiServiceLoader();
        }
        return sBaseSpiServiceLoader;
    }

    private BaseSpiServiceLoader() {
    }

    public Map<String, Object> serviceMap = new ArrayMap<>();

    public void registerService(Object object) {
        registerService(ClassUtils.getClassName(object.getClass()), object);
    }

    public void registerService(Class routerClassZZ, Object object) {
        registerService(ClassUtils.getClassName(routerClassZZ), object);
    }

    private void registerService(String routerName, Object object) {
        serviceMap.put(routerName, object);
    }

    public void unregisterService(Object object) {
        serviceMap.remove(ClassUtils.getClassName(object.getClass()));
    }

    private void unregisterService(String routerName) {
        serviceMap.remove(routerName);
    }

    public  <T> T getService(@NonNull Class<T> routerClassZZ) {
        return (T) serviceMap.get(ClassUtils.getClassName(routerClassZZ));
    }
}
