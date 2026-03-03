package com.sena.cafeteria_crud.inventory.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sena.cafeteria_crud.inventory.dto.request.ProductRequestDTO;
import com.sena.cafeteria_crud.inventory.dto.response.ProductResponseDTO;
import com.sena.cafeteria_crud.inventory.model.Product;
import com.sena.cafeteria_crud.inventory.repository.IInventoryRepository;

@Service
public class ProductService {

    @Autowired
    private IInventoryRepository inventoryRepository;

    public List<ProductResponseDTO> getAllProducts() {
        return inventoryRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public Optional<ProductResponseDTO> getProductById(Long id) {
        return inventoryRepository.findByIdAndStateTrue(id)
                .map(this::convertToResponseDTO);
    }

    public void createProduct(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setStock(productRequestDTO.getStock());
        product.setMinStock(productRequestDTO.getMinStock());
        product.setCategory(productRequestDTO.getCategory());
        product.setState(true);
        inventoryRepository.save(product);
    }

    public void updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product product = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setStock(productRequestDTO.getStock());
        product.setMinStock(productRequestDTO.getMinStock());
        product.setCategory(productRequestDTO.getCategory());
        inventoryRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        product.setState(false);
        inventoryRepository.save(product);
    }

    public List<ProductResponseDTO> getLowStockProducts() {
        return inventoryRepository.findLowStockProducts().stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public List<ProductResponseDTO> getProductsByCategory(String category) {
        return inventoryRepository.findByCategoryAndStateTrue(category).stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public void updateStock(Long id, Integer newStock) {
        Product product = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        product.setStock(newStock);
        inventoryRepository.save(product);
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
