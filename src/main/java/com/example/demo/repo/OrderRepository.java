package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long>{
	List<Order> findByUserOrderByOrderDateDesc(User user);
}
