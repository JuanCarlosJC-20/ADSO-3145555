-- ============================================
-- OBTENER IDS NECESARIOS
-- ============================================

SELECT ticket_segment_id FROM ticket_segment LIMIT 1;
SELECT check_in_status_id FROM check_in_status LIMIT 1;
SELECT boarding_group_id FROM boarding_group LIMIT 1;
SELECT user_account_id FROM user_account LIMIT 1;

-- ============================================
-- PRUEBA 1: INSERT DIRECTO (DISPARA TRIGGER)
-- ============================================

INSERT INTO check_in (
    ticket_segment_id,
    check_in_status_id,
    boarding_group_id,
    checked_in_by_user_id,
    checked_in_at
)
VALUES (
    (SELECT ticket_segment_id FROM ticket_segment LIMIT 1),
    (SELECT check_in_status_id FROM check_in_status LIMIT 1),
    (SELECT boarding_group_id FROM boarding_group LIMIT 1),
    (SELECT user_account_id FROM user_account LIMIT 1),
    now()
);

-- ============================================
-- VALIDACIÓN TRIGGER
-- ============================================

SELECT * 
FROM boarding_pass
ORDER BY created_at DESC;

-- ============================================
-- PRUEBA 2: USO DEL PROCEDIMIENTO
-- ============================================

CALL sp_register_checkin(
    (SELECT ticket_segment_id FROM ticket_segment LIMIT 1),
    (SELECT check_in_status_id FROM check_in_status LIMIT 1),
    (SELECT boarding_group_id FROM boarding_group LIMIT 1),
    (SELECT user_account_id FROM user_account LIMIT 1)
);

-- ============================================
-- VALIDACIÓN FINAL
-- ============================================

SELECT 
    ci.check_in_id,
    ci.ticket_segment_id,
    bp.boarding_pass_code,
    bp.barcode_value,
    bp.issued_at
FROM check_in ci
INNER JOIN boarding_pass bp 
    ON ci.check_in_id = bp.check_in_id
ORDER BY ci.created_at DESC;