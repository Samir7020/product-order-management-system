package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    

}







//// Auto-calculate total BEFORE updating cart in database
//@PreUpdate
//protected void calculateTotal() {
//  this.totalAmount = items.stream()
//      .map(item -> item.getProduct().getPrice()
//          .multiply(BigDecimal.valueOf(item.getQuantity())))
//      .reduce(BigDecimal.ZERO, BigDecimal::add);
//}