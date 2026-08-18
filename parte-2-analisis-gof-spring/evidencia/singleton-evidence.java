package org.springframework.beans.factory.support;

import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.lang.Nullable;

public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {

/** Caché de objetos singleton: nombre del bean -> instancia del bean */

private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

@Override

@Nullable

public Object getSingleton(String beanName) {

return getSingleton(beanName, true);

}

@Nullable

protected Object getSingleton(String beanName, boolean allowEarlyReference) {

// Intenta obtener la instancia existente desde la caché concurrente

Object singletonObject = this.singletonObjects.get(beanName);

if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {

synchronized (this.singletonObjects) {

singletonObject = this.singletonObjects.get(beanName);

if (singletonObject == null) {

// Lógica para manejar referencias tempranas y resolución de dependencias circulares

}

}

}

return singletonObject;

}

}