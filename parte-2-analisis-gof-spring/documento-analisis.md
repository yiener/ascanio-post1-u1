**# Análisis de Patrones GoF en Spring Framework**

---

**## Portada**

* ***Nombre del Estudiante:*** Yeiner Ascanio Colmenares

* ***Código:*** 02230132014

* ***Curso:*** Patrones de Diseño de Software

* ***Unidad:*** Unidad 1 – Fundamentos de Patrones de Diseño y Buenas Prácticas

* ***Fecha:*** 18 de agosto de 2026

---

**## 1. Introducción**

En el ámbito de la ingeniería de software moderna, los patrones de diseño catalogados por el **Gang of Four** (GoF) constituyen soluciones probadas para abordar problemas recurrentes en la arquitectura e implementación de sistemas orientados a objetos (Gamma et al., 1994). Dentro del ecosistema Java, el marco de trabajo ***Spring Framework*** (y su extensión ***Spring Boot***) representa uno de los casos de estudio más icónicos en la aplicación sistemática de estas soluciones (Refactoring Guru, s.f.). Spring Boot abstrae la configuración compleja del desarrollo empresarial mediante un contenedor de Inversión de Control (IoC) y un conjunto de módulos que delegan responsabilidades clave a patrones bien definidos, logrando un balance entre mantenibilidad, extensibilidad y modularidad (Spring Framework Documentation, 2026a).

El objetivo general de este documento es analizar la presencia y función de los patrones de diseño GoF dentro de la arquitectura interna de Spring Framework. Para cumplir este propósito, se examinarán tres patrones representativos pertenecientes a las tres categorías fundamentales (Creacional, Estructural y de Comportamiento), identificando sus componentes concretos dentro del código fuente del framework, el problema específico que resuelven en la gestión de la aplicación, su manifestación técnica mediante snippets de código Java y su alineación directa con los principios SOLID de diseño orientado a objetos.

---

**## 2. Análisis de Patrón 1: Singleton (Creacional)**

**### Nombre y Categoría**

* ***Nombre del Patrón:*** Singleton

* ***Categoría:*** Creacional

* ***Propósito:*** Garantizar que una clase tenga una única instancia en el ciclo de vida de la aplicación y proporcionar un punto de acceso global y controlado a dicha instancia (Gamma et al., 1994).

**### Clase / Componente en Spring**

* ***Clase Concreta:*** `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry`

* ***Módulo de Spring:*** `spring-beans`

* ***Ámbito / Scope:*** Contenedor de IoC (**Singleton Scope** por defecto)

**### Problema que Resuelve**

En aplicaciones empresariales de gran escala, instanciar repetidamente componentes pesados (como servicios de negocio, repositorios de datos o gestores de configuración) en cada solicitud o inyección genera un alto costo en consumo de memoria, sobrecarga en el recolector de basura (**Garbage Collector**) e inconsistencias en la gestión del estado compartido (Refactoring Guru, s.f.).

Spring resuelve esta problemática mediante el alcance **singleton** por defecto en su contenedor IoC. La clase `DefaultSingletonBeanRegistry` actúa como un registro centralizado que almacena y gestiona el ciclo de vida de todas las instancias únicas de beans definidas en el contexto. Cuando un componente requiere una dependencia, el contenedor consulta este registro para reutilizar la instancia existente en lugar de instanciar un objeto nuevo (Spring Framework Documentation, 2026a).

**### Extracto de Código Fuente**

```java

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

```

**Explicación técnica:** El atributo `singletonObjects` implementa una estructura de datos concurrente (`ConcurrentHashMap`) que almacena las referencias únicas asociadas a su identificador (`beanName`). El método `getSingleton` intercepta las solicitudes de resolución de beans para garantizar que solo se cree una instancia por cada bean registrado (Spring Framework Documentation, 2026a).

**### Principios SOLID Asociados**

1. ***Principio de Responsabilidad Única (SRP -** **Single Responsibility Principle***):*** La responsabilidad de garantizar la unicidad de las instancias no recae sobre las clases de negocio escritas por el desarrollador, sino de manera exclusiva sobre la infraestructura del contenedor IoC mediante `DefaultSingletonBeanRegistry` (Gamma et al., 1994).

2. ***Principio de Inversión de Dependencias (DIP -** **Dependency Inversion Principle***):*** Las clases de la aplicación dependen de abstracciones (interfaces de servicios) y no necesitan conocer el mecanismo de instanciación ni la gestión de unicidad del bean, recibiendo la referencia inyectada por el contenedor (Spring Framework Documentation, 2026a).

---

**## 3. Análisis de Patrón 2: Proxy (Estructural)**

**### Nombre y Categoría**

* ***Nombre del Patrón:*** Proxy

* ***Categoría:*** Estructural

* ***Propósito:*** Proporcionar un sustituto o intermediario (**surrogate/placeholder**) para controlar el acceso a un objeto destino, permitiendo ejecutar lógica adicional antes o después de la llamada al objeto real.

**### Clase / Componente en Spring**

* ***Clase Concreta:*** `org.springframework.aop.framework.JdkDynamicAopProxy`

* ***Módulo de Spring:*** `spring-aop`

* ***Componente Asociado:*** Programación Orientada a Aspectos (Spring AOP, `@Transactional`, `@Observed`, `@PreAuthorize`)

**### Problema que Resuelve**

En la arquitectura de software empresarial existen funcionalidades transversales o de corte vertical (**cross-cutting concerns**) como el manejo de transacciones de base de datos, el registro de auditoría (**logging**), la gestión de seguridad y el cálculo de métricas. Insertar manualmente este código repetitivo dentro de las clases de servicio contamina la lógica de negocio y genera una alta duplicación de código (Refactoring Guru, s.f.).

Spring AOP resuelve esta problemática empleando el patrón Proxy. En lugar de inyectar directamente la clase de servicio real, Spring genera de forma dinámica un objeto Proxy (`JdkDynamicAopProxy` o un proxy CGLIB) que envuelve a la instancia real. Cuando se invoca un método anotado (por ejemplo con `@Transactional`), el Proxy intercepta la llamada, inicia la transacción, delega la ejecución al objeto objetivo y finalmente confirma o revierte la transacción según el resultado (Spring Framework Documentation, 2026b).

**### Extracto de Código Fuente**

```java

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

```

**Explicación técnica:** La clase implementa `InvocationHandler` de Java Reflection. El método `invoke` intercepta todas las llamadas dirigidas al bean, obtiene los consejos (**advices**) mediante la cadena de interceptores y coordina la ejecución antes, durante y después de invocar el método del objeto `target`.

**### Principios SOLID Asociados**

1. ***Principio de Abierto/Cerrado (OCP -** **Open/Closed Principle***):*** Permite extender el comportamiento de las clases de negocio (añadiendo capacidades transaccionales, de seguridad o auditoría) sin necesidad de modificar el código fuente existente de dichas clases (Gamma et al., 1994).

2. ***Principio de Responsabilidad Única (SRP -** **Single Responsibility Principle***):*** Separa limpiamente las preocupaciones transversales (gestión de infraestructura) de la lógica del dominio de negocio principal (Refactoring Guru, s.f.).

---

**## 4. Análisis de Patrón 3: Observer (Comportamiento)**

**### Nombre y Categoría**

* ***Nombre del Patrón:*** Observer (también denominado **Publisher-Subscriber** / **Event Listener**)

* ***Categoría:*** Comportamiento

* ***Propósito:*** Definir una dependencia de tipo uno-a-muchos entre objetos, de manera que cuando un objeto (sujeto/publicador) cambia su estado o emite una señal, todos sus dependientes (observadores/escuchadores) son notificados automáticamente (Gamma et al., 1994).

**### Clase / Componente en Spring**

* ***Clase Concreta / Publicador:*** `org.springframework.context.event.SimpleApplicationEventMulticaster`

* ***Interfaz Receptor / Observador:*** `org.springframework.context.ApplicationListener`

* ***Clase de Evento:*** `org.springframework.context.ApplicationEvent`

* ***Módulo de Spring:*** `spring-context`

**### Problema que Resuelve**

Cuando un módulo de la aplicación necesita reaccionar ante eventos ocurridos en otro módulo (por ejemplo, enviar un correo electrónico de bienvenida tras el registro exitoso de un usuario), invocar directamente el servicio de correo dentro del servicio de usuarios crea un acoplamiento rígido entre ambos componentes. Esto dificulta la reutilización, complica la realización de pruebas unitarias aisladas y viola la modularidad (Refactoring Guru, s.f.).

El sistema de eventos de Spring, basado en el patrón Observer, resuelve este problema desacoplando al emisor del evento de sus receptores. El servicio de usuario únicamente publica un evento (`UserRegisteredEvent`) a través del multicaster (`SimpleApplicationEventMulticaster`). Cero o múltiples componentes registrados como `ApplicationListener` pueden escuchar y procesar dicho evento de manera sincrónica o asincrónica sin que el emisor conozca la existencia de los receptores (Spring Framework Documentation, 2026c).

**### Extracto de Código Fuente**

```java

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

```

**Explicación técnica:** El método `multicastEvent` consulta el registro interno de receptores (`getApplicationListeners`), filtra únicamente aquellos interesados en el tipo de evento actual y desencadena la llamada al método `onApplicationEvent` de cada suscriptor (Spring Framework Documentation, 2026c).

**### Principios SOLID Asociados**

1. ***Principio de Inversión de Dependencias (DIP -** **Dependency Inversion Principle***):*** Tanto el publicador como los receptores dependen de abstracciones generales del marco de trabajo (`ApplicationEvent` y `ApplicationListener`) en lugar de depender de clases concretas entre sí (Gamma et al., 1994).

2. ***Principio de Abierto/Cerrado (OCP -** **Open/Closed Principle***):*** Es posible agregar nuevos componentes escuchadores para reaccionar a un evento existente sin modificar una sola línea de código en el componente publicador ni en los demás escuchadores (Spring Framework Documentation, 2026c).

---

**## 5. Conclusiones**

El análisis arquitectónico realizado sobre Spring Framework evidencia de forma contundente cómo la integración estratégica de los patrones de diseño GoF —como Singleton en el contenedor IoC, Proxy en la capa de AOP y Observer en el subsistema de eventos— resulta determinante para garantizar el desacoplamiento, la extensibilidad y la rigurosidad técnica de los sistemas distribuidos orientados a objetos (Gamma et al., 1994; Spring Framework Documentation, 2026a, 2026b, 2026c). Esta comprensión profunda del funcionamiento interno del framework nos enseña que los patrones no son simples recetas teóricas de código, sino pilares estructurales que articulan los principios SOLID, demostrándonos que la clave para diseñar aplicaciones robustas y sostenibles en nuestro propio desarrollo radica en aislar responsabilidades, programar hacia abstracciones y favorecer esquemas extensibles por encima del acoplamiento rígido (Refactoring Guru, s.f.).

---

**## 6. Referencias**

* Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). **Design Patterns: Elements of Reusable Object-Oriented Software**. Addison-Wesley.

* Refactoring Guru. (s.f.). **Design Patterns in Java**. Recuperado de https\://refactoring.guru/design-patterns/java

* Spring Framework Documentation. (2026a). **Core Technologies: The IoC Container**. Spring Docs. Recuperado de https\://docs.spring.io/spring-framework/reference/core/beans.html

* Spring Framework Documentation. (2026b). **Aspect Oriented Programming with Spring**. Spring Docs. Recuperado de https\://docs.spring.io/spring-framework/reference/core/aop.html

* Spring Framework Documentation. (2026c). **Standard and Custom Events**. Spring Docs. Recuperado de https\://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-events
