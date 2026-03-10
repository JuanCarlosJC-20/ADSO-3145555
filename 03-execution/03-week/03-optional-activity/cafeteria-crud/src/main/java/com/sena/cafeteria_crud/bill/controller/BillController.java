package com.sena.cafeteria_crud.bill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sena.cafeteria_crud.bill.dto.request.BillRequestDTO;
import com.sena.cafeteria_crud.bill.service.impl.BillService;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    // Obtener todas las facturas
    @GetMapping
    public ResponseEntity<Object> getAllBills() {
        var bills = billService.getAllBills();
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    // Obtener factura por ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getBillById(@PathVariable Long id) {
        var bill = billService.getBillById(id);
        return new ResponseEntity<>(bill, HttpStatus.OK);
    }

    // Crear nueva factura
    @PostMapping
    public String createBill(@Validated @RequestBody BillRequestDTO billRequestDTO) {
        billService.createBill(billRequestDTO);
        return "Factura creada exitosamente";
    }

    // Eliminar factura
    @DeleteMapping("/{id}")
    public boolean deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return true;
    }

    // Obtener facturas por usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getBillsByUser(@PathVariable Long userId) {
        var bills = billService.getBillsByUser(userId);
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }
}