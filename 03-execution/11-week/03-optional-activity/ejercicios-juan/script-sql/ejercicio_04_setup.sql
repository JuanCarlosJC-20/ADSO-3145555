DROP TRIGGER IF EXISTS trg_update_loyalty_tier ON miles_transaction;
DROP FUNCTION IF EXISTS fn_update_loyalty_tier();
DROP PROCEDURE IF EXISTS sp_register_miles_transaction(uuid, varchar, numeric, text);
-- ============================================
-- TRIGGER: ACTUALIZAR NIVEL DE FIDELIZACIÓN
-- ============================================

CREATE OR REPLACE FUNCTION fn_update_loyalty_tier()
RETURNS TRIGGER AS $$
DECLARE
    v_total_miles numeric;
BEGIN
    -- Calcular total de millas acumuladas
    SELECT COALESCE(SUM(miles_amount), 0)
    INTO v_total_miles
    FROM miles_transaction
    WHERE loyalty_account_id = NEW.loyalty_account_id;

    -- Regla simple de ejemplo (puedes ajustarla según tu modelo)
    IF v_total_miles > 50000 THEN
        INSERT INTO loyalty_account_tier (
            loyalty_account_id,
            loyalty_tier_id,
            assigned_at
        )
        VALUES (
            NEW.loyalty_account_id,
            (SELECT loyalty_tier_id FROM loyalty_tier LIMIT 1),
            now()
        );
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_loyalty_tier
AFTER INSERT ON miles_transaction
FOR EACH ROW
EXECUTE FUNCTION fn_update_loyalty_tier();


-- ============================================
-- PROCEDIMIENTO: REGISTRAR TRANSACCIÓN DE MILLAS
-- ============================================

CREATE OR REPLACE PROCEDURE sp_register_miles_transaction(
    p_loyalty_account_id uuid,
    p_transaction_type varchar,
    p_miles_amount numeric,
    p_description text
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO miles_transaction (
        loyalty_account_id,
        transaction_type,
        miles_amount,
        transaction_date,
        description
    )
    VALUES (
        p_loyalty_account_id,
        p_transaction_type,
        p_miles_amount,
        now(),
        p_description
    );
END;
$$;