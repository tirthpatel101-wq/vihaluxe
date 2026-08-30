package com.vihaluxe.ai;

import com.vihaluxe.model.Product;

public class AIRecommendation {

    private String message;
    private Product product;

    public AIRecommendation() {
    }

    public AIRecommendation(String message, Product product) {
        this.message = message;
        this.product = product;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}