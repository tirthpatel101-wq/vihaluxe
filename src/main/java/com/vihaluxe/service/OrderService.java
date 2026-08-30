package com.vihaluxe.service;

import com.vihaluxe.model.Order;

import java.util.List;

public interface OrderService {

    void placeOrder(String email, String shippingAddress);

    List<Order> getOrders(String email);

}
