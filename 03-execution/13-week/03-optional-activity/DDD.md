# DDD — Documentación Arquitectónica (Domain-Driven Design)

## 1. Descripción General
El enfoque DDD se utiliza para modelar de forma precisa los dominios del sistema: Horarios, Usuarios, Ambientes e Inventario. DDD promueve bounded contexts, lenguaje ubicuo y separación entre dominio, capa de aplicación e infraestructura.

## 2. Características
- Modelos de dominio ricos (entidades y agregados) que encapsulan invariantes.
- Repositorios (`IRepository`) y servicios de dominio (`IService`) que implementan reglas complejas.
- Integración mediante eventos de dominio para mantener consistencia eventual entre contextos.

## 3. Estructura de Carpetas
```text
src/
├── domain/
│   ├── horarios/
│   │   ├── entity/
│   │   ├── aggregate/
│   │   ├── repository/   # IRepository interfaces
│   │   └── service/      # domain services (IService)
│   ├── inventario/
│   └── usuarios/
├── application/
│   └── usecases/         # Application services que usan domain services
├── infrastructure/
│   ├── persistence/      # Repository implementations
│   ├── messaging/        # Kafka/RabbitMQ adapters
│   └── security/         # JWT infra
└── interfaces/
    └── rest/             # Controllers, DTOs, IDTOs
```

## 4. Organización arquitectónica
- `Domain`: entidades, agregados y servicios de dominio que representan reglas de negocio.
- `Application`: casos de uso que coordinan repositorios y servicios de dominio.
- `Infrastructure`: adaptadores técnicos (PostgreSQL, Redis, mensajería, JWT).
- `Interfaces`: adaptadores de entrada (Controllers) y salida (listeners, API clients).

## 5. Diagrama C4 — Contenedores (DDD-friendly)
```text
Clients -> API Gateway -> interfaces/rest (Controllers)
interfaces/rest -> application/usecases -> domain services
domain services -> repositories (IRepository) -> infrastructure.persistence -> PostgreSQL
domain events -> messaging (Kafka/RabbitMQ) -> other bounded contexts
```

## 6. C4-3 Escenario: Asignación de instructor a horario (Componentes)
1. Coordinador solicita creación de asignación `POST /asignaciones` al `AsignacionController`.
2. `AsignacionController` valida `IDTO` y derechos mediante JWT.
3. `AsignacionUseCase` (en `application`) invoca `HorarioAggregate` para verificar disponibilidad.
4. `AsignacionUseCase` consulta `InstructorRepository` (IRepository) y solicita al `ProcessInventory` la reserva del ambiente si aplica.
5. `AsignacionUseCase` persiste cambios a través de `HorarioRepository` y publica evento `InstructorAsignado`.

## 7. C4-4 Escenario: Asignación de instructor (Detalle técnico)
```text
Client -> API Gateway -> AsignacionController
Controller -> AsignacionUseCase.execute(dto)
AsignacionUseCase:
  -> HorarioAggregate.checkAvailability(timeslot)
  -> InstructorRepository.findById(instructorId)
  -> If available: HorarioAggregate.assign(instructorId)
  -> HorarioRepository.save(aggregate)
  -> DomainEventPublisher.publish(InstructorAsignado)
DomainEvent (InstructorAsignado) -> inventario-service.listener -> ProcessInventory.reserve(ambienteId, timeslot)
If reserve fails -> compensating action: publish(InstructorAsignacionCompensada) and update HorarioRepository
```

## 8. Consideraciones sobre consistencia y sagas
- Se recomienda modelar flujos que involucren múltiples bounded contexts con sagas o workflows coordinados por eventos.
- `ProcessInventory` debe exponer idempotencia y manejo de reintentos para evitar reservaciones duplicadas.

## 9. Componentes requeridos (mapping)
- `Entity`: Horario, Instructor, Ambiente, InventarioItem
- `IRepository`: HorarioRepository, InstructorRepository, InventarioRepository
- `IService` / `Service`: HorarioService, InventoryService (ProcessInventory), AuthService
- `Controller`: HorariosController, UsuariosController, AmbientesController
- `DTO` / `IDTO`: HorarioDTO/IDTO, InstructorDTO/IDTO, AmbienteDTO/IDTO
- `Utils`: validación, parseo de rangos horarios, utilidades de fecha
- `JWT`: proveedor y middleware de autorización
- `ProcessInventory`: orquestador de reservas y liberaciones de recursos
