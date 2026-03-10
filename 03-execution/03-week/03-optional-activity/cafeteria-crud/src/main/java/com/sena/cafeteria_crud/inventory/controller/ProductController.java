package com.sena.cafeteria_crud.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sena.cafeteria_crud.inventory.dto.request.ProductRequestDTO;
import com.sena.cafeteria_crud.inventory.service.impl.ProductService;

@RestController
@RequestMapping("/api/inventory/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<Object> getAllProducts() {
        var products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Obtener producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        var product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // Crear nuevo producto
    @PostMapping
    public String createProduct(@Validated @RequestBody ProductRequestDTO productRequestDTO) {
        productService.createProduct(productRequestDTO);
        return "Producto creado exitosamente";
    }

    // Actualizar producto
    @PutMapping("/{id}")
    public boolean updateProduct(@PathVariable Long id, @Validated @RequestBody ProductRequestDTO productRequestDTO) {
        productService.updateProduct(id, productRequestDTO);
        return true;
    }

    // Eliminar producto (soft delete)
    @DeleteMapping("/{id}")
    public boolean deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return true;
    }

    // Obtener productos con stock bajo
    @GetMapping("/low-stock")
    public ResponseEntity<Object> getLowStockProducts() {
        var products = productService.getLowStockProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Obtener productos por categoría
    @GetMapping("/category/{category}")
    public ResponseEntity<Object> getProductsByCategory(@PathVariable String category) {
        var products = productService.getProductsByCategory(category);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Actualizar solo el stock
    @PatchMapping("/{id}/stock")
    public boolean updateStock(@PathVariable Long id, @RequestParam Integer newStock) {
        productService.updateStock(id, newStock);
        return true;
    }
}