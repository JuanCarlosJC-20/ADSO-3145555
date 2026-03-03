package com.sena.cafeteria_crud.inventory.service.interfaces;

import java.util.List;
import java.util.Optional;

import com.sena.cafeteria_crud.inventory.dto.request.ProductRequestDTO;
import com.sena.cafeteria_crud.inventory.dto.response.ProductResponseDTO;

/**
 * Contrato de lógica de negocio para el módulo Inventory
 * Aplica principios SOLID: Single Responsibility y Dependency Inversion
 * Define todos los casos de uso del módulo de inventario
 */
public interface IInventoryService {
    
    // Gestión de productos
    ProductResponseDTO createProduct(ProductRequestDTO productRequest);
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequest);
    Optional<ProductResponseDTO> getProductById(Long id);
    Optional<ProductResponseDTO> getProductByName(String name);
    List<ProductResponseDTO> getAllProducts();
    List<ProductResponseDTO> getActiveProducts();
    boolean deleteProduct(Long id);
    
    // Gestión de Stock
    boolean updateStock(Long productId, Integer newStock);
    boolean increaseStock(Long productId, Integer quantity);
    boolean decreaseStock(Long productId, Integer quantity);
    List<ProductResponseDTO> getLowStockProducts();
    List<ProductResponseDTO> getAvailableProducts();
    List<ProductResponseDTO> getOutOfStockProducts();
    
    // Gestión por categorías
    List<ProductResponseDTO> getProductsByCategory(String category);
    List<String> getAllCategories();
    
    // Validaciones de negocio
    boolean hasAvailableStock(Long productId, Integer requiredQuantity);
    boolean isProductAvailable(Long productId);
    boolean validateStockMovement(Long productId, Integer quantity);
    
    // Alertas y reportes
    List<ProductResponseDTO> getProductsNeedingRestock();
    Integer getTotalProductsCount();
    Double getTotalInventoryValue();
}