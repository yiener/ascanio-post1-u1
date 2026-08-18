// Clase: org.springframework.context.event.SimpleApplicationEventMulticaster
// Método: multicastEvent(ApplicationEvent event, ResolvableType eventType)

public void multicastEvent(final ApplicationEvent event, @Nullable ResolvableType eventType) {
    ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
    // Obtiene los listeners que escuchan este tipo de evento
    for (final ApplicationListener<?> listener : getApplicationListeners(event, type)) {
        Executor executor = getTaskExecutor();
        if (executor != null) {
            executor.execute(() -> invokeListener(listener, event));
        } else {
            invokeListener(listener, event);
        }
    }
}