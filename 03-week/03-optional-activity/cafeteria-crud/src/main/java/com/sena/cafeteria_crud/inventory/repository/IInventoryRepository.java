package com.sena.cafeteria_crud.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sena.cafeteria_crud.inventory.model.Product;

/**
 * Contrato de acceso a datos para el módulo Inventory
 * Aplica principios SOLID: Interface Segregation y Dependency Inversion
 * Maneja todas las operaciones de persistencia para el inventario
 */
@Repository
public interface IInventoryRepository extends JpaRepository<Product, Long> {
    
    // Operaciones básicas para Product
    Optional<Product> findByIdAndStateTrue(Long id);
    List<Product> findByStateTrue();
    
    // Buscar productos con stock bajo
    @Query("SELECT p FROM Product p WHERE p.stock <= p.minStock AND p.state = true")
    List<Product> findLowStockProducts();
    
    // Buscar productos por categoría
    List<Product> findByCategoryAndStateTrue(String category);
    
    // Buscar productos por nombre
    Optional<Product> findByNameAndStateTrue(String name);
    
    // Verificar existencia por nombre
    boolean existsByNameAndStateTrue(String name);
    
    // Operaciones de stock
    @Query("SELECT p FROM Product p WHERE p.stock > 0 AND p.state = true")
    List<Product> findAvailableProducts();
    
    @Query("SELECT p FROM Product p WHERE p.stock = 0 AND p.state = true")
    List<Product> findOutOfStockProducts();
}