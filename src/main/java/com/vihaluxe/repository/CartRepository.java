package com.vihaluxe.repository;

import com.vihaluxe.model.Cart;
import com.vihaluxe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

}