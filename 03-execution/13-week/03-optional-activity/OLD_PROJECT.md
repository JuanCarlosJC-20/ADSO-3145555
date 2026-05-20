# OLD PROJECT — Documentación Arquitectónica (Legacy)

## 1. Descripción General
Este documento describe la arquitectura legacy anterior a la modernización hacia microservicios. Se trata de una aplicación monolítica en capas (Presentación, Negocio, Persistencia) que reúne toda la funcionalidad en una sola base de código y despliegue.

## 2. Características
- Monolito en capas: simple de implementar inicialmente pero con límites de escalabilidad.
- Despliegue único y acoplamiento fuerte entre módulos.
- Facilita la trazabilidad de transacciones locales y la consistencia inmediata.

## 3. Estructura de Carpetas
```text
src/
├── Controller/
│   ├── HorarioController
│   ├── InstructorController
│   └── AmbienteController
├── Service/
│   ├── HorarioService
│   ├── InstructorService
│   └── InventoryService (ProcessInventory)
├── Repository/
│   └── PostgreSQL implementations
├── Entity/
├── DTO/
├── Utils/
└── Security/
    └── JWT/
```

## 4. Organización arquitectónica
- `Controller`: controladores que exponen endpoints HTTP y realizan validación de DTO/IDTO.
- `Service` / `IService`: contiene la lógica de negocio (reglas, transacciones).
- `Repository` / `IRepository`: acceso directo a PostgreSQL.
- `ProcessInventory`: módulo dentro del servicio de inventario para la gestión de recursos.
- `JWT`: mecanismo de autenticación integrado en la capa de seguridad.

## 5. Diagrama C4 — Nivel 1 (Contexto)
```text
System: PRJ-EDU-HORARIOS (Monolito)
Actors:
- Coordinador Académico
- Instructor
- Administrador de Ambientes
External Systems:
- PostgreSQL
- Redis
```

## 6. Diagrama C4 — Nivel 2 (Contenedores)
```text
Monolith Application
├─ Web/API (Controllers)
├─ Business Layer (Services)
├─ Persistence Layer (Repository -> PostgreSQL)
└─ Cache (Redis)
```

## 7. C4-3 Escenario: Crear horario (Componentes)
1. `HorarioController` recibe POST /horarios con `DTO`.
2. Middleware valida JWT.
3. `HorarioService.createHorario(dto)` ejecuta validaciones de negocio en `Utils`.
4. `HorarioService` llama a `HorarioRepository.save(entity)`.
5. `HorarioService` invoca `ProcessInventory` para reservar ambiente.
6. Respuesta: `HorarioDTO` con estado.

## 8. C4-4 Escenario: Crear horario (Detalle técnico)
```text
Client -> HTTP POST /horarios
-> API Layer: JWT middleware validates token
-> HorarioController.deserialize(dto) -> validate(idto)
-> HorarioService.createHorario(dto)
   -> Start DB transaction
   -> HorarioRepository.insert(horarioEntity)
   -> ProcessInventory.reserve(ambienteId, horarioRange)
   -> If ProcessInventory fails -> rollback transaction and return error
   -> Commit transaction
-> Controller returns 201 Created with HorarioDTO
```

## 9. Consideraciones académicas
- El monolito simplifica transacciones pero complica escalado y despliegue independiente.
- Recomendación: identificar bounded contexts y planificar migración incremental.
