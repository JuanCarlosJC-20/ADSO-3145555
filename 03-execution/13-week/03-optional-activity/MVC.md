# MVC — Documentación Arquitectónica (Model-View-Controller)

## 1. Descripción General
El patrón MVC separa responsabilidades entre Model (datos y lógica de negocio), View (presentación) y Controller (entrada y coordinación). En arquitecturas distribuidas el `View` suele ser un frontend independiente y los `Controller` actúan como adaptadores HTTP que exponen `DTO`/`IDTO`.

## 2. Características
- Claridad en responsabilidades y flujo de datos.
- Facilita pruebas unitarias y de integración al aislar controladores y servicios.
- Adecuado para aplicaciones con interfaz web y APIs REST.

## 3. Estructura de Carpetas
```text
frontend/            # app web o SPA (no incluido)
services/
└── horarios-service/
    ├── src/
    │   ├── Controller/    # endpoints REST
    │   ├── Model/         # Entity / Domain models
    │   ├── Service/       # casos de uso (IService/Service)
    │   ├── Repository/    # IRepository implementations
    │   ├── DTO/
    │   └── Utils/
```

## 4. Organización arquitectónica
- `Controller`: valida `IDTO`, gestiona autenticación JWT, y delega a `IService`.
- `Service` / `IService`: orquesta reglas de negocio e interacción con `ProcessInventory`.
- `Repository` / `IRepository`: persistencia en PostgreSQL.
- `DTO` / `IDTO`: contratos que definen la interfaz pública de los endpoints.

## 5. Diagrama C4 — Contenedores (texto)
```text
Browser -> API Gateway -> horarios-service.Controller
horarios-service.Controller -> HorarioService (IService)
HorarioService -> HorarioRepository (IRepository) -> PostgreSQL
HorarioService -> ProcessInventory -> inventario-service or internal component
```

## 6. C4-3 Escenario: Actualizar disponibilidad del Instructor (Componentes)
1. Instructor autenticado llama PUT /instructor/{id}/disponibilidad.
2. `Controller` valida JWT y deserializa `IDTO`.
3. `Controller` invoca `InstructorService.updateAvailability(dto)`.
4. `InstructorService` actualiza `InstructorRepository` y publica evento `DisponibilidadActualizada`.

## 7. C4-4 Escenario: Actualizar disponibilidad (Detalle técnico)
```text
Client -> Browser -> Frontend -> API Gateway -> InstructorController
InstructorController -> validate DTO/IDTO and JWT
-> InstructorService.updateAvailability(dto)
   -> InstructorRepository.update(entity)
   -> EventPublisher.publish(DisponibilidadActualizada)
-> Controller returns 200 OK
Event consumers may act on DisponibilidadActualizada (e.g., recompute asignaciones)
```

## 8. Consideraciones
- Mantener `Controller` lo más delgado posible; concentración de lógica en `Service`.
- Uso consistente de `DTO`/`IDTO` para evitar acoplamiento con `Entity`.
