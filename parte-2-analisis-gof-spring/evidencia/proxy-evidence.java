package org.springframework.aop.framework;

import java.lang.reflect.InvocationHandler;

import java.lang.reflect.Method;

import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

final class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

private final AdvisedSupport advised;

@Override

@Nullable

public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

Object target = this.advised.getTargetSource().getTarget();

Class<?> targetClass = (target != null ? target.getClass() : null);

// Obtiene la cadena de interceptores (advices) aplicables al método invocado

List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

if (chain.isEmpty()) {

// Si no hay aspectos aplicables, se invoca directamente el método sobre el objeto real

return AopUtils.invokeJoinpointUsingReflection(target, method, args);

} else {

// Se crea la invocación reflexiva para ejecutar la cadena de interceptores

MethodInvocation invocation = new ReflectiveMethodInvocation(

proxy, target, method, args, targetClass, chain

);

// Procede a ejecutar los interceptores (advices) y finalmente el método objetivo

return invocation.proceed();

}

}

}