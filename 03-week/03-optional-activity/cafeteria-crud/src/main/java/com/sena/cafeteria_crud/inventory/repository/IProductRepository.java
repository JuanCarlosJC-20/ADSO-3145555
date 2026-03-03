package com.sena.cafeteria_crud.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sena.cafeteria_crud.inventory.model.Product;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByIdAndStateTrue(Long id);
    
    List<Product> findByStateTrue();
    
    @Query("SELECT p FROM Product p WHERE p.stock <= p.minStock AND p.state = true")
    List<Product> findLowStockProducts();
    
    List<Product> findByCategoryAndStateTrue(String category);
    
    Optional<Product> findByNameAndStateTrue(String name);
    
    boolean existsByNameAndStateTrue(String name);
    
    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.state = true")
    List<Product> findAvailableProducts();
    
    @Query("SELECT p FROM Product p WHERE p.stock = 0 AND p.state = true")
    List<Product> findOutOfStockProducts();
}
