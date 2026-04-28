-- ============================================
-- CONSULTA PRINCIPAL (INNER JOIN)
-- ============================================

SELECT 
    a.aircraft_id,
    al.airline_name,
    am.model_name,
    mf.manufacturer_name,
    mt.maintenance_type_name,
    mp.provider_name,
    me.status,
    me.start_date,
    me.end_date
FROM aircraft a
INNER JOIN airline al 
    ON a.airline_id = al.airline_id
INNER JOIN aircraft_model am 
    ON a.aircraft_model_id = am.aircraft_model_id
INNER JOIN aircraft_manufacturer mf 
    ON am.aircraft_manufacturer_id = mf.aircraft_manufacturer_id
INNER JOIN maintenance_event me 
    ON a.aircraft_id = me.aircraft_id
INNER JOIN maintenance_type mt 
    ON me.maintenance_type_id = mt.maintenance_type_id
INNER JOIN maintenance_provider mp 
    ON me.maintenance_provider_id = mp.maintenance_provider_id;


-- ============================================
-- PRUEBA DIRECTA DEL TRIGGER
-- ============================================

UPDATE maintenance_event
SET end_date = now()
WHERE maintenance_event_id = (
    SELECT maintenance_event_id FROM maintenance_event LIMIT 1
);


-- ============================================
-- USO DEL PROCEDIMIENTO
-- ============================================

CALL sp_register_maintenance_event(
    (SELECT aircraft_id FROM aircraft LIMIT 1),
    (SELECT maintenance_type_id FROM maintenance_type LIMIT 1),
    (SELECT maintenance_provider_id FROM maintenance_provider LIMIT 1),
    'IN_PROGRESS',
    now(),
    'Mantenimiento preventivo programado'
);


-- ============================================
-- VALIDACIÓN FINAL
-- ============================================

SELECT 
    a.aircraft_id,
    me.status,
    me.start_date,
    me.end_date,
    a.updated_at
FROM aircraft a
INNER JOIN maintenance_event me 
    ON a.aircraft_id = me.aircraft_id
ORDER BY me.start_date DESC;