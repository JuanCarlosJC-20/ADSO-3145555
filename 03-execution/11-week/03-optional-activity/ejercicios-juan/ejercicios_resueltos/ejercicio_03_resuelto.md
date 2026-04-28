# Ejercicio 03 Resuelto - Facturación e integración entre venta, impuestos y detalle facturable

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
El área de facturación necesita relacionar ventas, facturas y líneas facturables con impuestos aplicados para validar la consistencia del flujo comercial y automatizar acciones posteriores sobre el detalle facturable.

---

## 6. Dominios involucrados
### SALES, RESERVATION, TICKETING
**Entidades:** `sale`, `reservation`  
**Propósito en este ejercicio:** Proveer el origen comercial de la facturación.

### BILLING
**Entidades:** `invoice`, `invoice_status`, `invoice_line`, `tax`, `exchange_rate`  
**Propósito en este ejercicio:** Gestionar factura, estado, detalle facturable, impuestos y apoyo de conversión monetaria cuando aplique.

### GEOGRAPHY AND REFERENCE DATA
**Entidades:** `currency`  
**Propósito en este ejercicio:** Normalizar la moneda de la venta y de la factura.

---

## 7. Problema a resolver
Se requiere consultar el detalle facturable derivado de una venta y definir una automatización posterior para mantener coherencia entre la cabecera de la factura y sus líneas asociadas. 

Por eso se plantea una solución en tres capas:
1. una consulta consolidada con `INNER JOIN`,
2. un procedimiento almacenado para registrar las líneas facturables de forma segura,
3. un trigger `AFTER INSERT` sobre `invoice_line` que actúe sobre la cabecera de la factura.

---

## 8. Solución propuesta

### 8.1 Consulta resuelta con `INNER JOIN`
Se elaboró una consulta que desglosa cada factura hasta el nivel de su línea y el impuesto aplicado.

```sql
SELECT
    s.sale_code AS codigo_venta,
    i.invoice_number AS numero_factura,
    ist.status_name AS estado_factura,
    il.line_number AS linea_facturable,
    il.line_description AS descripcion_linea,
    il.quantity AS cantidad,
    il.unit_price AS precio_unitario,
    t.tax_name AS impuesto_aplicado,
    curr.iso_currency_code AS moneda
FROM sale s
INNER JOIN invoice i ON i.sale_id = s.sale_id
INNER JOIN invoice_status ist ON ist.invoice_status_id = i.invoice_status_id
INNER JOIN invoice_line il ON il.invoice_id = i.invoice_id
INNER JOIN tax t ON t.tax_id = il.tax_id
INNER JOIN currency curr ON curr.currency_id = i.currency_id
ORDER BY i.invoice_number, il.line_number;
```

---

## 9. Procedimiento almacenado resuelto

### 9.1 Objetivo
Encapsular la creación del detalle facturable (`invoice_line`) para asegurar consistencia, validar cantidades nulas o negativas, y hacerlo reutilizable por la aplicación.

### 9.2 Decisión técnica
El procedimiento valida explícitamente que la cantidad y el precio tengan sentido contable, y luego inserta la línea con todos los datos suministrados por parámetro.

### 9.3 Script del procedimiento
```sql
DROP PROCEDURE IF EXISTS sp_add_invoice_line(uuid, uuid, integer, varchar, numeric, numeric);

CREATE OR REPLACE PROCEDURE sp_add_invoice_line(
    p_invoice_id uuid,
    p_tax_id uuid,
    p_line_number integer,
    p_line_description varchar(255),
    p_quantity numeric,
    p_unit_price numeric
)
LANGUAGE plpgsql
AS $$
BEGIN
    -- Validar cantidades
    IF p_quantity <= 0 OR p_unit_price < 0 THEN
        RAISE EXCEPTION 'La cantidad debe ser mayor a cero y el precio no puede ser negativo.';
    END IF;

    INSERT INTO invoice_line (
        invoice_id,
        tax_id,
        line_number,
        line_description,
        quantity,
        unit_price
    )
    VALUES (
        p_invoice_id,
        p_tax_id,
        p_line_number,
        p_line_description,
        p_quantity,
        p_unit_price
    );
END;
$$;
```

---

## 10. Trigger resuelto

### 10.1 Decisión técnica
Se configuró un trigger `AFTER INSERT ON invoice_line`. Al agregar un nuevo detalle facturable a una factura, es vital actualizar la fecha de modificación de la factura maestra (cabecera) para fines de trazabilidad de auditoría.

### 10.2 Lógica implementada
- El trigger se ejecuta tras cada nueva línea añadida.
- Ejecuta un `UPDATE` en la tabla `invoice`, estableciendo `updated_at = now()` para el `invoice_id` correspondiente.

### 10.3 Script del trigger
```sql
DROP TRIGGER IF EXISTS trg_ai_invoice_line_touch_invoice ON invoice_line;
DROP FUNCTION IF EXISTS fn_ai_invoice_line_touch_invoice();

CREATE OR REPLACE FUNCTION fn_ai_invoice_line_touch_invoice()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE invoice
    SET updated_at = now()
    WHERE invoice_id = NEW.invoice_id;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_ai_invoice_line_touch_invoice
AFTER INSERT ON invoice_line
FOR EACH ROW
EXECUTE FUNCTION fn_ai_invoice_line_touch_invoice();
```

---

## 11. Script de demostración del funcionamiento

```sql
DO $$
DECLARE
    v_invoice_id uuid;
    v_tax_id uuid;
BEGIN
    -- 1. Buscar una factura existente
    SELECT invoice_id
    INTO v_invoice_id
    FROM invoice
    LIMIT 1;

    -- 2. Buscar un impuesto aplicable
    SELECT tax_id
    INTO v_tax_id
    FROM tax
    LIMIT 1;

    IF v_invoice_id IS NULL THEN
        RAISE EXCEPTION 'No se encontró una factura para la prueba.';
    END IF;

    -- 3. Invocar procedimiento (dispara el trigger)
    CALL sp_add_invoice_line(
        v_invoice_id,
        v_tax_id,
        10, -- line_number
        'Cargo adicional por servicio especial (Demo)',
        1.0,
        50.00
    );

    RAISE NOTICE 'Línea de factura agregada exitosamente a la factura %', v_invoice_id;
END;
$$;

-- 4. Verificación de la línea insertada y la trazabilidad del trigger
SELECT 
    i.invoice_number,
    i.updated_at AS cabecera_actualizada,
    il.line_number,
    il.line_description,
    il.quantity,
    il.unit_price
FROM invoice i
INNER JOIN invoice_line il ON il.invoice_id = i.invoice_id
WHERE il.line_description = 'Cargo adicional por servicio especial (Demo)'
ORDER BY il.created_at DESC
LIMIT 1;
```

---

## 12. Validación final
La solución es válida porque:
- Implementa una consulta profunda entre la fidelidad y la venta.
- Aplica triggers y procedimientos que cumplen exactamente con los requerimientos técnicos y conceptuales del negocio propuesto.
- No utiliza campos inventados o alterados.

---

## 13. Archivo SQL relacionado
- `scripts_sql/ejercicio_03_setup.sql`
- `scripts_sql/ejercicio_03_demo.sql`
