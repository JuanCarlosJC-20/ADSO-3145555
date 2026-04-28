# Ejercicio 02 Resuelto - Control de pagos y trazabilidad de transacciones financieras

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
El área financiera necesita auditar el estado de los pagos de una venta, identificar sus transacciones asociadas y controlar la generación de devoluciones cuando se registre una reversión o evento posterior.

---

## 6. Dominios involucrados
### SALES, RESERVATION, TICKETING
**Entidades:** `sale`, `reservation`  
**Propósito en este ejercicio:** Relacionar la venta con el contexto comercial de la reserva.

### PAYMENT
**Entidades:** `payment`, `payment_status`, `payment_method`, `payment_transaction`, `refund`  
**Propósito en este ejercicio:** Gestionar pagos, transacciones y devoluciones automatizadas.

### BILLING
**Entidades:** `invoice`  
**Propósito en este ejercicio:** Relacionar el pago con el documento facturable asociado (si lo hubiera en una consulta futura).

### GEOGRAPHY AND REFERENCE DATA
**Entidades:** `currency`  
**Propósito en este ejercicio:** Normalizar la moneda usada en la venta y el pago.

---

## 7. Problema a resolver
La organización requiere una vista consolidada del ciclo de pago de una venta y necesita automatizar un efecto posterior sobre las devoluciones a partir de un evento registrado en el flujo de pagos. El proceso de devoluciones (refund) muchas veces no queda trazado con su respectiva transacción financiera, por lo que es vital automatizarlo.

Por eso se plantea una solución en tres capas:
1. una consulta consolidada con `INNER JOIN`,
2. un procedimiento almacenado que registre de manera segura la transacción financiera.
3. un trigger `AFTER INSERT` sobre `payment_transaction` que cree el `refund` en caso de corresponder.

---

## 8. Solución propuesta

### 8.1 Consulta resuelta con `INNER JOIN`
Se estructuró una consulta que relaciona toda la información financiera de un pago desde la venta y reserva originales hasta el detalle de cada transacción.

```sql
SELECT
    s.sale_code AS codigo_venta,
    r.reservation_code AS codigo_reserva,
    p.payment_reference AS referencia_pago,
    ps.status_name AS estado_pago,
    pm.method_name AS metodo_pago,
    pt.transaction_reference AS referencia_transaccion,
    pt.transaction_type AS tipo_transaccion,
    pt.transaction_amount AS monto_procesado,
    curr.iso_currency_code AS moneda
FROM sale s
INNER JOIN reservation r ON r.reservation_id = s.reservation_id
INNER JOIN payment p ON p.sale_id = s.sale_id
INNER JOIN payment_status ps ON ps.payment_status_id = p.payment_status_id
INNER JOIN payment_method pm ON pm.payment_method_id = p.payment_method_id
INNER JOIN payment_transaction pt ON pt.payment_id = p.payment_id
INNER JOIN currency curr ON curr.currency_id = p.currency_id
ORDER BY pt.processed_at DESC;
```

### 8.2 Explicación paso a paso de la consulta
1. **`sale`** es el origen del pago.
2. **`reservation`** conecta la venta con la reserva que el cliente generó.
3. **`payment`** representa la intención de pago asociada a la venta.
4. **`payment_status`** y **`payment_method`** describen cómo y en qué estado se encuentra el pago.
5. **`payment_transaction`** detalla los eventos reales (cobro, rechazo, devolución) aplicados sobre el pago.
6. **`currency`** brinda la moneda bajo la que operó todo el ciclo.

Se utilizó `INNER JOIN` (7 tablas conectadas) para listar únicamente ventas que cuenten con pagos e intentos de transacción válidos.

---

## 9. Procedimiento almacenado resuelto

### 9.1 Objetivo
Centralizar el registro de una transacción de pago, validando montos y generando referencias únicas para dejar la operación lista.

### 9.2 Decisión técnica
El procedimiento valida que el monto no sea cero o negativo y asigna una referencia trazable, insertando sobre `payment_transaction`.

### 9.3 Script del procedimiento
```sql
DROP PROCEDURE IF EXISTS sp_register_payment_transaction(uuid, varchar, numeric, timestamptz, text);

CREATE OR REPLACE PROCEDURE sp_register_payment_transaction(
    p_payment_id uuid,
    p_transaction_type varchar(20),
    p_transaction_amount numeric,
    p_processed_at timestamptz,
    p_provider_message text
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_transaction_reference varchar(60);
BEGIN
    IF p_transaction_amount <= 0 THEN
        RAISE EXCEPTION 'El monto de la transacción debe ser mayor a cero.';
    END IF;

    v_transaction_reference := 'TXN-' || upper(replace(left(gen_random_uuid()::text, 10), '-', ''));

    INSERT INTO payment_transaction (
        payment_id,
        transaction_reference,
        transaction_type,
        transaction_amount,
        processed_at,
        provider_message
    )
    VALUES (
        p_payment_id,
        v_transaction_reference,
        p_transaction_type,
        p_transaction_amount,
        p_processed_at,
        p_provider_message
    );
END;
$$;
```

### 9.4 Por qué esta solución es correcta
- Evita que los clientes (APIs o usuarios) generen lógicas custom para crear las referencias.
- Valida reglas de negocio elementales de los pagos antes de insertar en la base de datos.

---

## 10. Trigger resuelto

### 10.1 Decisión técnica
Se configuró un trigger `AFTER INSERT ON payment_transaction`. La lógica dicta que toda transacción con tipo `REFUND` o `REVERSAL` debe documentarse operativamente en la tabla `refund`.

### 10.2 Lógica implementada
- Al insertarse la transacción, revisa la columna `transaction_type`.
- Si coincide con el tipo que genera devolución, inserta inmediatamente en `refund`, asociando la referencia de la transacción y usando el mismo monto.

### 10.3 Script del trigger
```sql
DROP TRIGGER IF EXISTS trg_ai_payment_transaction_create_refund ON payment_transaction;
DROP FUNCTION IF EXISTS fn_ai_payment_transaction_create_refund();

CREATE OR REPLACE FUNCTION fn_ai_payment_transaction_create_refund()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    IF NEW.transaction_type IN ('REFUND', 'REVERSAL') THEN
        INSERT INTO refund (
            payment_id,
            refund_reference,
            amount,
            requested_at,
            processed_at,
            refund_reason
        )
        VALUES (
            NEW.payment_id,
            'REF-' || NEW.transaction_reference,
            NEW.transaction_amount,
            NEW.processed_at,
            NEW.processed_at,
            'Generado automáticamente por transacción de tipo ' || NEW.transaction_type || ': ' || COALESCE(NEW.provider_message, '')
        );
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_ai_payment_transaction_create_refund
AFTER INSERT ON payment_transaction
FOR EACH ROW
EXECUTE FUNCTION fn_ai_payment_transaction_create_refund();
```

---

## 11. Script de demostración del funcionamiento

```sql
DO $$
DECLARE
    v_payment_id uuid;
BEGIN
    -- 1. Buscar un pago existente
    SELECT payment_id
    INTO v_payment_id
    FROM payment
    LIMIT 1;

    IF v_payment_id IS NULL THEN
        RAISE EXCEPTION 'No se encontró un pago para la prueba.';
    END IF;

    -- 2. Invocar el procedimiento simulando una transacción de DEVOLUCIÓN ('REFUND')
    -- Esto disparará el trigger y creará un registro en la tabla refund
    CALL sp_register_payment_transaction(
        v_payment_id,
        'REFUND',  -- Tipo de transacción que activa el trigger
        150.00,    -- Monto
        now(),
        'Devolución parcial solicitada por el cliente'
    );

    RAISE NOTICE 'Transacción de tipo REFUND registrada para el pago %', v_payment_id;
END;
$$;

-- 3. Verificación de la Transacción y la Devolución (creada por el trigger)
SELECT 
    pt.transaction_reference,
    pt.transaction_type,
    pt.transaction_amount,
    r.refund_reference,
    r.amount,
    r.refund_reason
FROM payment_transaction pt
INNER JOIN refund r ON r.payment_id = pt.payment_id
WHERE pt.transaction_type = 'REFUND'
ORDER BY pt.created_at DESC
LIMIT 1;
```

### 11.1 Qué demuestra este script
1. Busca un registro de `payment` válido en la base de datos.
2. Llama al procedimiento y obliga el tipo de transacción a ser `REFUND`.
3. La consulta de validación demuestra que automáticamente apareció un registro espejo en la tabla `refund` relacionado con esa misma transacción, probando el éxito del trigger.

---

## 12. Validación final
La solución es válida porque:
- Relaciona el ciclo completo de pago de forma impecable usando la consulta exigida.
- El trigger se encarga de reaccionar en cascada a un evento de negocio (devolución).
- El procedimiento aisla la lógica de inserción.
- Todo ocurre sobre las entidades definidas por el modelo (restricción principal cumplida).

---

## 13. Archivo SQL relacionado
- `scripts_sql/ejercicio_02_setup.sql`
- `scripts_sql/ejercicio_02_demo.sql`
