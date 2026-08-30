package com.vihaluxe.repository;

import com.vihaluxe.model.Cart;
import com.vihaluxe.model.CartItem;
import com.vihaluxe.model.Product;
import com.vihaluxe.model.CustomCandle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Used while adding products to cart
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    Optional<CartItem> findByCartAndCustomCandle(
            Cart cart,
            CustomCandle customCandle
    );

    // Used by Admin when deleting a product
    @Transactional
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.product.id = :productId")
    void deleteByProductId(Long productId);

}