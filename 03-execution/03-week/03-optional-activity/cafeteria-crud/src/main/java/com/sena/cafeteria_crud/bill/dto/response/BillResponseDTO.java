package com.sena.cafeteria_crud.bill.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BillResponseDTO {

    private Long id;
    private String billNumber;
    private LocalDateTime date;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private Boolean state;
    private Long userId;
    private String username;
    private List<BillDetailResponseDTO> billDetails;
}