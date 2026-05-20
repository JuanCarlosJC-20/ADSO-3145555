# BY MODULE — Documentación Arquitectónica por Módulos

## 1. Descripción General
La arquitectura por módulos organiza el sistema por bounded contexts: `horarios`, `usuarios`, `ambientes`, `inventario` y `seguridad`. Cada módulo puede ser desplegado como microservicio independiente, con su propio ciclo de vida, repositorio y API.

## 2. Características
- Aislamiento fuerte entre módulos y responsabilidades.
- Facilidad para asignar equipos por módulo y desplegar independientemente.
- Permite escalado selectivo y versiones desacopladas.

## 3. Estructura de Carpetas
```text
services/
├── horarios-service/
│   ├── src/
│   │   ├── Controller/
│   │   ├── Service/
│   │   ├── Repository/
│   │   ├── Entity/
│   │   ├── DTO/
│   │   └── Utils/
│   └── tests/
├── usuarios-service/
├── ambientes-service/
├── inventario-service/
└── platform/
    ├── auth-service/   # JWT issuer
    ├── api-gateway/
    └── shared-utils/
```

## 4. Organización arquitectónica
- Cada módulo implementa `Entity`, `IRepository`, `IService` y `Controller`.
- `ProcessInventory` se implementa dentro de `inventario-service` y se comunica vía eventos.
- Mensajería (Kafka/RabbitMQ) conecta servicios para eventos de integración.
- Redis se usa para cache compartido y sesiones, API Gateway expone endpoints consolidando rutas y seguridad.

## 5. Diagrama C4 — Nivel 2 (Contenedores por Módulo)
```text
API Gateway
├─ horarios-service (REST)
├─ usuarios-service (REST)
├─ ambientes-service (REST)
├─ inventario-service (REST + EventConsumer)
└─ auth-service (JWT issuer)

Infra:
- PostgreSQL (per-service or shared schema)
- Kafka/RabbitMQ (event bus)
- Redis (cache/session)
```

## 6. C4-3 Escenario: Reservar ambiente para un horario (Componentes)
1. `API Gateway` recibe POST /horarios y enruta a `horarios-service.Controller`.
2. `horarios-service.Controller` valida `IDTO` y emite `HorarioCreado` a Kafka.
3. `inventario-service` (listener) procesa `HorarioCreado` y ejecuta `ProcessInventory.reserve`.
4. `inventario-service` actualiza su `InventoryRepository` y publica `AmbienteReservado`.
5. `horarios-service` recibe `AmbienteReservado` y actualiza estado del horario.

## 7. C4-4 Escenario: Reservar ambiente (Flujo técnico detallado)
```text
Client -> API Gateway -> horarios-service.Controller
Controller -> HorarioService.create(dto)
HorarioService -> HorarioRepository.save(entity)
HorarioService -> EventPublisher.publish(HorarioCreado)
Kafka -> inventario-service.listener(HorarioCreado)
inventario-service -> ProcessInventory.reserve(ambienteId, timeslot)
inventario-service -> InventoryRepository.update(status)
inventario-service -> EventPublisher.publish(AmbienteReservado)
HorarioService.listener(AmbienteReservado) -> HorarioRepository.update(status)
```

## 8. Notas y ventajas
- Permite gestionar fallos y reintentos en `inventario-service` sin impactar directamente a `horarios-service`.
- Facilita auditoría y rastreo mediante trazabilidad de eventos.
