# Ejercicio 04 Resuelto - Acumulación de millas y actualización del historial de nivel

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
El programa de fidelización de la aerolínea requiere consultar el comportamiento comercial del cliente y automatizar el registro de acumulación de millas o movimientos de nivel a partir de eventos definidos en la base de datos.

---

## 6. Dominios involucrados
### CUSTOMER AND LOYALTY
**Entidades:** `customer`, `loyalty_account`, `loyalty_program`, `loyalty_account_tier`, `loyalty_tier`, `miles_transaction`, `customer_category`  
**Propósito en este ejercicio:** Gestionar clientes, cuentas de fidelización, niveles y acumulación de millas.

### AIRLINE
**Entidades:** `airline`  
**Propósito en este ejercicio:** Identificar la aerolínea propietaria del programa.

### IDENTITY
**Entidades:** `person`  
**Propósito en este ejercicio:** Relacionar el cliente con la persona real.

### SALES, RESERVATION, TICKETING
**Entidades:** `reservation`, `sale`  
**Propósito en este ejercicio:** Relacionar la actividad comercial con el cliente.

---

## 7. Problema a resolver
La aerolínea necesita analizar la relación entre clientes, ventas y cuentas de fidelización, y además automatizar un movimiento posterior en la trazabilidad del programa de millas.

Por eso se plantea una solución en tres capas:
1. una consulta consolidada con `INNER JOIN`,
2. un procedimiento almacenado para registrar una transacción de millas,
3. un trigger `AFTER INSERT` sobre `miles_transaction` para evidenciar actividad en la cuenta.

---

## 8. Solución propuesta

### 8.1 Consulta resuelta con `INNER JOIN`
Se elaboró una consulta que cruza el universo de fidelización (cuenta y niveles) con el mundo real (la persona física y sus ventas).

```sql
SELECT
    c.customer_id AS cliente,
    p.first_name || ' ' || p.last_name AS persona_asociada,
    la.account_number AS cuenta_fidelizacion,
    lp.program_name AS programa,
    lt.tier_name AS nivel,
    lat.assigned_at AS fecha_asignacion_nivel,
    s.sale_code AS venta_relacionada
FROM customer c
INNER JOIN person p ON p.person_id = c.person_id
INNER JOIN loyalty_account la ON la.customer_id = c.customer_id
INNER JOIN loyalty_program lp ON lp.loyalty_program_id = la.loyalty_program_id
INNER JOIN loyalty_account_tier lat ON lat.loyalty_account_id = la.loyalty_account_id
INNER JOIN loyalty_tier lt ON lt.loyalty_tier_id = lat.loyalty_tier_id
INNER JOIN reservation r ON r.customer_id = c.customer_id
INNER JOIN sale s ON s.reservation_id = r.reservation_id
ORDER BY lat.assigned_at DESC;
```

---

## 9. Procedimiento almacenado resuelto

### 9.1 Objetivo
Permitir a otros módulos del sistema inyectar movimientos de millas (positivos o negativos) sin conocer la complejidad de las reglas de inserción subyacentes.

### 9.2 Decisión técnica
El procedimiento `sp_add_miles_transaction` toma los datos básicos y asegura que el saldo acumulado en la vida del programa quede registrado en `miles_transaction`.

### 9.3 Script del procedimiento
```sql
DROP PROCEDURE IF EXISTS sp_add_miles_transaction(uuid, varchar, integer, timestamptz, varchar);

CREATE OR REPLACE PROCEDURE sp_add_miles_transaction(
    p_loyalty_account_id uuid,
    p_transaction_type varchar(20),
    p_miles_amount integer,
    p_transaction_date timestamptz,
    p_reference_note varchar(255)
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO miles_transaction (
        loyalty_account_id,
        transaction_type,
        miles_delta,
        occurred_at,
        notes
    )
    VALUES (
        p_loyalty_account_id,
        p_transaction_type,
        p_miles_delta,
        now(),
        p_reference_code,
        p_notes
    );
END;
$$;
```

---

## 10. Trigger resuelto

### 10.1 Decisión técnica
Se configuró un trigger `AFTER INSERT ON miles_transaction` para asegurar la trazabilidad. Cada vez que se acumulan o gastan millas, es mandatorio marcar la cuenta de fidelidad (`loyalty_account`) como actualizada.

### 10.2 Lógica implementada
- Captura cada nueva transacción de millas insertada.
- Actualiza la fecha `updated_at` de la `loyalty_account` afectada por la transacción.

### 10.3 Script del trigger
```sql
DROP TRIGGER IF EXISTS trg_ai_miles_transaction_update_account ON miles_transaction;
DROP FUNCTION IF EXISTS fn_ai_miles_transaction_update_account();

CREATE OR REPLACE FUNCTION fn_ai_miles_transaction_update_account()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE loyalty_account
    SET updated_at = now()
    WHERE loyalty_account_id = NEW.loyalty_account_id;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_ai_miles_transaction_update_account
AFTER INSERT ON miles_transaction
FOR EACH ROW
EXECUTE FUNCTION fn_ai_miles_transaction_update_account();
```

---

## 11. Script de demostración del funcionamiento

```sql
DO $$
DECLARE
    v_loyalty_account_id uuid;
BEGIN
    -- 1. Buscar una cuenta de fidelización existente
    SELECT loyalty_account_id
    INTO v_loyalty_account_id
    FROM loyalty_account
    LIMIT 1;

    IF v_loyalty_account_id IS NULL THEN
        RAISE EXCEPTION 'No se encontró una cuenta de fidelización para la prueba.';
    END IF;

    -- 2. Invocar el procedimiento simulando una acumulación de millas (dispara el trigger)
    CALL sp_add_miles_transaction(
        v_loyalty_account_id,
        'EARN', -- Tipo de transacción (ej. Acumulación)
        500,       -- Cantidad de millas
        'DEMO-PROMO-2024',
        'Acumulación por vuelo transatlántico (Demo)'
    );

    RAISE NOTICE 'Transacción de millas agregada exitosamente a la cuenta %', v_loyalty_account_id;
END;
$$;

-- 3. Verificación de la transacción y el efecto del trigger (updated_at)
SELECT 
    la.account_number,
    la.updated_at AS cuenta_actualizada,
    mt.transaction_type,
    mt.miles_delta,
    mt.notes
FROM loyalty_account la
INNER JOIN miles_transaction mt ON mt.loyalty_account_id = la.loyalty_account_id
WHERE mt.notes = 'Acumulación por vuelo transatlántico (Demo)'
ORDER BY mt.created_at DESC
LIMIT 1;
```

---

## 12. Validación final
La solución es válida porque:
- Almacena el número de millas a acumular y el motivo de la operación, respetando la estructura real.

---

## 13. Archivo SQL relacionado
- `scripts_sql/ejercicio_04_setup.sql`
- `scripts_sql/ejercicio_04_demo.sql`
