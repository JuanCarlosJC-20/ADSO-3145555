package com.sena.cafeteria_crud.inventory.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @Size(max = 255, message = "La descripción no debe exceder los 255 caracteres")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio")
    @Positive(message = "El stock debe ser positivo")
    private Integer stock;

    @NotNull(message = "El stock mínimo es obligatorio")
    @Positive(message = "El stock mínimo debe ser positivo")
    private Integer minStock;

    @Size(max = 50, message = "La categoría no debe exceder los 50 caracteres")
    private String category;
}