# PRJ-EDU-HORARIOS — Documentación Técnica (Visión General)

## 1. Descripción General
PRJ-EDU-HORARIOS es un sistema distribuido para la gestión de horarios académicos, instructores, ambientes e inventario, con soporte de autenticación y seguridad basada en JWT. Está diseñado para operar bajo principios de arquitectura modular y microservicios, empleando PostgreSQL para persistencia relacional, Kafka o RabbitMQ para mensajería asíncrona, Redis para caching y sesión, y un API Gateway para orquestar el acceso externo.

El propósito de esta documentación es ofrecer un entendimiento inicial del sistema, describir la organización arquitectónica, presentar diagramas C4 en formato textual y detallar escenarios operativos (niveles C4-3 y C4-4). Todo el contenido está orientado a documentación técnica académica y profesional.

## 2. Características
- Arquitectura basada en bounded contexts y separación de responsabilidades.
- Soporte para alta disponibilidad y escalado horizontal mediante microservicios.
- Integración mediante bus de eventos para consistencia eventual y desacoplamiento.
- Seguridad centralizada con JWT y políticas de autorización por rol.

## 3. Componentes clave
- `Entity`: modelos de dominio (Horario, Instructor, Ambiente, InventarioItem).
- `IRepository`: interfaces de acceso a datos.
- `IService` / `Service`: lógica de negocio y casos de uso.
- `Controller`: adaptadores REST en cada servicio.
- `DTO` / `IDTO`: contratos de entrada/salida.
- `Utils`: utilidades transversales (validaciones, manejo de fechas, conversiones).
- `JWT`: emisión, validación y middleware para protección de endpoints.
- `ProcessInventory`: servicio/componente encargado de la reserva y gestión del inventario físico.

## 4. Usuarios y Roles
- Coordinador Académico — Planificación y validación de horarios.
- Instructor — Consulta y actualización de disponibilidad.
- Administrador de Ambientes — Gestión de recursos físicos y su disponibilidad.

## 5. Tecnologías de referencia
- Microservicios, PostgreSQL, Kafka/RabbitMQ, Redis, API Gateway, JWT.

## 6. Siguientes entregables
- Documentación de las 4 arquitecturas solicitadas: `OLD_PROJECT.md`, `BY_MODULE.md`, `MVC.md`, `DDD.md` (ubicados en esta misma carpeta).
- Detalle de escenarios C4 nivel 3 y nivel 4 en los respectivos archivos arquitectónicos.
