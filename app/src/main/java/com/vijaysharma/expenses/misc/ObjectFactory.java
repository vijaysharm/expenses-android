package com.vijaysharma.expenses.misc;

import java.util.Map;

public class ObjectFactory {
    public interface Factory {
        <T> T create(ObjectFactory factory);
    }

    private static ObjectFactory INSTANCE;

    public static ObjectFactory getInstance() {
        return INSTANCE;
    }

    public static void setInstance(ObjectFactory factory) {
        INSTANCE = factory;
    }

    private final Map<Class<?>, Object> singletons;
    private final Map<Class<?>, Factory> factories;

    public ObjectFactory(Map<Class<?>, Object> singletons, Map<Class<?>, Factory> factories) {
        this.singletons = singletons;
        this.factories = factories;
    }

    public <T> T singleton(Class<T> clazz) {
        return (T) singletons.get(clazz);
    }

    public <T> T instance(Class<T> clazz) {
        return factories.get(clazz).create(this);
    }
}
