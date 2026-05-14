package com.example.demo.controller;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Order;
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
    
    // View only enabled products
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> viewProducts() {
        return ResponseEntity.ok(productService.getEnabledProducts());
    }
    
    // View single product
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> viewProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    // Add to cart
    @PostMapping("/{userId}/cart")
    public ResponseEntity<Void> addToCart(@PathVariable Long userId, 
                                          @Valid @RequestBody CartItemDTO dto) {
        cartService.addToCart(userId, dto);
        return ResponseEntity.ok().build();
    }
    
    // Update cart item quantity
    @PutMapping("/{userId}/cart/{productId}")
    public ResponseEntity<Void> updateCartItem(@PathVariable Long userId, 
                                               @PathVariable Long productId,
                                               @RequestParam Integer quantity) {
        cartService.updateCartItem(userId, productId, quantity);
        return ResponseEntity.ok().build();
    }
    
    // Remove item from cart
    @DeleteMapping("/{userId}/cart/{productId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long userId, 
                                               @PathVariable Long productId) {
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.noContent().build();
    }
    
    // View cart
    @GetMapping("/{userId}/cart")
    public ResponseEntity<Cart> viewCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }
    
    // Place order (TRANSACTIONAL)
    @PostMapping("/{userId}/orders")
    public ResponseEntity<Order> placeOrder(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.placeOrder(userId));
    }
    
    // View all orders
    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<Order>> viewOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }
    
    // View single order
    @GetMapping("/{userId}/orders/{orderId}")
    public ResponseEntity<Order> viewOrder(@PathVariable Long userId, 
                                           @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }
}