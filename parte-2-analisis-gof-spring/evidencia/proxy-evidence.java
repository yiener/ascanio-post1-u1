// Clase: org.springframework.aop.framework.JdkDynamicAopProxy
// Método: invoke(Object proxy, Method method, Object[] args)

public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    MethodInvocation invocation;
    Object target = this.advised.getTargetSource().getTarget();
    // ... configuración de la invocación
    invocation = new ReflectiveMethodInvocation(proxy, target, method, args, ...);
    // Ejecuta la cadena de interceptores (advice)
    return invocation.proceed();
}