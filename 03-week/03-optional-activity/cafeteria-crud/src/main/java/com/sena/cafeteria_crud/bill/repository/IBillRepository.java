package com.sena.cafeteria_crud.bill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sena.cafeteria_crud.bill.model.Bill;

@Repository
public interface IBillRepository extends JpaRepository<Bill, Long> {
}