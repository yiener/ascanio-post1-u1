// Clase: org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
// Método: getSingleton(String beanName)

/** Cache of singleton objects: bean name to bean instance. */
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

@Override
public Object getSingleton(String beanName) {
    return getSingleton(beanName, true);
}

protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    // Quick check for existing instance without full singleton lock
    Object singletonObject = this.singletonObjects.get(beanName);
    if (singletonObject == null) {
        // ... lógica para crear la instancia si no existe
    }
    return singletonObject;
}