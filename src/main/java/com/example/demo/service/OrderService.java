package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.CartEmptyException;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.repo.CartRepository;
import com.example.demo.repo.OrderRepository;
import com.example.demo.repo.ProductRepository;
import com.example.demo.repo.UserRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
   
    @Transactional
    public Order placeOrder(Long userId) {
       
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        
        Cart cart = cartRepository.findByUser(user)
            .orElseThrow(() -> new CartEmptyException("Cart is empty"));
        
       
        if (cart.getItems().isEmpty()) {
            throw new CartEmptyException("Cart is empty. Cannot place order.");
        }
        
        
        Order order = new Order();
        order.setUser(user);
        order.setStatus("CONFIRMED");
        order.setTotalAmount(BigDecimal.ZERO);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            
            
            if (!product.getEnabled()) {
                throw new RuntimeException("Product '" + product.getName() + "' is disabled");
            }
            
            
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock for: " + product.getName() + 
                    ". Available: " + product.getQuantity() + 
                    ", Requested: " + cartItem.getQuantity()
                );
            }
            
           
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice()); // Save price at order time
            order.getItems().add(orderItem);
            
          
            totalAmount = totalAmount.add(
                product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }
        
        
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        
        
        cart.getItems().clear();
        cartRepository.save(cart);
        
        return savedOrder;
    }
    
    
    @Transactional(readOnly = true)
    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }
    
    // Get specific order by ID
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}