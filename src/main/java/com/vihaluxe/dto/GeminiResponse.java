package com.vihaluxe.dto;

import java.util.List;

public class GeminiResponse {

    private String message;

    private List<String> products;

    public GeminiResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }
}
