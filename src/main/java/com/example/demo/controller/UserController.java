package com.example.demo.controller;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Order;
import com.example.demo.security.SecurityUtils;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final SecurityUtils securityUtils;
    
   
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> viewProducts() {
        return ResponseEntity.ok(productService.getEnabledProducts());
    }
    
   
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> viewProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
   
    @PostMapping("/cart")
    public ResponseEntity<Void> addToCart(@Valid @RequestBody CartItemDTO dto) {
        Long userId = securityUtils.getCurrentUserId();
        cartService.addToCart(userId, dto);
        return ResponseEntity.ok().build();
    }
    
  
    @PutMapping("/cart/{productId}")
    public ResponseEntity<Void> updateCartItem(@PathVariable Long productId,
                                               @RequestParam Integer quantity) {
        Long userId = securityUtils.getCurrentUserId();
        cartService.updateCartItem(userId, productId, quantity);
        return ResponseEntity.ok().build();
    }
    

    @DeleteMapping("/cart/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long productId) {
        Long userId = securityUtils.getCurrentUserId();
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }
    
  
    @GetMapping("/cart")
    public ResponseEntity<Cart> viewCart() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }
    
  
    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(orderService.placeOrder(userId));
    }
    
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> viewOrders() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }
    

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> viewOrder(@PathVariable Long orderId) {
        Long userId = securityUtils.getCurrentUserId();
        
        Order order = orderService.getOrderByIdForUser(orderId, userId);
        return ResponseEntity.ok(order);
    }
}