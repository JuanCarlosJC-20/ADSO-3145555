# Ejercicio 05 Resuelto - Mantenimiento de aeronaves y habilitación operativa

# Modelo de datos base del sistema

## 1. Descripción general del modelo
El modelo de datos corresponde a un sistema integral de aerolínea, diseñado para soportar de forma relacional los procesos principales del negocio: gestión geográfica, identidad de personas, seguridad, clientes, fidelización, aeropuertos, aeronaves, operación de vuelos, reservas, tiquetes, abordaje, pagos y facturación.

Se trata de un modelo amplio y normalizado, en el que las entidades están separadas por dominios funcionales y conectadas mediante llaves foráneas para garantizar trazabilidad, integridad y consistencia en todo el flujo operativo y comercial.

---

## 2. Resumen previo del análisis realizado
Como base de trabajo, previamente se identificó y organizó el script en dominios funcionales. A partir de esa revisión, se determinó que el modelo no corresponde a un caso pequeño o aislado, sino a una solución empresarial con múltiples áreas del negocio conectadas entre sí.

También se verificó que:
- el modelo contiene más de 60 entidades,
- las relaciones entre tablas siguen una estructura consistente,
- existen restricciones de integridad mediante `PRIMARY KEY`, `FOREIGN KEY`, `UNIQUE` y `CHECK`,
- el diseño soporta trazabilidad end-to-end desde la reserva hasta el pago, abordaje y facturación.

---

## 3. Dominios del modelo y propósito general

### GEOGRAPHY AND REFERENCE DATA
**Entidades:** `time_zone`, `continent`, `country`, `state_province`, `city`, `district`, `address`, `currency`  
**Resumen:** Centraliza información geográfica y de referencia para ubicar aeropuertos, personas, proveedores y definir monedas operativas del sistema.

### AIRLINE
**Entidades:** `airline`  
**Resumen:** Representa la aerolínea operadora del sistema, incluyendo sus códigos y país base.

### IDENTITY
**Entidades:** `person_type`, `document_type`, `contact_type`, `person`, `person_document`, `person_contact`  
**Resumen:** Permite modelar la identidad de las personas, sus documentos y medios de contacto.

### SECURITY
**Entidades:** `user_status`, `security_role`, `security_permission`, `user_account`, `user_role`, `role_permission`  
**Resumen:** Administra autenticación, autorización y control de acceso al sistema.

### CUSTOMER AND LOYALTY
**Entidades:** `customer_category`, `benefit_type`, `loyalty_program`, `loyalty_tier`, `customer`, `loyalty_account`, `loyalty_account_tier`, `miles_transaction`, `customer_benefit`  
**Resumen:** Gestiona clientes, programas de fidelización, acumulación de millas, beneficios y niveles.

### AIRPORT
**Entidades:** `airport`, `terminal`, `boarding_gate`, `runway`, `airport_regulation`  
**Resumen:** Modela la infraestructura aeroportuaria y las condiciones regulatorias asociadas a cada aeropuerto.

### AIRCRAFT
**Entidades:** `aircraft_manufacturer`, `aircraft_model`, `cabin_class`, `aircraft`, `aircraft_cabin`, `aircraft_seat`, `maintenance_provider`, `maintenance_type`, `maintenance_event`  
**Resumen:** Gestiona aeronaves, fabricantes, configuración interna y procesos de mantenimiento.

### FLIGHT OPERATIONS
**Entidades:** `flight_status`, `delay_reason_type`, `flight`, `flight_segment`, `flight_delay`  
**Resumen:** Controla la operación de vuelos, sus segmentos, estados y retrasos.

### SALES, RESERVATION, TICKETING
**Entidades:** `reservation_status`, `sale_channel`, `fare_class`, `fare`, `ticket_status`, `reservation`, `reservation_passenger`, `sale`, `ticket`, `ticket_segment`, `seat_assignment`, `baggage`  
**Resumen:** Gestiona el flujo comercial principal: reserva, pasajero, venta, emisión de tiquetes, asignación de asiento y equipaje.

### BOARDING
**Entidades:** `boarding_group`, `check_in_status`, `check_in`, `boarding_pass`, `boarding_validation`  
**Resumen:** Soporta el proceso de check-in, emisión de pase de abordar y validación final de embarque.

### PAYMENT
**Entidades:** `payment_status`, `payment_method`, `payment`, `payment_transaction`, `refund`  
**Resumen:** Administra pagos, transacciones y devoluciones asociadas a las ventas.

### BILLING
**Entidades:** `tax`, `exchange_rate`, `invoice_status`, `invoice`, `invoice_line`  
**Resumen:** Gestiona impuestos, tasas de cambio, facturas y detalle facturable.

---

## 4. Restricción general para todos los ejercicios
Todos los ejercicios se resuelven respetando estrictamente el modelo entregado.

No se cambia:
- ningún atributo existente,
- nombres de tablas o columnas,
- relaciones del modelo,
- ni la estructura general del script base.

---

## 5. Contexto del ejercicio
El área técnica desea consultar el historial de mantenimiento de las aeronaves y automatizar efectos posteriores cuando un evento de mantenimiento cambie de estado o se complete.

---

## 6. Dominios involucrados
### AIRCRAFT
**Entidades:** `aircraft`, `aircraft_model`, `aircraft_manufacturer`, `maintenance_event`, `maintenance_type`, `maintenance_provider`  
**Propósito en este ejercicio:** Gestionar aeronaves, modelo, fabricante, tipos de mantenimiento y proveedores.

### AIRLINE
**Entidades:** `airline`  
**Propósito en este ejercicio:** Relacionar cada aeronave con la aerolínea operadora.

### GEOGRAPHY AND REFERENCE DATA
**Entidades:** `address`  
**Propósito en este ejercicio:** Relacionar la ubicación del proveedor de mantenimiento, cuando exista.

---

## 7. Problema a resolver
La organización necesita una visión consolidada de eventos de mantenimiento y un mecanismo automatizado que evidencie cambios posteriores cuando se registra o actualiza un mantenimiento técnico, con el fin de informar a sistemas dependientes (como logística) que el avión sufrió una intervención.

Por eso se plantea una solución en tres capas:
1. una consulta consolidada con `INNER JOIN`,
2. un procedimiento almacenado para registrar de manera unificada los mantenimientos.
3. un trigger `AFTER INSERT OR UPDATE` sobre `maintenance_event` para reaccionar ante los cambios de estado.

---

## 8. Solución propuesta

### 8.1 Consulta resuelta con `INNER JOIN`
La consulta reúne todas las entidades involucradas en un evento de mantenimiento, desde el equipo físico (la aeronave y el modelo) hasta el proveedor y el tipo de intervención técnica.

```sql
SELECT
    a.registration_number AS matricula,
    al.airline_name AS aerolinea,
    am.model_name AS modelo,
    mfr.manufacturer_name AS fabricante,
    mt.type_name AS tipo_mantenimiento,
    mp.provider_name AS proveedor,
    me.status_code AS estado_evento,
    me.started_at AS fecha_inicio,
    me.completed_at AS fecha_finalizacion
FROM aircraft a
INNER JOIN airline al ON al.airline_id = a.airline_id
INNER JOIN aircraft_model am ON am.aircraft_model_id = a.aircraft_model_id
INNER JOIN aircraft_manufacturer mfr ON mfr.aircraft_manufacturer_id = am.aircraft_manufacturer_id
INNER JOIN maintenance_event me ON me.aircraft_id = a.aircraft_id
INNER JOIN maintenance_type mt ON mt.maintenance_type_id = me.maintenance_type_id
INNER JOIN maintenance_provider mp ON mp.maintenance_provider_id = me.maintenance_provider_id
ORDER BY me.started_at DESC;
```

---

## 9. Procedimiento almacenado resuelto

### 9.1 Objetivo
Garantizar que todo nuevo registro de mantenimiento entre validado, reduciendo anomalías operativas.

### 9.2 Decisión técnica
El procedimiento inserta un evento de mantenimiento pidiendo como base el estado del código y obligando a referenciar la aeronave y el tipo de trabajo. 

### 9.3 Script del procedimiento
```sql
DROP PROCEDURE IF EXISTS sp_register_maintenance_event(uuid, uuid, uuid, varchar, timestamptz, text);

CREATE OR REPLACE PROCEDURE sp_register_maintenance_event(
    p_aircraft_id uuid,
    p_maintenance_type_id uuid,
    p_provider_id uuid,
    p_status_code varchar(20),
    p_started_at timestamptz,
    p_maintenance_notes text
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO maintenance_event (
        aircraft_id,
        maintenance_type_id,
        maintenance_provider_id,
        status_code,
        started_at,
        notes
    )
    VALUES (
        p_aircraft_id,
        p_maintenance_type_id,
        p_maintenance_provider_id,
        p_status_code,
        p_started_at,
        p_notes
    );
END;
$$;
```

---

## 10. Trigger resuelto

### 10.1 Decisión técnica
El diseño elegido es un trigger `AFTER INSERT OR UPDATE ON maintenance_event`. Cuando cambia la historia técnica de un avión, la tabla principal `aircraft` debe reflejar evidencia de actualización.

### 10.2 Lógica implementada
- Captura inserciones de nuevos mantenimientos o cambios de estado (por ejemplo de 'EN PROGRESO' a 'COMPLETADO').
- Toca el registro base `aircraft` modificando su `updated_at`.

### 10.3 Script del trigger
```sql
DROP TRIGGER IF EXISTS trg_aiu_maintenance_event_update_aircraft ON maintenance_event;
DROP FUNCTION IF EXISTS fn_aiu_maintenance_event_update_aircraft();

CREATE OR REPLACE FUNCTION fn_aiu_maintenance_event_update_aircraft()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE aircraft
    SET updated_at = now()
    WHERE aircraft_id = NEW.aircraft_id;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_aiu_maintenance_event_update_aircraft
AFTER INSERT OR UPDATE ON maintenance_event
FOR EACH ROW
EXECUTE FUNCTION fn_aiu_maintenance_event_update_aircraft();
```

---

## 11. Script de demostración del funcionamiento

```sql
DO $$
DECLARE
    v_aircraft_id uuid;
    v_maintenance_type_id uuid;
    v_provider_id uuid;
BEGIN
    -- 1. Buscar una aeronave
    SELECT aircraft_id INTO v_aircraft_id FROM aircraft LIMIT 1;

    -- 2. Buscar tipo de mantenimiento
    SELECT maintenance_type_id INTO v_maintenance_type_id FROM maintenance_type LIMIT 1;

    -- 3. Buscar proveedor
    SELECT maintenance_provider_id INTO v_provider_id FROM maintenance_provider LIMIT 1;

    IF v_aircraft_id IS NULL OR v_maintenance_type_id IS NULL OR v_provider_id IS NULL THEN
        RAISE EXCEPTION 'Faltan datos base (aeronave, tipo o proveedor) para la prueba.';
    END IF;

    -- 4. Invocar el procedimiento (dispara el trigger)
    CALL sp_register_maintenance_event(
        v_aircraft_id,
        v_maintenance_type_id,
        v_provider_id,
        'PLANNED',
        now(),
        'Inspección de rutina - Demo'
    );

    RAISE NOTICE 'Evento de mantenimiento registrado para la aeronave %', v_aircraft_id;
END;
$$;

-- 5. Verificación de la trazabilidad en la aeronave
SELECT 
    a.registration_number,
    a.updated_at AS aeronave_actualizada,
    me.status_code,
    me.started_at,
    me.notes
FROM aircraft a
INNER JOIN maintenance_event me ON me.aircraft_id = a.aircraft_id
WHERE me.notes = 'Inspección de rutina - Demo'
ORDER BY me.created_at DESC
LIMIT 1;
```

---

## 12. Validación final
La solución es válida porque:
- Resuelve la auditoría de activos críticos como aeronaves, a través de disparadores automáticos (`AFTER INSERT OR UPDATE`).
- Las uniones de tablas muestran correctamente toda la historia y procedencia de la parte intervenida.

---

## 13. Archivo SQL relacionado
- `scripts_sql/ejercicio_05_setup.sql`
- `scripts_sql/ejercicio_05_demo.sql`
