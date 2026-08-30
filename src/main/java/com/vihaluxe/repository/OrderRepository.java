package com.vihaluxe.repository;

import com.vihaluxe.model.Order;
import com.vihaluxe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByOrderDateDesc(User user);

}