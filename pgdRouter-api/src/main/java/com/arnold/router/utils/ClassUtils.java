package com.arnold.router.utils;


/**
 * 类反射的一些处理
 * <p>
 * Created by waylenw on 16/8/30.
 */
public class ClassUtils {
    private ClassUtils() {
    }

    public static String getClassName(Class classZZ) {
        return classZZ.getCanonicalName();
    }
}
