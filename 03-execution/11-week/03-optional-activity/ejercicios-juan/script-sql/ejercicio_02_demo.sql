-- ============================================
-- CONSULTA PRINCIPAL (INNER JOIN)
-- ============================================

SELECT 
    s.sale_id,
    r.reservation_code,
    p.payment_id,
    ps.payment_status_name,
    pm.payment_method_name,
    pt.transaction_reference,
    pt.transaction_type,
    pt.amount,
    c.currency_code
FROM sale s
INNER JOIN reservation r 
    ON s.reservation_id = r.reservation_id
INNER JOIN payment p 
    ON s.sale_id = p.sale_id
INNER JOIN payment_status ps 
    ON p.payment_status_id = ps.payment_status_id
INNER JOIN payment_method pm 
    ON p.payment_method_id = pm.payment_method_id
INNER JOIN payment_transaction pt 
    ON p.payment_id = pt.payment_id
INNER JOIN currency c 
    ON p.currency_id = c.currency_id;


-- ============================================
-- PRUEBA DIRECTA DEL TRIGGER
-- ============================================

INSERT INTO payment_transaction (
    payment_id,
    transaction_type,
    amount,
    transaction_reference,
    processed_at
)
VALUES (
    (SELECT payment_id FROM payment LIMIT 1),
    'REVERSAL',
    100,
    'TEST-REV',
    now()
);


-- ============================================
-- USO DEL PROCEDIMIENTO
-- ============================================

CALL sp_register_payment_transaction(
    (SELECT payment_id FROM payment LIMIT 1),
    'REVERSAL',
    150,
    'Reversión por error en cobro'
);


-- ============================================
-- VALIDACIÓN FINAL
-- ============================================

SELECT 
    p.payment_id,
    pt.transaction_type,
    pt.amount,
    r.refund_amount,
    r.created_at
FROM payment p
INNER JOIN payment_transaction pt 
    ON p.payment_id = pt.payment_id
LEFT JOIN refund r 
    ON p.payment_id = r.payment_id
ORDER BY pt.processed_at DESC;