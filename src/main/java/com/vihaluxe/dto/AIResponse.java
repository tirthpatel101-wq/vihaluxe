package com.vihaluxe.dto;

import com.vihaluxe.model.Product;
import java.util.List;

public class AIResponse {

    private String message;

    private List<Product> products;

    public AIResponse() {
    }

    public AIResponse(String message, List<Product> products) {
        this.message = message;
        this.products = products;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}