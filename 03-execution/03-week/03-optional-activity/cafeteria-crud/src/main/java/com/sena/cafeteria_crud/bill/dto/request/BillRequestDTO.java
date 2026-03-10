package com.sena.cafeteria_crud.bill.dto.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillRequestDTO {

    @NotNull(message = "El subtotal es obligatorio")
    @Positive(message = "El subtotal debe ser positivo")
    private BigDecimal subtotal;

    private BigDecimal tax;

    @NotNull(message = "El total es obligatorio")
    @Positive(message = "El total debe ser positivo")
    private BigDecimal total;

    @NotNull(message = "El id del usuario es obligatorio")
    private Long userId;

    @NotEmpty(message = "La factura debe tener al menos un detalle")
    private List<BillDetailRequestDTO> billDetails;
}