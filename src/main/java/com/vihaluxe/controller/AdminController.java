package com.vihaluxe.controller;

import com.vihaluxe.model.Order;
import com.vihaluxe.model.Product;
import com.vihaluxe.repository.CartItemRepository;
import com.vihaluxe.repository.OrderRepository;
import com.vihaluxe.repository.ProductRepository;
import com.vihaluxe.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;

    public AdminController(ProductRepository productRepository,
                           UserRepository userRepository,
                           OrderRepository orderRepository,
                           CartItemRepository cartItemRepository) {

        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @GetMapping("")
    public String dashboard(Model model) {

        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalOrders", orderRepository.count());

        double revenue = orderRepository.findAll()
                .stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();

        model.addAttribute("totalRevenue", revenue);

        model.addAttribute("recentOrders", orderRepository.findAll());

        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products(Model model) {

        model.addAttribute("products", productRepository.findAll());

        return "admin/products";
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {

        model.addAttribute("orders", orderRepository.findAll());

        return "admin/orders";
    }

    @PostMapping("/orders/update/{id}")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam("status") String status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);

        orderRepository.save(order);

        return "redirect:/admin/orders";
    }

    @GetMapping("/products/add")
    public String addProductForm(Model model) {

        model.addAttribute("product", new Product());

        return "admin/add-product";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product) {

        productRepository.save(product);

        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id,
                              Model model) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        model.addAttribute("product", product);

        return "admin/edit-product";
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Product product) {

        product.setId(id);

        productRepository.save(product);

        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStock(0);

        productRepository.save(product);

        return "redirect:/admin/products";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {

        model.addAttribute("users", userRepository.findAll());

        return "admin/users";
    }

}