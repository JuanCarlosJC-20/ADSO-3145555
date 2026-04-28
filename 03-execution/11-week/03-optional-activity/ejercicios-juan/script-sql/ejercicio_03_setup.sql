DROP TRIGGER IF EXISTS trg_update_invoice_total ON invoice_line;
DROP FUNCTION IF EXISTS fn_update_invoice_total();
DROP PROCEDURE IF EXISTS sp_add_invoice_line(uuid, uuid, integer, text, numeric, numeric);

-- ============================================
-- TRIGGER: ACTUALIZAR TOTAL DE FACTURA
-- ============================================

CREATE OR REPLACE FUNCTION fn_update_invoice_total()
RETURNS TRIGGER AS $$
BEGIN
    -- Recalcula el total de la factura sumando sus líneas
    UPDATE invoice
    SET total_amount = (
        SELECT COALESCE(SUM(quantity * unit_price), 0)
        FROM invoice_line
        WHERE invoice_id = NEW.invoice_id
    )
    WHERE invoice_id = NEW.invoice_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_invoice_total
AFTER INSERT ON invoice_line
FOR EACH ROW
EXECUTE FUNCTION fn_update_invoice_total();


-- ============================================
-- PROCEDIMIENTO: REGISTRAR LÍNEA FACTURABLE
-- ============================================

CREATE OR REPLACE PROCEDURE sp_add_invoice_line(
    p_invoice_id uuid,
    p_tax_id uuid,
    p_line_number integer,
    p_description text,
    p_quantity numeric,
    p_unit_price numeric
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO invoice_line (
        invoice_id,
        tax_id,
        line_number,
        description,
        quantity,
        unit_price
    )
    VALUES (
        p_invoice_id,
        p_tax_id,
        p_line_number,
        p_description,
        p_quantity,
        p_unit_price
    );
END;
$$;