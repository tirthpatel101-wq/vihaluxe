package com.vihaluxe.controller;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.vihaluxe.model.CustomCandle;
import com.vihaluxe.model.User;
import com.vihaluxe.repository.UserRepository;
import com.vihaluxe.service.CartService;
import com.vihaluxe.service.CustomCandleService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class CustomCandleController {

    private final CustomCandleService customCandleService;
    private final CartService cartService;
    private final UserRepository userRepository;

    public CustomCandleController(CustomCandleService customCandleService,
                                  CartService cartService,
                                  UserRepository userRepository) {

        this.customCandleService = customCandleService;
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping("/customize")
    public String customizePage() {
        return "customize";
    }

    @PostMapping("/customize/save")
    public String saveCustomCandle(
            Authentication authentication,

            @RequestParam String size,
            @RequestParam String jar,
            @RequestParam String wax,
            @RequestParam String fragrance,
            @RequestParam String wick,
            @RequestParam String label,
            @RequestParam String message,
            @RequestParam String color,
            @RequestParam(defaultValue = "false") boolean giftWrap,
            @RequestParam Double price,

            @RequestParam(value = "customImage", required = false)
            MultipartFile customImage
    ) throws IOException {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomCandle candle = new CustomCandle();

        candle.setUser(user);

        candle.setSize(size);
        candle.setJar(jar);
        candle.setWax(wax);
        candle.setFragrance(fragrance);
        candle.setColor(color);
        candle.setWick(wick);
        candle.setLabelStyle(label);
        candle.setPersonalizedMessage(message);
        candle.setGiftWrap(giftWrap);

        candle.setPrice(price);

        // =============================
        // SAVE CUSTOM IMAGE
        // =============================

        String imagePath = "custom-candle.jpg";

        if (customImage != null && !customImage.isEmpty()) {

            Path uploadPath = Paths.get("uploads/custom");

            Files.createDirectories(uploadPath);

            String originalName = customImage.getOriginalFilename();

            String extension = "";

            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(
                        originalName.lastIndexOf(".")
                );
            }

            String fileName =
                    UUID.randomUUID() + extension;

            Path filePath =
                    uploadPath.resolve(fileName);

            Files.copy(
                    customImage.getInputStream(),
                    filePath
            );

            imagePath =
                    "/uploads/custom/" + fileName;
        }

        candle.setImagePath(imagePath);

        candle.setCreatedAt(LocalDateTime.now());

        candle.setStatus("IN_CART");

        CustomCandle saved =
                customCandleService.save(candle);

        cartService.addCustomCandle(
                saved,
                authentication.getName()
        );

        return "redirect:/cart";
    }
}