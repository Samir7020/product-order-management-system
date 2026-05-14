package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{
	Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
