package com.vihaluxe.controller;

import com.vihaluxe.service.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/cart")
    public String cart(Authentication authentication,
                       Model model) {

        model.addAttribute("cart",
                cartService.getCart(authentication.getName()));

        return "cart";
    }

    @PostMapping("/cart/increase/{id}")
    public String increase(@PathVariable Long id) {

        cartService.increaseQuantity(id);

        return "redirect:/cart";
    }

    @PostMapping("/cart/decrease/{id}")
    public String decrease(@PathVariable Long id) {

        cartService.decreaseQuantity(id);

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{id}")
    public String remove(@PathVariable Long id) {

        cartService.removeItem(id);

        return "redirect:/cart";
    }
}