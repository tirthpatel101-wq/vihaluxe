package com.vihaluxe.service;

import com.vihaluxe.model.CustomCandle;

import com.vihaluxe.model.Cart;

public interface CartService {

    void addToCart(Long productId, String email);

    void addCustomCandle(CustomCandle customCandle, String email);

    Cart getCart(String email);

    void increaseQuantity(Long cartItemId);

    void decreaseQuantity(Long cartItemId);

    void removeItem(Long cartItemId);
}