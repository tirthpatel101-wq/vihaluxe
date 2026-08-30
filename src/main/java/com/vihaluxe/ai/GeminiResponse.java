package com.vihaluxe.ai;

public class GeminiResponse {

    private String message;
    private String product;

    public GeminiResponse() {
    }

    public GeminiResponse(String message, String product) {
        this.message = message;
        this.product = product;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
}