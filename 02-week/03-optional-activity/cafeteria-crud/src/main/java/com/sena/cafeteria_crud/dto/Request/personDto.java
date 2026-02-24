package com.sena.cafeteria_crud.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class personDto {

     //@NotNull        // No puede ser null
    //@NotBlank       // No puede ser null, vacío o solo espacios (String)
    //@NotEmpty       // No puede ser null o vacío (Strings, Collections)
    //@Size(min=, max=)  // Tamaño mínimo/máximo
    //@Min(value)     // Valor mínimo (números)
    //@Max(value)     // Valor máximo (números)
    //@Email          // Debe ser email válido
    //@Pattern(regexp="")  // Debe coincidir con expresión regular
    //@Past           // Fecha en el pasado
    //@Future         // Fecha en el futuro
    //@Positive       // Número positivo
    //@PositiveOrZero // Número positivo o cero
    //@DecimalMin     // Valor decimal mínimo
    //@DecimalMax     // Valor decimal máximo

    

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres") 
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 80, message = "El apellido debe tener entre 2 y 80 caracteres") 
    private String lastName;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    private String email;

    @Size(max = 20, message = "El teléfono no debe exceder los 20 caracteres")
    private String phone;

    
   

}
