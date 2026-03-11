/*
funcion para obtener nombre completo de la persona a partir de su id
*/

CREATE FUNCTION  get_person_name(p_person_id UUID)

RETURNS VARCHAR /*tipo de dato que retorna */
LANGUAGE plpgsql -- Se define el lenguaje que usará la función
AS $$
DECLARE -- Se declara una variable donde se guardará el nombre completo
    full_name VARCHAR;

    -- Inicio de la lógica de la función
BEGIN

    SELECT first_name || ' ' || last_name
    INTO full_name
    FROM person
    WHERE id = p_person_id;

    RETURN full_name;

END;
$$;-- Fin de la definición de la 

SELECT get_person_name('123e4567-e89b-12d3-a456-426614174000'); -- Ejemplo de cómo llamar a la función con un id específico