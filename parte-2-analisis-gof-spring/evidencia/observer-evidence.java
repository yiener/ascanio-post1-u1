package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;

import org.springframework.context.ApplicationListener;

import org.springframework.core.ResolvableType;

import org.springframework.lang.Nullable;

import java.util.concurrent.Executor;

public class SimpleApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

@Override

public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {

ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));

Executor executor = getTaskExecutor();

// Itera sobre todos los ApplicationListeners registrados para la tipología del evento

for (ApplicationListener<?> listener : getApplicationListeners(event, type)) {

if (executor != null) {

// Ejecución asincrónica si se ha configurado un Executor

executor.execute(() -> invokeListener(listener, event));

} else {

// Ejecución sincrónica por defecto

invokeListener(listener, event);

}

}

}

private void invokeListener(ApplicationListener listener, ApplicationEvent event) {

try {

listener.onApplicationEvent(event);

} catch (Throwable err) {

// Manejo estandarizado de excepciones durante la notificación

}

}

}