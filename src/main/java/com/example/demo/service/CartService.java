package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.repo.CartItemRepository;
import com.example.demo.repo.CartRepository;
import com.example.demo.repo.ProductRepository;
import com.example.demo.repo.UserRepository;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
    // USER: Add product to cart
    @Transactional
    public void addToCart(Long userId, CartItemDTO dto) {
      
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
       
        Product product = productRepository.findById(dto.getProductId())
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        
        
        if (!product.getEnabled()) {
            throw new RuntimeException("Product is disabled");
        }
        
       
        if (product.getQuantity() < dto.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock. Available: " + product.getQuantity());
        }
        
      
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
        
       
        CartItem existingItem = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);
        
        if (existingItem != null) {
            
            int newQuantity = existingItem.getQuantity() + dto.getQuantity();
            if (product.getQuantity() < newQuantity) {
                throw new InsufficientStockException("Insufficient stock for total requested quantity");
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(dto.getQuantity());
            cart.getItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }
    }
    
   
    @Transactional
    public void updateCartItem(Long userId, Long productId, Integer quantity) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Cart not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        
        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
            .orElseThrow(() -> new RuntimeException("Item not in cart"));
        
        if (quantity <= 0) {
           
            cart.getItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            
            if (product.getQuantity() < quantity) {
                throw new InsufficientStockException("Insufficient stock");
            }
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }
    
   
    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        updateCartItem(userId, productId, 0);
    }
    
    // USER: View cart
    @Transactional(readOnly = true)
    public Cart getCart(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return cartRepository.findByUser(user).orElse(new Cart());
    }
}
