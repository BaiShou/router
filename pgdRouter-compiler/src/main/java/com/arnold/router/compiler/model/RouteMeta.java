package com.arnold.router.compiler.model;

import javax.lang.model.element.Element;

public class RouteMeta {

    public RouteMeta() {
    }

    public RouteMeta(String key, Element rawType) {
        this.key = key;
        this.rawType = rawType;
    }

    private String key;            // Path of route
    private Element rawType;        // Raw type of route

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Element getRawType() {
        return rawType;
    }

    public void setRawType(Element rawType) {
        this.rawType = rawType;
    }
}
