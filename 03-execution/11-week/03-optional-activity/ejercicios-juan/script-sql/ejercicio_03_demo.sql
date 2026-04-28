-- ============================================
-- CONSULTA PRINCIPAL (INNER JOIN)
-- ============================================

SELECT 
    s.sale_id,
    i.invoice_id,
    isf.invoice_status_name,
    il.line_number,
    il.description,
    il.quantity,
    il.unit_price,
    t.tax_name,
    c.currency_code
FROM sale s
INNER JOIN invoice i 
    ON s.sale_id = i.sale_id
INNER JOIN invoice_status isf 
    ON i.invoice_status_id = isf.invoice_status_id
INNER JOIN invoice_line il 
    ON i.invoice_id = il.invoice_id
INNER JOIN tax t 
    ON il.tax_id = t.tax_id
INNER JOIN currency c 
    ON i.currency_id = c.currency_id;

-- ============================================
-- PRUEBA DIRECTA DEL TRIGGER
-- ============================================

INSERT INTO invoice_line (
    invoice_id,
    tax_id,
    line_number,
    description,
    quantity,
    unit_price
)
VALUES (
    (SELECT invoice_id FROM invoice LIMIT 1),
    (SELECT tax_id FROM tax LIMIT 1),
    1,
    'Servicio adicional',
    2,
    50000
);


-- ============================================
-- USO DEL PROCEDIMIENTO
-- ============================================

CALL sp_add_invoice_line(
    (SELECT invoice_id FROM invoice LIMIT 1),
    (SELECT tax_id FROM tax LIMIT 1),
    2,
    'Equipaje extra',
    1,
    80000
);


-- ============================================
-- VALIDACIÓN FINAL
-- ============================================

SELECT 
    i.invoice_id,
    i.total_amount,
    il.line_number,
    il.description,
    (il.quantity * il.unit_price) AS line_total
FROM invoice i
INNER JOIN invoice_line il 
    ON i.invoice_id = il.invoice_id
ORDER BY i.invoice_id, il.line_number;