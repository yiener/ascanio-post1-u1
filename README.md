# Post-contenido — Unidad 1: Fundamentos de Patrones de Diseño y Buenas Prácticas

## Descripción
Repositorio del post-contenido de la Unidad 1 de Patrones de Diseño de Software — Sexto Semestre. Contiene dos partes: refactorización SOLID de un God Object (parte-1-refactorizacion-solid/) y análisis de patrones GoF en Spring Framework (parte-2-analisis-gof-spring/).

---

## Parte 1 — Refactorización SOLID
Proyecto Maven que refactoriza la clase `OrderProcessor` (God Object) aplicando los principios **SRP**, **OCP** y **DIP**. El código refactorizado separa responsabilidades en clases cohesivas, utiliza el patrón Strategy para los descuentos e inyecta dependencias por constructor.

### Análisis de Violaciones SOLID — Parte 1

| Principio | Método/Sección afectada | Descripción de la violación |
|-----------|-------------------------|-----------------------------|
| **SRP** (Single Responsibility Principle) | `calculateTotal`, `applyDiscount`, `saveOrder`, `sendEmail`, `printReport` | La clase `OrderProcessor` asume múltiples responsabilidades que deberían estar en clases separadas: cálculo de impuestos, aplicación de descuentos, persistencia de órdenes, envío de correos electrónicos y generación de reportes. Esto viola el Principio de Responsabilidad Única porque existen al menos cinco razones diferentes para modificar la clase (cambiar la lógica de impuestos, agregar un nuevo descuento, modificar la forma de guardar, cambiar el formato del correo o alterar el reporte). |
| **OCP** (Open/Closed Principle) | `applyDiscount` (con `if/else` sobre `customerType`) | El método `applyDiscount` utiliza una estructura condicional (`if/else`) para determinar el descuento según el tipo de cliente. Para agregar un nuevo tipo de cliente (por ejemplo, "PREMIUM"), es necesario modificar el código fuente de la clase `OrderProcessor`, añadiendo un nuevo `if`. Esto viola el Principio de Abierto/Cerrado, ya que la clase no está abierta a extensión (no se puede agregar un nuevo tipo de descuento sin modificar la clase) y no está cerrada a modificación. |
| **LSP** (Liskov Substitution Principle) | No aplica directamente (no hay herencia) | La clase `OrderProcessor` no extiende ninguna clase ni implementa interfaces, por lo que no hay subtipos que puedan violar LSP. Sin embargo, el diseño actual no favorece la creación de subtipos reutilizables, lo que limita la extensibilidad. Al refactorizar usando interfaces (como `DiscountStrategy`), se garantizará que cualquier implementación pueda sustituir a la interfaz base sin alterar el comportamiento esperado. |
| **ISP** (Interface Segregation Principle) | No aplica directamente (no hay interfaces) | La clase `OrderProcessor` no implementa ninguna interfaz, por lo que no se puede decir que viole ISP. Sin embargo, si se hubieran definido interfaces, estas serían monolíticas (por ejemplo, una interfaz `OrderProcessor` con todos los métodos), obligando a las implementaciones a depender de métodos que no necesitan. La refactorización aplicará interfaces segregadas (por ejemplo, `DiscountStrategy` será una interfaz pequeña y específica), respetando ISP. |
| **DIP** (Dependency Inversion Principle) | Toda la clase (dependencias internas sin abstracciones) | La clase `OrderProcessor` depende directamente de implementaciones concretas: `ArrayList` para la lista de órdenes, `System.out` para la salida de consola, y no utiliza inyección de dependencias. Las dependencias son creadas internamente (por ejemplo, `new ArrayList<>()`) en lugar de ser recibidas a través del constructor o mediante interfaces. Esto viola el Principio de Inversión de Dependencias, ya que los módulos de alto nivel (la lógica de negocio) dependen de detalles de bajo nivel (implementaciones concretas) en lugar de depender de abstracciones. |

---

## Parte 2 — Análisis de Patrones GoF en Spring Framework
Análisis documentado de tres patrones GoF de categorías distintas identificados en el código fuente de Spring Framework. El documento completo se encuentra en `parte-2-analisis-gof-spring/documento-analisis.md`.

### Patrones analizados

| # | Patrón | Categoría | Clase en Spring |
|---|--------|-----------|-----------------|
| 1 | **Singleton** | Creacional | `DefaultSingletonBeanRegistry` (spring-beans) |
| 2 | **Proxy** | Estructural | `JdkDynamicAopProxy` (spring-aop) |
| 3 | **Observer** | Comportamiento | `ApplicationEvent` / `ApplicationListener` (spring-context) |

Ver análisis detallado en: [parte-2-analisis-gof-spring/documento-analisis.md](parte-2-analisis-gof-spring/documento-analisis.md)

---

## Herramientas utilizadas
- Java 17, Apache Maven 3.9.16, VS Code, Git, GitHub
- Código fuente de Spring Framework (investigación en repositorio oficial)
- Documentación oficial de Spring Boot y Refactoring Guru

---
## Estructura del repositorio

ascanio-post1-u1/
├── parte-1-refactorizacion-solid/
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── java/
│               └── com/
│                   └── patrones/
│                       └── u1/
│                           ├── OrderProcessor.java      # God Object original
│                           ├── TaxCalculator.java       # SRP
│                           ├── OrderRepository.java     # SRP
│                           ├── EmailNotifier.java       # SRP
│                           ├── OrderReporter.java       # SRP
│                           ├── DiscountStrategy.java    # OCP
│                           ├── VipDiscount.java         # OCP
│                           ├── RegularDiscount.java     # OCP
│                           ├── NoDiscount.java          # OCP
│                           ├── OrderService.java        # DIP
│                           └── Main.java                # Demostración
├── parte-2-analisis-gof-spring/
│   ├── documento-analisis.md
│   └── evidencia/
│       └── (fragmentos de código de Spring)
└── README.md

## Ejecucion
# Moverse a la carpeta del proyecto Maven
cd parte-1-refactorizacion-solid

# Compilar el proyecto
mvn clean compile

## salida 

[DB] Orden guardada: ORD-001
[EMAIL] Enviando a vip@mail.com confirmación de orden ORD-001
[DB] Orden guardada: ORD-002
[EMAIL] Enviando a reg@mail.com confirmación de orden ORD-002
=== Reporte de Órdenes ===
  ORD-001:297.5
  ORD-002:190.0
# Ejecutar la clase principal para ver la demostración
mvn exec:java -Dexec.mainClass="com.patrones.u1.Main"
## Conclusiones
La refactorización del God Object permitió evidenciar cómo los principios SOLID mejoran la mantenibilidad y extensibilidad del código, separando responsabilidades y favoreciendo la inyección de dependencias. Por otro lado, el análisis de Spring Framework demostró que los patrones GoF son fundamentales en frameworks maduros, proporcionando soluciones probadas para problemas recurrentes como la gestión de objetos, la separación de preocupaciones y la comunicación desacoplada. Estas experiencias refuerzan la importancia de diseñar con intención, aplicando patrones y principios de manera consciente para construir software robusto y adaptable, tal como se evidencia en el diseño de Spring.