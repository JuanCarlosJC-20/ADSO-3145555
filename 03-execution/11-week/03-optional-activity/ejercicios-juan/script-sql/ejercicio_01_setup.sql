DROP TRIGGER IF EXISTS trg_generate_boarding_pass ON check_in;
DROP FUNCTION IF EXISTS fn_generate_boarding_pass();
DROP PROCEDURE IF EXISTS sp_register_checkin(uuid, uuid, uuid, uuid);

CREATE OR REPLACE FUNCTION fn_generate_boarding_pass()
RETURNS TRIGGER AS $$
DECLARE
    v_code TEXT;
    v_barcode TEXT;
BEGIN
    -- Generar código único simple
    v_code := 'BP-' || substr(md5(random()::text), 1, 10);
    v_barcode := substr(md5(random()::text), 1, 20);

    INSERT INTO boarding_pass (
        check_in_id,
        boarding_pass_code,
        barcode_value,
        issued_at
    )
    VALUES (
        NEW.check_in_id,
        v_code,
        v_barcode,
        now()
    );

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_generate_boarding_pass
AFTER INSERT ON check_in
FOR EACH ROW
EXECUTE FUNCTION fn_generate_boarding_pass();

CREATE OR REPLACE PROCEDURE sp_register_checkin(
    p_ticket_segment_id uuid,
    p_check_in_status_id uuid,
    p_boarding_group_id uuid,
    p_user_id uuid
)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO check_in (
        ticket_segment_id,
        check_in_status_id,
        boarding_group_id,
        checked_in_by_user_id,
        checked_in_at
    )
    VALUES (
        p_ticket_segment_id,
        p_check_in_status_id,
        p_boarding_group_id,
        p_user_id,
        now()
    );
END;
$$;

--Consulta resuelta: Mostrar los pasajeros asociados a un vuelo, indicando la reserva, el tiquete, el segmento y la fecha del servicio.
SELECT 
    r.reservation_code,
    f.flight_number,
    f.service_date,
    t.ticket_number,
    rp.passenger_sequence_no,
    CONCAT(p.first_name, ' ', p.last_name) AS passenger_name,
    fs.segment_number,
    fs.scheduled_departure_at
FROM reservation r
INNER JOIN reservation_passenger rp 
    ON r.reservation_id = rp.reservation_id
INNER JOIN person p 
    ON rp.person_id = p.person_id
INNER JOIN ticket t 
    ON rp.reservation_passenger_id = t.reservation_passenger_id
INNER JOIN ticket_segment ts 
    ON t.ticket_id = ts.ticket_id
INNER JOIN flight_segment fs 
    ON ts.flight_segment_id = fs.flight_segment_id
INNER JOIN flight f 
    ON fs.flight_id = f.flight_id;