package com.example.bookshop.dao;


import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemDao extends JpaRepository<com.example.bookshop.entity.OrderItem, Integer> {
}
