package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CartItemDTO {
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be at least 1")
    private Integer quantity;
}