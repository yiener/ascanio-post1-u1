# ascanio-post1-u1
Post-contenido — Refactorización SOLID y análisis de patrones GoF en Spring
## Análisis de Violaciones SOLID — Parte 1


| Principio | Método/Sección afectada | Descripción de la violación |
|-----------|-------------------------|-----------------------------|
| SRP (Single Responsibility Principle) | `calculateTotal`, `applyDiscount`, `saveOrder`, `sendEmail`, `printReport` | La clase `OrderProcessor` asume múltiples responsabilidades que deberían estar en clases separadas: cálculo de impuestos, aplicación de descuentos, persistencia de órdenes, envío de correos electrónicos y generación de reportes. Esto viola el Principio de Responsabilidad Única porque existen al menos cinco razones diferentes para modificar la clase (cambiar la lógica de impuestos, agregar un nuevo descuento, modificar la forma de guardar, cambiar el formato del correo o alterar el reporte). |

| OCP(Open/Closed Principle) | `applyDiscount` (con `if/else` sobre `customerType`) | El método `applyDiscount` utiliza una estructura condicional (`if/else`) para determinar el descuento según el tipo de cliente. Para agregar un nuevo tipo de cliente (por ejemplo, "PREMIUM"), es necesario modificar el código fuente de la clase `OrderProcessor`, añadiendo un nuevo `if`. Esto viola el Principio de Abierto/Cerrado, ya que la clase no está abierta a extensión (no se puede agregar un nuevo tipo de descuento sin modificar la clase) y no está cerrada a modificación. |


| LSP (Liskov Substitution Principle) | No aplica directamente (no hay herencia) | La clase `OrderProcessor` no extiende ninguna clase ni implementa interfaces, por lo que no hay subtipos que puedan violar LSP. Sin embargo, el diseño actual no favorece la creación de subtipos reutilizables, lo que limita la extensibilidad. Al refactorizar usando interfaces (como `DiscountStrategy`), se garantizará que cualquier implementación pueda sustituir a la interfaz base sin alterar el comportamiento esperado. |

| ISP (Interface Segregation Principle) | No aplica directamente (no hay interfaces) | La clase `OrderProcessor` no implementa ninguna interfaz, por lo que no se puede decir que viole ISP. Sin embargo, si se hubieran definido interfaces, estas serían monolíticas (por ejemplo, una interfaz `OrderProcessor` con todos los métodos), obligando a las implementaciones a depender de métodos que no necesitan. La refactorización aplicará interfaces segregadas (por ejemplo, `DiscountStrategy` será una interfaz pequeña y específica), respetando ISP. |


| DIP (Dependency Inversion Principle) | Toda la clase (dependencias internas sin abstracciones) | La clase `OrderProcessor` depende directamente de implementaciones concretas: `ArrayList` para la lista de órdenes, `System.out` para la salida de consola, y no utiliza inyección de dependencias. Las dependencias son creadas internamente (por ejemplo, `new ArrayList<>()`) en lugar de ser recibidas a través del constructor o mediante interfaces. Esto viola el Principio de Inversión de Dependencias, ya que los módulos de alto nivel (la lógica de negocio) dependen de detalles de bajo nivel (implementaciones concretas) en lugar de depender de abstracciones. |