-- ============================================
-- CONSULTA PRINCIPAL (INNER JOIN)
-- ============================================

SELECT 
    c.customer_id,
    CONCAT(p.first_name, ' ', p.last_name) AS customer_name,
    la.loyalty_account_id,
    lp.program_name,
    lt.tier_name,
    lat.assigned_at,
    s.sale_id
FROM customer c
INNER JOIN person p 
    ON c.person_id = p.person_id
INNER JOIN loyalty_account la 
    ON c.customer_id = la.customer_id
INNER JOIN loyalty_program lp 
    ON la.loyalty_program_id = lp.loyalty_program_id
INNER JOIN loyalty_account_tier lat 
    ON la.loyalty_account_id = lat.loyalty_account_id
INNER JOIN loyalty_tier lt 
    ON lat.loyalty_tier_id = lt.loyalty_tier_id
INNER JOIN sale s 
    ON c.customer_id = s.customer_id;


-- ============================================
-- PRUEBA DIRECTA DEL TRIGGER
-- ============================================

INSERT INTO miles_transaction (
    loyalty_account_id,
    transaction_type,
    miles_amount,
    transaction_date
)
VALUES (
    (SELECT loyalty_account_id FROM loyalty_account LIMIT 1),
    'EARN',
    60000,
    now()
);


-- ============================================
-- USO DEL PROCEDIMIENTO
-- ============================================

CALL sp_register_miles_transaction(
    (SELECT loyalty_account_id FROM loyalty_account LIMIT 1),
    'EARN',
    70000,
    'Acumulación por vuelo internacional'
);


-- ============================================
-- VALIDACIÓN FINAL
-- ============================================

SELECT 
    la.loyalty_account_id,
    lt.tier_name,
    lat.assigned_at
FROM loyalty_account la
INNER JOIN loyalty_account_tier lat 
    ON la.loyalty_account_id = lat.loyalty_account_id
INNER JOIN loyalty_tier lt 
    ON lat.loyalty_tier_id = lt.loyalty_tier_id
ORDER BY lat.assigned_at DESC;