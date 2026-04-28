DROP TRIGGER IF EXISTS trg_generate_refund ON payment_transaction;
DROP FUNCTION IF EXISTS fn_generate_refund();
DROP PROCEDURE IF EXISTS sp_register_payment_transaction(uuid, varchar, numeric, text);
-- ============================================
-- TRIGGER: GENERAR REFUND AUTOMÁTICO
-- ============================================

CREATE OR REPLACE FUNCTION fn_generate_refund()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.transaction_type = 'REVERSAL' THEN
        INSERT INTO refund (
            payment_id,
            refund_amount,
            refund_reason,
            created_at
        )
        VALUES (
            NEW.payment_id,
            NEW.amount,
            'Reversión automática',
            now()
        );
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_generate_refund
AFTER INSERT ON payment_transaction
FOR EACH ROW
EXECUTE FUNCTION fn_generate_refund();


-- ============================================
-- PROCEDIMIENTO: REGISTRAR TRANSACCIÓN
-- ============================================

CREATE OR REPLACE PROCEDURE sp_register_payment_transaction(
    p_payment_id uuid,
    p_transaction_type varchar,
    p_amount numeric,
    p_provider_message text
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO payment_transaction (
        payment_id,
        transaction_type,
        amount,
        transaction_reference,
        processed_at,
        provider_message
    )
    VALUES (
        p_payment_id,
        p_transaction_type,
        p_amount,
        substr(md5(random()::text), 1, 12),
        now(),
        p_provider_message
    );
END;
$$;