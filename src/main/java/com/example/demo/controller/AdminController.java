package com.example.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ProductDTO;
import com.example.demo.service.ProductService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;
    
    
    @PostMapping("/products")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.addProduct(dto));
    }
    
  
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, 
                                                     @Valid @RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }
    
    
    @PatchMapping("/products/{id}/price")
    public ResponseEntity<Void> updatePrice(@PathVariable Long id, 
                                            @RequestParam BigDecimal price) {
        productService.updatePrice(id, price);
        return ResponseEntity.ok().build();
    }
    
    
    @PatchMapping("/products/{id}/quantity")
    public ResponseEntity<Void> updateQuantity(@PathVariable Long id, 
                                               @RequestParam Integer quantity) {
        productService.updateQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }
    
   
    @PatchMapping("/products/{id}/enable")
    public ResponseEntity<Void> enableProduct(@PathVariable Long id) {
        productService.enableProduct(id);
        return ResponseEntity.ok().build();
    }
    
   
    @PatchMapping("/products/{id}/disable")
    public ResponseEntity<Void> disableProduct(@PathVariable Long id) {
        productService.disableProduct(id);
        return ResponseEntity.ok().build();
    }
    
    
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
}
