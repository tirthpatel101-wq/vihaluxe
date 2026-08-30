package com.vihaluxe.controller;

import com.vihaluxe.model.Cart;
import com.vihaluxe.service.CartService;
import com.vihaluxe.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderController {

    private final CartService cartService;
    private final OrderService orderService;

    public OrderController(CartService cartService,
                           OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public String checkout(Authentication authentication,
                           Model model) {

        Cart cart = cartService.getCart(authentication.getName());

        model.addAttribute("cart", cart);

        return "checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(Authentication authentication,
                             @RequestParam("shippingAddress") String shippingAddress) {

        orderService.placeOrder(authentication.getName(), shippingAddress);

        return "redirect:/order-success";
    }

    @GetMapping("/order-success")
    public String success() {
        return "order-success";
    }

    @GetMapping("/orders")
    public String orders(Authentication authentication,
                         Model model) {

        model.addAttribute("orders",
                orderService.getOrders(authentication.getName()));

        return "orders";
    }
}