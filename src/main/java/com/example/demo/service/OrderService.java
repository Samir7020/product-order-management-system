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
import com.example.demo.repo.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    
   
    @Transactional
    public Order placeOrder(Long userId) {
       return placeOrderWithPartial(userId);
    }
       
    
	 @Transactional
   public Order placeOrderWithPartial(Long userId) {
     
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
       List<String> adjustmentMessages = new ArrayList<>();
       List<CartItem> itemsToRemove = new ArrayList<>();
       
       
       for (CartItem cartItem : cart.getItems()) {
           Product product = cartItem.getProduct();
           int requestedQuantity = cartItem.getQuantity();
           int availableQuantity = product.getQuantity();
           
          
           if (!product.getEnabled()) {
               throw new RuntimeException("Product '" + product.getName() + "' is disabled");
           }
           
         
           int finalQuantity = Math.min(requestedQuantity, availableQuantity);
           
         
           if (finalQuantity == 0) {
               adjustmentMessages.add("Product '" + product.getName() + 
                   "' is out of stock. Removed from Order.");
               itemsToRemove.add(cartItem);
               continue;
           }
           
           
           if (finalQuantity < requestedQuantity) {
               adjustmentMessages.add("Product '" + product.getName() + 
                   "': Requested " + requestedQuantity + 
                   ", Only " + finalQuantity + " available. Ordered : " + finalQuantity + ".");
           }
           
          
           product.setQuantity(availableQuantity - finalQuantity);
           productRepository.save(product);
           
           
           OrderItem orderItem = new OrderItem();
           orderItem.setOrder(order);
           orderItem.setProduct(product);
           orderItem.setQuantity(finalQuantity);
           orderItem.setPrice(product.getPrice());
           order.getItems().add(orderItem);
           
           
           totalAmount = totalAmount.add(
               product.getPrice().multiply(BigDecimal.valueOf(finalQuantity))
           );
           
           
           if (finalQuantity < requestedQuantity) {
               if (finalQuantity == 0) {
                   itemsToRemove.add(cartItem);
               } else {
                   cartItem.setQuantity(finalQuantity);
                   cartItemRepository.save(cartItem);
               }
           }
       }
       
       if (order.getItems().isEmpty()) {
           throw new InsufficientStockException("No items available in stock. Cannot place order.");
       }
       
       order.setTotalAmount(totalAmount);
       Order savedOrder = orderRepository.save(order);
       
       
       for(CartItem item : itemsToRemove) {
    	   cart.getItems().remove(item);
    	   cartItemRepository.delete(item);
       }
       
       
       cart.getItems().removeAll(itemsToRemove);
       cartRepository.save(cart);
       
       if (!adjustmentMessages.isEmpty()) {
           System.out.println("\n========== ORDER ADJUSTMENTS ============");
           for (String msg : adjustmentMessages) {
               System.out.println(msg);
           }
           System.out.println("========================================\n");
       }
       
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
    
    @Transactional(readOnly = true)
    public Order getOrderByIdForUser(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only access your own orders");
        }
        
        return order;
    }
}