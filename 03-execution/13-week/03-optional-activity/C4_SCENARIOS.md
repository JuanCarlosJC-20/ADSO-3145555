# C4 — Escenarios Detallados (Nivel 3 y Nivel 4)

Este documento consolida escenarios C4 nivel 3 (componentes) y nivel 4 (detalle técnico/flujo) para casos operativos críticos del sistema PRJ-EDU-HORARIOS. Cada escenario presenta: objetivo, componentes implicados (C4-3) y flujo técnico detallado con consideraciones de consistencia, idempotencia y manejo de errores (C4-4).

---

## Caso 1 — Crear horario (Crear una nueva sesión académica)

### Objetivo
Registrar un nuevo horario académico con asignación temporal y solicitar la reserva de ambiente si aplica.

### C4-3 (Componentes)
1. `Client` (Coordinador Académico)
2. `API Gateway` (ruteo y seguridad JWT)
3. `horarios-service.Controller` (entrada REST)
4. `HorarioService` (IService / Service)
5. `HorarioRepository` (IRepository)
6. `ProcessInventory` (a través de `inventario-service` o adaptador interno)
7. `EventPublisher` (Kafka/RabbitMQ)
8. `PostgreSQL` (persistencia primaria)

### C4-4 (Flujo técnico detallado)
```text
1. Client -> API Gateway: POST /horarios (HorarioCreateDTO) with JWT
2. API Gateway -> horarios-service.Controller: validate JWT, forward request
3. Controller -> HorarioService.create(dto): map DTO -> Domain IDTO
4. HorarioService performs domain validations (Utils)
5. HorarioService -> HorarioRepository.beginTransaction()
6. HorarioService -> HorarioRepository.insert(horarioEntity)
7. HorarioService -> EventPublisher.publish(HorarioCreado {horarioId, timeslot, ambienteReq})
8. HorarioRepository.commitTransaction()  // commit local persist
9. EventBroker delivers HorarioCreado to listeners (inventario-service)
10. inventario-service.listener(HorarioCreado) -> ProcessInventory.reserve(ambienteReq)
11a. If reserve succeeds: inventario-service -> InventoryRepository.update(status=reserved) -> EventPublisher.publish(AmbienteReservado {horarioId, ambienteId})
11b. If reserve fails: inventario-service -> EventPublisher.publish(AmbienteReservadoFallido {horarioId, reason})
12. horarios-service.listener(AmbienteReservado) -> HorarioRepository.update(status=confirmed)
13. horarios-service.listener(AmbienteReservadoFallido) -> HorarioRepository.update(status=pending) and notify Coordinator

Considerations:
- The initial DB commit ensures the horario exists even if inventory reservation is eventual.
- Use idempotent handlers for listeners: events carry correlationId and retryCount.
- If ambient reservation is critical: implement a saga coordinator to lock seat and compensate on failures.
- Apply timeouts and compensating transactions: if AmbienteReservado not received within TTL, mark horario as "pending" and schedule compensation/alert.
- Ensure secure propagation of user identity in events (minimal claims) without embedding full JWT.
```

---

## Caso 2 — Reservar ambiente (Reserva directa desde administrador de ambientes)

### Objetivo
Reservar un ambiente físico para un intervalo horario solicitado por un usuario (Administración de Ambientes) o como resultado de la creación de un horario.

### C4-3 (Componentes)
1. `Client` (Admin Ambientes / Sistema)
2. `API Gateway`
3. `ambientes-service.Controller`
4. `InventoryService` / `ProcessInventory` (core)
5. `InventoryRepository` (IRepository)
6. `EventPublisher` (Kafka/RabbitMQ)
7. `Redis` (opcional: locks / cache)
8. `PostgreSQL` (inventario)

### C4-4 (Flujo técnico detallado)
```text
Direct reservation flow (synchronous preferred for admin UI):
1. Client -> API Gateway -> ambientes-service.Controller: POST /ambientes/{id}/reserve (ReserveDTO) with JWT and role
2. Controller -> InventoryService.reserve(ambienteId, timeslot)
3. InventoryService -> Acquire distributed lock (Redis SETNX lock:ambiente:{id}:{timeslot}) with short TTL
4. InventoryService -> InventoryRepository.findAvailability(ambienteId, timeslot)
5. If available:
   -> InventoryRepository.insert(ReservationRecord)
   -> InventoryRepository.update(ambiente status if needed)
   -> Release lock
   -> EventPublisher.publish(AmbienteReservado {reservationId, ambienteId, timeslot})
   -> Controller returns 200 OK with ReservationDTO
6. If not available: Release lock -> return 409 Conflict with details

Asynchronous reservation (event-driven, from HorarioCreado):
1. inventario-service.listener(HorarioCreado) -> Try to acquire lock
2. If lock acquired: proceed as synchronous path, otherwise schedule retry with backoff
3. For long-running allocation, mark provisional reservation and confirm when allocation finalizes

Considerations:
- Use distributed locks (Redis) to avoid race conditions when multiple services attempt reservations concurrently.
- Ensure reservation creation is idempotent: use natural keys or deduplication by correlationId.
- On failure after DB write, implement compensating actions to delete provisional records and emit ReservaCancelada events.
- Publish events for audit and downstream consumers.
```

---

## Caso 3 — Asignar instructor (Asignación y notificación)

### Objetivo
Asignar un instructor a un horario, verificar disponibilidad del instructor y reservar el ambiente si no está reservado.

### C4-3 (Componentes)
1. `Client` (Coordinador Académico)
2. `API Gateway`
3. `asignacion/horarios-service.Controller` (o AsignacionController)
4. `AsignacionUseCase` / `HorarioService` (application layer)
5. `HorarioAggregate` (domain)
6. `InstructorRepository` (IRepository)
7. `HorarioRepository` (IRepository)
8. `ProcessInventory` / `inventario-service`
9. `EventPublisher`

### C4-4 (Flujo técnico detallado)
```text
1. Client -> API Gateway -> AsignacionController: POST /asignaciones (AsignacionDTO) with JWT
2. Controller -> AsignacionUseCase.execute(dto)
3. AsignacionUseCase -> InstructorRepository.findAvailability(instructorId, timeslot)
4. If instructor not available -> return 409 Conflict
5. AsignacionUseCase -> HorarioAggregate.checkAndLock(timeslot)  // domain-level guard
6. If horario exists and unassigned:
   -> HorarioAggregate.assignInstructor(instructorId)
   -> HorarioRepository.save(aggregate) // may be persisted in transaction
   -> EventPublisher.publish(InstructorAsignado {horarioId, instructorId})
7. Event consumers:
   -> inventario-service.listener(InstructorAsignado) -> ensure ambiente reserved (ProcessInventory)
   -> notification-service.listener(InstructorAsignado) -> send email/notification to instructor
8. If ProcessInventory.reserve fails for the assignment path: publish(InstructorAsignacionCompensada) and AsignacionUseCase triggers compensation to unassign instructor and persist state

Considerations:
- Validate instructor availability with eventual consistency: include last-updated timestamp and a short lock for assignment window.
- Use optimistic locking/version on aggregates to prevent concurrent assignment conflicts.
- All side effects (notifications, inventory reservation) should be event-driven to decouple and allow retries.
- Implement audit logs for assignment actions and role-based checks using JWT claims.
```

---

## Caso 4 — Gestión de inventario (Actualizar, liberar y reconciliar recursos)

### Objetivo
Gestionar el ciclo de vida de items de inventario: crear registros de equipamiento, marcar reservas, liberar recursos y reconciliar inconsistencias.

### C4-3 (Componentes)
1. `Admin` (Administrador de inventario)
2. `api-gateway` -> `inventario-service.Controller`
3. `InventoryService` / `ProcessInventory`
4. `InventoryRepository` (IRepository)
5. `EventPublisher` / `EventConsumer`
6. `PostgreSQL` (inventario)
7. `Redis` (locks, cache)
8. `AuditService` (opcional)

### C4-4 (Flujo técnico detallado)
```text
Common operations:
A) Reservar recurso (on-demand or from HorarioCreado)
   -> See Caso 2 flow (use distributed locks, idempotency, event publication)

B) Liberar recurso (after class end or cancellation)
1. inventory-service receives ReleaseRequest (API or Event)
2. InventoryService -> InventoryRepository.update(reservation status = released)
3. InventoryService -> EventPublisher.publish(ReservaLiberada {reservationId, ambienteId})
4. Consumers (horarios-service) update horario status if needed

C) Reconciliación (periodic background job)
1. Reconciler job scans ReservationRecords where status = reserved but no active horario mappings or past TTL
2. Reconciler -> attempt to reconcile by checking HorarioRepository via event-sourced logs or API
3. If orphan reservation -> InventoryRepository.markAsOrphaned and publish(ReservaOrfana)
4. Optionally trigger automated cleanup or human review workflow

Considerations:
- Idempotency: every reservation and release must be idempotent using correlationId.
- Locks and TTLs: use short TTL locks in Redis and compensating cleanups in case of stale locks.
- Sagas for multi-step operations: if reservation participates in broader workflow, implement saga to coordinate commit/compensate across services.
- Observability: publish audit and trace events (correlationId) to enable root-cause analysis.
```

---

## Notas finales (transversales)
- Todos los flujos event-driven deben incluir: correlationId, originator, timestamp, retryCount.
- Los handlers de eventos deben ser idempotentes y seguros ante re-delivery.
- JWT se usa en la capa de API Gateway y no debe circular en eventos; propagar solo claims necesarios.
- Para operaciones críticas considerar sagas coordinadas y asegurar compensaciones automatizadas y rastreables.
