package com.vihaluxe.service;

import com.vihaluxe.model.*;
import com.vihaluxe.repository.CartItemRepository;
import com.vihaluxe.repository.CartRepository;
import com.vihaluxe.repository.OrderRepository;
import com.vihaluxe.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(UserRepository userRepository,
                            CartRepository cartRepository,
                            CartItemRepository cartItemRepository,
                            OrderRepository orderRepository) {

        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void placeOrder(String email, String shippingAddress) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Order order = new Order();

        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Processing");
        order.setShippingAddress(shippingAddress);

        double total = 0;

        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setQuantity(cartItem.getQuantity());

            // =============================
            // NORMAL PRODUCT
            // =============================

            if (cartItem.getProduct() != null) {

                orderItem.setProduct(cartItem.getProduct());

                orderItem.setPrice(
                        cartItem.getProduct().getPrice()
                );

                total += cartItem.getProduct().getPrice()
                        * cartItem.getQuantity();
            }

            // =============================
            // CUSTOM CANDLE
            // =============================

            else if (cartItem.getCustomCandle() != null) {

                orderItem.setCustomCandle(
                        cartItem.getCustomCandle()
                );

                orderItem.setPrice(
                        cartItem.getCustomCandle().getPrice()
                );

                total += cartItem.getCustomCandle().getPrice()
                        * cartItem.getQuantity();
            }

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(total);

        orderRepository.save(order);

        cartItemRepository.deleteAll(cart.getItems());

    }

    @Override
    public List<Order> getOrders(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }
}