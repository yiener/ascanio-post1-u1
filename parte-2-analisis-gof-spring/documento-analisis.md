# Análisis de Patrones GoF en Spring Framework

**Autor:** YEINER ASCANIO COLMENARES  
**Código:** 02230132014  
**Curso:** Patrones de Diseño de Software  
**Unidad:** 1 – Fundamentos de Patrones de Diseño y Buenas Prácticas  
**Fecha:** 18/08/2026  

---

## 1. Introducción

El presente documento tiene como objetivo analizar la aplicación de patrones de diseño GoF (Gang of Four) en el framework Spring, específicamente en su ecosistema Spring Boot. Spring es uno de los frameworks más utilizados en el desarrollo de aplicaciones empresariales Java, y su arquitectura está fuertemente basada en patrones de diseño que facilitan la mantenibilidad, extensibilidad y desacoplamiento del código.

Se han seleccionado tres patrones de distintas categorías (Creacional, Estructural y de Comportamiento) que se encuentran presentes en el núcleo de Spring. Para cada uno se describirá su propósito, la clase concreta donde se implementa, el problema que resuelve, un extracto de código fuente que lo evidencie y la relación con los principios SOLID. Este análisis permite comprender por qué Spring es un framework robusto y cómo la aplicación sistemática de patrones de diseño contribuye a su calidad arquitectónica.

---

## 2. Análisis de Patrón 1: Singleton (Creacional)

### Nombre y categoría
* **Patrón:** Singleton  
* **Categoría:** Creacional  
* **Propósito:** Garantizar que una clase tenga una única instancia y proporcionar un punto de acceso global a ella.

### Ubicación en Spring
* **Clase:** `org.springframework.beans.factory.support.DefaultSingletonBeanRegistry`  
* **Módulo:** `spring-beans`

### Problema que resuelve
Spring necesita gestionar el ciclo de vida de los beans que se definen en el contenedor IoC (*Inversion of Control*). Por defecto, los beans tienen alcance *singleton*, es decir, el contenedor crea una única instancia por cada bean definido y la reutiliza en todas las inyecciones de dependencia. Sin este patrón, cada vez que se solicitara un bean se crearía una nueva instancia, lo que aumentaría el consumo de memoria y podría generar inconsistencias en el estado compartido. El Singleton resuelve este problema centralizando la creación y almacenamiento de la instancia única en el registro de singletons.

### Evidencia de código
A continuación se muestra un extracto de la clase `DefaultSingletonBeanRegistry` que implementa el registro de instancias singleton:

```java
// org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

@Override
public Object getSingleton(String beanName) {
    return getSingleton(beanName, true);
}

protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    Object singletonObject = this.singletonObjects.get(beanName);
    if (singletonObject == null) {
        // ... lógica de creación sincronizada
    }
    return singletonObject;
}
```

El mapa `singletonObjects` actúa como el caché de instancias únicas, y el método `getSingleton` devuelve la instancia existente o la crea si no está presente.

### Principio SOLID asociado
Este patrón refuerza el **Principio de Responsabilidad Única (SRP)**, ya que la responsabilidad de gestionar la unicidad y el ciclo de vida de los beans recae exclusivamente en el contenedor (la clase `DefaultSingletonBeanRegistry`), y no en las propias clases de negocio. Además, apoya el **Principio de Inversión de Dependencias (DIP)**, porque permite que las clases dependan de abstracciones (interfaces) y el contenedor inyecte la instancia única sin que las clases conozcan los detalles de su creación.

---

## 3. Análisis de Patrón 2: Proxy (Estructural)

### Nombre y categoría
* **Patrón:** Proxy  
* **Categoría:** Estructural  
* **Propósito:** Proporcionar un sustituto o intermediario para controlar el acceso a un objeto, añadiendo funcionalidades como logging, seguridad o lazy loading.

### Ubicación en Spring
* **Clase:** `org.springframework.aop.framework.JdkDynamicAopProxy`  
* **Módulo:** `spring-aop`

### Problema que resuelve
Spring AOP (*Aspect-Oriented Programming*) permite aplicar aspectos transversales (como logging, transacciones, seguridad) a métodos de beans sin modificar el código fuente de estos. Para ello, Spring crea un proxy que envuelve al objeto original e intercepta las llamadas a métodos, aplicando los *advices* (consejos) correspondientes. Sin el patrón Proxy, sería necesario modificar cada clase para añadir estas funcionalidades, violando el principio de separación de preocupaciones. El proxy actúa como un intermediario que delega la ejecución al objeto real después de aplicar los aspectos.

### Evidencia de código
Extracto de `JdkDynamicAopProxy` que muestra la invocación del método interceptado:

```java
// org.springframework.aop.framework.JdkDynamicAopProxy
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    MethodInvocation invocation;
    Object target = this.advised.getTargetSource().getTarget();
    // ... se configura la invocación con los interceptores
    invocation = new ReflectiveMethodInvocation(proxy, target, method, args, ...);
    // Ejecuta la cadena de interceptores (advice)
    return invocation.proceed();
}
```

El método `invoke` captura la llamada al método, obtiene el objeto objetivo y ejecuta la cadena de interceptores (que incluyen los *advices* de AOP) antes de invocar finalmente el método real.

### Principio SOLID asociado
El patrón Proxy en Spring AOP implementa el **Principio de Abierto/Cerrado (OCP)**, ya que permite extender el comportamiento de los beans (añadir aspectos) sin modificar el código de las clases existentes. También respalda el **Principio de Responsabilidad Única (SRP)** al separar las preocupaciones transversales de la lógica de negocio.

---

## 4. Análisis de Patrón 3: Observer (Comportamiento)

### Nombre y categoría
* **Patrón:** Observer (también conocido como *Event Listener*)  
* **Categoría:** Comportamiento  
* **Propósito:** Definir una dependencia uno-a-muchos entre objetos, de modo que cuando un objeto cambie de estado, todos sus dependientes sean notificados automáticamente.

### Ubicación en Spring
* **Clase:** `org.springframework.context.event.SimpleApplicationEventMulticaster` (publicador de eventos)  
* **Interfaz:** `org.springframework.context.ApplicationListener` (receptor)  
* **Módulo:** `spring-context`

### Problema que resuelve
Spring proporciona un mecanismo de eventos para comunicar componentes de forma desacoplada. Un componente puede publicar un evento (por ejemplo, `ApplicationEvent`) y otros componentes que implementen `ApplicationListener` pueden reaccionar a él. Esto permite una comunicación asíncrona y desacoplada, evitando que el publicador conozca a los receptores. Sin el patrón Observer, se necesitaría una dependencia directa entre componentes, lo que aumentaría el acoplamiento y dificultaría la extensibilidad.

### Evidencia de código
Extracto de `SimpleApplicationEventMulticaster` que muestra la notificación a los listeners:

```java
// org.springframework.context.event.SimpleApplicationEventMulticaster
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
```

El multicaster obtiene todos los listeners registrados para el tipo de evento y los notifica, ejecutando la invocación de forma sincrónica o asincrónica según la configuración.

### Principio SOLID asociado
Este patrón aplica el **Principio de Inversión de Dependencias (DIP)**, ya que el publicador depende de la abstracción `ApplicationEvent` y los listeners dependen de la abstracción `ApplicationListener`, sin conocer las implementaciones concretas. También favorece el **Principio de Abierto/Cerrado (OCP)**, porque se pueden agregar nuevos listeners sin modificar el publicador.

---

## 5. Conclusiones

El análisis realizado evidencia que Spring Framework aplica de manera sistemática y coherente los patrones de diseño GoF para resolver problemas recurrentes en el desarrollo de aplicaciones empresariales. La adopción de patrones como Singleton, Proxy y Observer no solo mejora la calidad del framework (mantenibilidad, extensibilidad y desacoplamiento), sino que también educa a los desarrolladores sobre buenas prácticas arquitectónicas. La conexión de cada patrón con los principios SOLID demuestra que el diseño de Spring está alineado con los fundamentos de la ingeniería de software moderna. Como lección personal, este análisis refuerza la importancia de aplicar patrones de diseño y principios SOLID en el desarrollo propio, ya que permiten construir sistemas más flexibles, robustos y fáciles de mantener.

---

## 6. Referencias

1. Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
2. Spring Framework Documentation. (2026). *Core Technologies – The IoC Container*. Recuperado de https://docs.spring.io/spring-framework/reference/core/beans.html
3. Spring Framework Documentation. (2026). *AOP – Proxying Mechanisms*. Recuperado de https://docs.spring.io/spring-framework/reference/core/aop/proxying.html
4. Spring Framework Documentation. (2026). *Events and Listeners*. Recuperado de https://docs.spring.io/spring-framework/reference/core/beans/events.html
5. Refactoring Guru. (s.f.). *Design Patterns*. Recuperado de https://refactoring.guru/design-patterns