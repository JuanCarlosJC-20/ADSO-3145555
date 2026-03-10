package com.sena.cafeteria_crud.inventory.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer minStock;
    private String category;
    private Boolean state;
    private Boolean lowStock; // Indica si el stock está por debajo del mínimo
}