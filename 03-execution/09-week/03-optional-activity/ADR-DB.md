# ADRs – Decisiones de arquitectura sobre el modelo de datos

Este documento recopila decisiones arquitectónicas relacionadas con ajustes en el esquema de base de datos. El objetivo es dejar evidencia de qué se modificó, por qué se tomó cada decisión, qué opciones se evaluaron y cuáles son sus efectos esperados.

---

## ADR 001 – Simplificación de la relación en `seat_assignment`

**Título:** Eliminación de una clave foránea compuesta innecesaria

### Decisión

Se retirará la clave foránea compuesta basada en los campos:

```sql
(ticket_segment_id, flight_segment_id)
```

dentro de la tabla `seat_assignment`.

### Contexto y justificación

El campo `ticket_segment_id` ya identifica de forma única el registro correspondiente en `ticket_segment`, por lo que incluir también `flight_segment_id` en la relación genera una validación redundante. Mantener esta FK compuesta aumenta la complejidad del modelo sin aportar una mejora real en la integridad de los datos.

### Alternativas consideradas

- Conservar la clave foránea compuesta actual.
- Redefinir `ticket_segment` para manejar una clave compuesta real.
- Mantener únicamente la relación directa mediante `ticket_segment_id`.

### Consecuencias

- El modelo queda más claro y fácil de mantener.
- Se reduce la complejidad en consultas y relaciones.
- Puede mejorar el rendimiento al evitar validaciones innecesarias.
- La integridad se mantiene mediante la clave primaria existente en `ticket_segment`.

---

## ADR 002 – Incorporación de FK directa para `flight_segment_id`

**Título:** Agregar restricción referencial hacia `flight_segment`

### Decisión

Se agregará una clave foránea desde `seat_assignment.flight_segment_id` hacia la tabla `flight_segment`.

### Contexto y justificación

Se identificó que `flight_segment_id` podía almacenar valores sin una restricción que comprobara su existencia en `flight_segment`. Esto permite la creación de registros inconsistentes, por ejemplo, asignaciones de asiento asociadas a segmentos de vuelo inexistentes.

### Alternativas consideradas

- Realizar la validación únicamente desde el backend.
- Mantener el campo sin restricción referencial.
- Agregar una FK directa en la base de datos.

### Consecuencias

- Se refuerza la integridad referencial.
- Se evita que existan asignaciones vinculadas a vuelos inválidos.
- La base de datos asume parte de la validación estructural del modelo.

---

## ADR 003 – Validación entre asiento y avión del vuelo

**Título:** Control de coherencia entre el asiento asignado y la aeronave del vuelo

### Decisión

Se implementará una validación para asegurar que el asiento asignado pertenezca al mismo avión utilizado por el segmento de vuelo. Esta validación podrá realizarse mediante un trigger en la base de datos o mediante lógica controlada desde el backend.

### Contexto y justificación

Aunque un asiento y un segmento de vuelo puedan existir por separado, es necesario verificar que ambos correspondan a la misma aeronave. Sin esta regla, el sistema podría permitir asignar un asiento de un avión diferente al utilizado en el vuelo, generando inconsistencias críticas en la operación.

### Alternativas consideradas

- No aplicar validación adicional.
- Validar exclusivamente desde el backend.
- Implementar un trigger o regla de validación en la base de datos.

### Consecuencias

- Se mejora la consistencia del modelo.
- Se evita la asignación de asientos inválidos.
- Aumenta ligeramente la complejidad técnica.
- Será necesario definir con claridad si la validación vivirá en base de datos, backend o ambos.

---

## ADR 004 – Manejo de unicidad en campos opcionales

**Título:** Uso de índices únicos parciales para columnas que permiten `NULL`

### Decisión

Se reemplazarán algunas restricciones `UNIQUE` tradicionales por índices únicos condicionales, aplicados únicamente cuando el valor no sea `NULL`.

Ejemplo:

```sql
CREATE UNIQUE INDEX idx_unique_optional_field
ON table_name(optional_field)
WHERE optional_field IS NOT NULL;
```

### Contexto y justificación

En PostgreSQL, una restricción `UNIQUE` permite múltiples valores `NULL`, ya que `NULL` representa ausencia de valor y no se considera igual a otro `NULL`. En campos opcionales, esto puede ser correcto, pero también puede causar comportamientos no deseados si se espera controlar la unicidad solo cuando el dato está presente.

### Alternativas consideradas

- Mantener las restricciones `UNIQUE` actuales.
- Cambiar los campos opcionales a `NOT NULL`.
- Usar índices únicos parciales con condición `WHERE IS NOT NULL`.

### Consecuencias

- Se obtiene mayor control sobre las reglas de unicidad.
- Se mantiene la flexibilidad de campos opcionales.
- Se mejora la calidad de los datos almacenados.
- El diseño queda más explícito frente al comportamiento de PostgreSQL con valores `NULL`.

---

## ADR 005 – Ajuste del almacenamiento de contraseñas

**Título:** Mejorar definición y validación de `password_hash`

### Decisión

Se modificará la columna `password_hash` para utilizar el tipo de dato `TEXT` y se agregará una validación de longitud mínima.

### Contexto y justificación

El uso de `varchar(255)` puede resultar limitado para ciertos algoritmos o configuraciones de hash de contraseñas. Además, no existía una validación mínima que ayudara a garantizar que el valor almacenado tenga una estructura razonable para representar un hash.

### Alternativas consideradas

- Mantener `varchar(255)`.
- Validar el contenido únicamente desde el backend.
- Cambiar la columna a `TEXT` y agregar validaciones básicas en base de datos.

### Consecuencias

- Se gana flexibilidad frente a distintos algoritmos de hashing.
- Se mejora la seguridad del modelo al exigir una longitud mínima.
- Se evita depender completamente del backend para validar el campo.
- El esquema queda preparado para posibles cambios futuros en la estrategia de seguridad.

---

## ADR 006 – Normalización de valores categóricos

**Título:** Reemplazo de `CHECK` por tablas catálogo para valores tipo enumeración

### Decisión

Se crearán tablas catálogo para manejar valores categóricos como `transaction_type`, en lugar de representar estos valores únicamente mediante restricciones `CHECK`.

### Contexto y justificación

Las restricciones `CHECK` son útiles para reglas simples, pero pueden volverse poco prácticas cuando los valores deben crecer, reutilizarse o administrarse desde otras partes del sistema. Una tabla catálogo permite centralizar estos valores y relacionarlos mediante claves foráneas.

### Alternativas consideradas

- Mantener las restricciones `CHECK`.
- Usar tipos `ENUM` nativos de PostgreSQL.
- Crear tablas catálogo reutilizables.

### Consecuencias

- El modelo será más flexible y extensible.
- Los valores podrán reutilizarse en distintas tablas.
- Se facilita el mantenimiento cuando aparezcan nuevos tipos.
- Aumenta el número de tablas del modelo, aunque con una estructura más normalizada.

---

## ADR 007 – Actualización automática de `updated_at`

**Título:** Automatizar el mantenimiento del campo de actualización

### Decisión

Se implementarán triggers para actualizar automáticamente el campo `updated_at` cada vez que un registro sea modificado.

### Contexto y justificación

El campo `updated_at` permite conocer cuándo fue la última modificación de un registro. Si este valor depende del backend o de actualizaciones manuales, puede quedar desactualizado y afectar la trazabilidad de los datos.

### Alternativas consideradas

- Actualizar `updated_at` manualmente desde el backend.
- No utilizar el campo `updated_at`.
- Automatizar la actualización mediante triggers en la base de datos.

### Consecuencias

- Se mejora la trazabilidad de los cambios.
- Se reduce el riesgo de olvidar actualizar el campo desde la aplicación.
- La auditoría básica de datos se vuelve más confiable.
- Existe un pequeño costo adicional por la ejecución de triggers en operaciones `UPDATE`.

---

# Resumen general

Las decisiones registradas en este documento buscan mejorar el diseño de la base de datos mediante:

- eliminación de relaciones redundantes;
- refuerzo de integridad referencial;
- prevención de inconsistencias entre vuelos, aviones y asientos;
- mejor manejo de campos opcionales únicos;
- almacenamiento más flexible y seguro para hashes de contraseñas;
- normalización de valores categóricos;
- automatización de campos de trazabilidad.

En conjunto, estos cambios hacen que el modelo sea más consistente, mantenible y preparado para crecer.