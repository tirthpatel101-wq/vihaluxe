package com.vihaluxe.controller;

import com.vihaluxe.dto.UserRegistrationDto;
import com.vihaluxe.service.PasswordResetService;
import com.vihaluxe.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    public AuthController(
            UserService userService,
            PasswordResetService passwordResetService) {

        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {

        model.addAttribute(
                "user",
                new UserRegistrationDto()
        );

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user")
            UserRegistrationDto registrationDto) {

        userService.registerUser(registrationDto);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {

        return "login";
    }

    @GetMapping("/profile")
    public String profile(
            Authentication authentication,
            Model model) {

        model.addAttribute(
                "email",
                authentication.getName()
        );

        return "profile";
    }


    // ==========================================
    // FORGOT PASSWORD
    // ==========================================

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {

        return "forgot-password";
    }


    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestParam String email,
            Model model) {

        passwordResetService.createResetToken(email);

        model.addAttribute(
                "message",
                "If an account exists with this email, "
                        + "a password reset link has been sent."
        );

        return "forgot-password";
    }


    // ==========================================
    // RESET PASSWORD PAGE
    // ==========================================

    @GetMapping("/reset-password")
    public String resetPasswordPage(
            @RequestParam String token,
            Model model) {

        if (!passwordResetService.isValidToken(token)) {

            model.addAttribute(
                    "error",
                    "This password reset link is invalid or has expired."
            );

            return "reset-password";
        }

        model.addAttribute("token", token);

        return "reset-password";
    }


    // ==========================================
    // RESET PASSWORD
    // ==========================================

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {

            model.addAttribute(
                    "error",
                    "Passwords do not match."
            );

            model.addAttribute("token", token);

            return "reset-password";
        }

        try {

            passwordResetService.resetPassword(
                    token,
                    password
            );

            return "redirect:/login?resetSuccess";

        } catch (Exception e) {

            model.addAttribute(
                    "error",
                    "Invalid or expired reset link."
            );

            return "reset-password";
        }
    }
}