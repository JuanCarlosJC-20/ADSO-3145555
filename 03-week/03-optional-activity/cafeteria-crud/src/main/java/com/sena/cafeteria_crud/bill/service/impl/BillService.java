package com.sena.cafeteria_crud.bill.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sena.cafeteria_crud.bill.dto.request.BillDetailRequestDTO;
import com.sena.cafeteria_crud.bill.dto.request.BillRequestDTO;
import com.sena.cafeteria_crud.bill.dto.response.BillDetailResponseDTO;
import com.sena.cafeteria_crud.bill.dto.response.BillResponseDTO;
import com.sena.cafeteria_crud.bill.model.Bill;
import com.sena.cafeteria_crud.bill.model.BillDetail;
import com.sena.cafeteria_crud.bill.repository.IBillRepository;
import com.sena.cafeteria_crud.bill.service.interfaces.IBillService;
import com.sena.cafeteria_crud.inventory.model.Product;
import com.sena.cafeteria_crud.inventory.repository.IProductRepository;
import com.sena.cafeteria_crud.security.model.User;
import com.sena.cafeteria_crud.security.repository.IUserRepository;


@Service
public class BillService implements IBillService {

    @Autowired
    private IBillRepository billRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IProductRepository productRepository;

    @Override
    public List<BillResponseDTO> getAllBills() {
        return billRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public BillResponseDTO getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + id));
        return convertToResponseDTO(bill);
    }

    @Override
    @Transactional
    public boolean createBill(BillRequestDTO billRequestDTO) {
        User user = userRepository.findById(billRequestDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + billRequestDTO.getUserId()));

        Bill bill = new Bill();
        bill.setBillNumber("BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        bill.setDate(LocalDateTime.now());
        bill.setSubtotal(billRequestDTO.getSubtotal());
        bill.setTax(billRequestDTO.getTax());
        bill.setTotal(billRequestDTO.getTotal());
        bill.setUser(user);
        bill.setState(true);

        Bill savedBill = billRepository.save(bill);

        // Crear detalles de la factura
        for (BillDetailRequestDTO detailDTO : billRequestDTO.getBillDetails()) {
            Product product = productRepository.findById(detailDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + detailDTO.getProductId()));

            BillDetail detail = new BillDetail();
            detail.setQuantity(detailDTO.getQuantity());
            detail.setUnitPrice(detailDTO.getUnitPrice());
            detail.setTotalPrice(detailDTO.getTotalPrice());
            detail.setBill(savedBill);
            detail.setProduct(product);
            detail.setState(true);

            // Actualizar stock del producto
            product.setStock(product.getStock() - detailDTO.getQuantity());
            productRepository.save(product);
        }

        return true;
    }

    @Override
    public boolean deleteBill(Long id) {
        billRepository.deleteById(id);
        return true;
    }

    @Override
    public List<BillResponseDTO> getBillsByUser(Long userId) {
        return billRepository.findAll().stream()
                .filter(bill -> bill.getUser().getId().equals(userId))
                .map(this::convertToResponseDTO)
                .toList();
    }

    private BillResponseDTO convertToResponseDTO(Bill bill) {
        List<BillDetailResponseDTO> detailDTOs = bill.getBillDetails().stream()
                .map(detail -> new BillDetailResponseDTO(
                        detail.getId(),
                        detail.getQuantity(),
                        detail.getUnitPrice(),
                        detail.getTotalPrice(),
                        detail.getState(),
                        detail.getProduct().getId(),
                        detail.getProduct().getName()
                )).toList();

        return new BillResponseDTO(
                bill.getId(),
                bill.getBillNumber(),
                bill.getDate(),
                bill.getSubtotal(),
                bill.getTax(),
                bill.getTotal(),
                bill.getState(),
                bill.getUser().getId(),
                bill.getUser().getUsername(),
                detailDTOs
        );
    }
}