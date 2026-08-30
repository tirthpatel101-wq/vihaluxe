package com.vihaluxe.controller;

import com.vihaluxe.service.CartService;
import com.vihaluxe.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CartService cartService;

    public ProductController(ProductService productService,
                             CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String keyword,
                           Model model) {

        if (keyword != null && !keyword.isBlank()) {

            model.addAttribute("products",
                    productService.searchProducts(keyword));

            model.addAttribute("keyword", keyword);

        } else {

            model.addAttribute("products",
                    productService.getAllProducts());

        }

        return "products";
    }
    @GetMapping("/product/{id}")
    public String productDetails(@PathVariable Long id,
                                 Model model) {

        model.addAttribute("product",
                productService.getProductById(id));

        return "product-details";
    }

    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable Long id,
                            Authentication authentication) {

        cartService.addToCart(id, authentication.getName());

        return "redirect:/cart";
    }

}