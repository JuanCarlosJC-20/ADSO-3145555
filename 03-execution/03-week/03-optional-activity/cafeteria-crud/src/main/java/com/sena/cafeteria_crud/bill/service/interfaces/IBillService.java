package com.sena.cafeteria_crud.bill.service.interfaces;

import java.util.List;

import com.sena.cafeteria_crud.bill.dto.request.BillRequestDTO;
import com.sena.cafeteria_crud.bill.dto.response.BillResponseDTO;

public interface IBillService {
    List<BillResponseDTO> getAllBills();
    BillResponseDTO getBillById(Long id);
    boolean createBill(BillRequestDTO billRequestDTO);
    boolean deleteBill(Long id);
    List<BillResponseDTO> getBillsByUser(Long userId);
}