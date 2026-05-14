package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.repo.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    
    
    @Transactional
    public ProductDTO addProduct(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setEnabled(dto.getEnabled() != null ? dto.getEnabled() : true);
        
        return convertToDTO(productRepository.save(product));
    }
    
    
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getQuantity() != null) product.setQuantity(dto.getQuantity());
        if (dto.getEnabled() != null) product.setEnabled(dto.getEnabled());
        
        return convertToDTO(productRepository.save(product));
    }
    
   
    @Transactional
    public void updatePrice(Long id, BigDecimal price) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        product.setPrice(price);
        productRepository.save(product);
    }
    
    
    @Transactional
    public void updateQuantity(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        product.setQuantity(quantity);
        productRepository.save(product);
    }
    
   
    @Transactional
    public void enableProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        product.setEnabled(true);
        productRepository.save(product);
    }
    
   
    @Transactional
    public void disableProduct(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        product.setEnabled(false);
        productRepository.save(product);
    }
    
  
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getEnabledProducts() {
        return productRepository.findByEnabledTrue().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return convertToDTO(product);
    }
    
    
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setEnabled(product.getEnabled());
        return dto;
    }
}