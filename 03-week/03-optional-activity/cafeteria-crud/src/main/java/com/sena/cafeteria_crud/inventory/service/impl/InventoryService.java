package com.sena.cafeteria_crud.inventory.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.cafeteria_crud.inventory.dto.request.ProductRequestDTO;
import com.sena.cafeteria_crud.inventory.dto.response.ProductResponseDTO;
import com.sena.cafeteria_crud.inventory.model.Product;
import com.sena.cafeteria_crud.inventory.repository.IInventoryRepository;
import com.sena.cafeteria_crud.inventory.service.interfaces.IInventoryService;

/**
 * Implementación de lógica de negocio para el módulo Inventory
 * Aplica principios SOLID: Single Responsibility y Dependency Inversion
 */
@Service
public class InventoryService implements IInventoryService {

    @Autowired
    private IInventoryRepository inventoryRepository;

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setMinStock(productRequest.getMinStock());
        product.setCategory(productRequest.getCategory());
        product.setState(true);
        
        Product savedProduct = inventoryRepository.save(product);
        return convertToResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequest) {
        Product product = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStock(productRequest.getStock());
        product.setMinStock(productRequest.getMinStock());
        product.setCategory(productRequest.getCategory());
        
        Product updatedProduct = inventoryRepository.save(product);
        return convertToResponseDTO(updatedProduct);
    }

    @Override
    public Optional<ProductResponseDTO> getProductById(Long id) {
        return inventoryRepository.findByIdAndStateTrue(id)
                .map(this::convertToResponseDTO);
    }

    @Override
    public Optional<ProductResponseDTO> getProductByName(String name) {
        return inventoryRepository.findByNameAndStateTrue(name)
                .map(this::convertToResponseDTO);
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return inventoryRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public List<ProductResponseDTO> getActiveProducts() {
        return inventoryRepository.findByStateTrue().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public boolean deleteProduct(Long id) {
        Product product = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        product.setState(false);
        inventoryRepository.save(product);
        return true;
    }

    @Override
    public boolean updateStock(Long productId, Integer newStock) {
        Product product = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));
        product.setStock(newStock);
        inventoryRepository.save(product);
        return true;
    }

    @Override
    public boolean increaseStock(Long productId, Integer quantity) {
        Product product = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));
        product.setStock(product.getStock() + quantity);
        inventoryRepository.save(product);
        return true;
    }

    @Override
    public boolean decreaseStock(Long productId, Integer quantity) {
        Product product = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));
        
        if (product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            inventoryRepository.save(product);
            return true;
        }
        return false;
    }

    @Override
    public List<ProductResponseDTO> getLowStockProducts() {
        return inventoryRepository.findLowStockProducts().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public List<ProductResponseDTO> getAvailableProducts() {
        return inventoryRepository.findAvailableProducts().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public List<ProductResponseDTO> getOutOfStockProducts() {
        return inventoryRepository.findOutOfStockProducts().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(String category) {
        return inventoryRepository.findByCategoryAndStateTrue(category).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    @Override
    public List<String> getAllCategories() {
        return inventoryRepository.findByStateTrue().stream()
                .map(Product::getCategory)
                .distinct()
                .toList();
    }

    @Override
    public boolean hasAvailableStock(Long productId, Integer requiredQuantity) {
        Product product = inventoryRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));
        return product.getStock() >= requiredQuantity && product.getState();
    }

    @Override
    public boolean isProductAvailable(Long productId) {
        return inventoryRepository.findByIdAndStateTrue(productId)
                .map(product -> product.getStock() > 0)
                .orElse(false);
    }

    @Override
    public boolean validateStockMovement(Long productId, Integer quantity) {
        return quantity > 0 && hasAvailableStock(productId, quantity);
    }

    @Override
    public List<ProductResponseDTO> getProductsNeedingRestock() {
        return getLowStockProducts();
    }

    @Override
    public Integer getTotalProductsCount() {
        return Math.toIntExact(inventoryRepository.count());
    }

    @Override
    public Double getTotalInventoryValue() {
        return inventoryRepository.findByStateTrue().stream()
                .mapToDouble(product -> product.getPrice().multiply(java.math.BigDecimal.valueOf(product.getStock())).doubleValue())
                .sum();
    }

    private ProductResponseDTO convertToResponseDTO(Product product) {
        boolean lowStock = product.getStock() <= product.getMinStock();
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getMinStock(),
                product.getCategory(),
                product.getState(),
                lowStock
        );
    }
}