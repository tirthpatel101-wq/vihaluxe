package com.vihaluxe.ai;

import com.vihaluxe.dto.CandleDesignRecommendation;
import com.vihaluxe.ai.GeminiResponse;
import com.vihaluxe.model.Product;
import com.vihaluxe.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private final ProductRepository productRepository;

    public GeminiService(RestTemplate restTemplate,
                         ProductRepository productRepository) {

        this.restTemplate = restTemplate;
        this.productRepository = productRepository;
    }

    public AIRecommendation askGemini(String userPrompt) {
        {

            // Fetch all products from database
            List<Product> products = productRepository.findAll();

            StringBuilder productList = new StringBuilder();

            for (Product p : products) {

                productList.append("Product Name: ").append(p.getName()).append("\n");
                productList.append("Category: ").append(p.getCategory()).append("\n");
                productList.append("Price: ₹").append(p.getPrice()).append("\n");
                productList.append("Description: ").append(p.getDescription()).append("\n");
                productList.append("Fragrance: ").append(p.getFragrance()).append("\n");
                productList.append("Mood: ").append(p.getMood()).append("\n");
                productList.append("Occasion: ").append(p.getOccasion()).append("\n");
                productList.append("Burn Time: ").append(p.getBurnTime()).append("\n");
                productList.append("Intensity: ").append(p.getIntensity()).append("\n\n");
            }

            // AI Prompt
            String finalPrompt = """
                    You are Viha Luxe's official AI shopping assistant.
                    
                    Your job is to recommend ONLY Viha Luxe candles.
                    
                    The products available are:
                    
                    %s
                    
                    Customer Question:
                    %s
                    
                    Instructions:
                    
                    If one product matches best, recommend ONLY that product.
                    
                    Answer in this exact JSON format:
                    
                    {
                      "message":"Short explanation why this candle suits the customer.",
                      "product":"Exact Product Name"
                    }
                    
                    Do NOT recommend products that are not listed.
                    
                    Do NOT return markdown.
                    
                    Do NOT return extra text.
                    
                    Only return valid JSON.
                    """.formatted(productList.toString(), userPrompt);

            String url =
                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                            + apiKey;

            Map<String, Object> body = Map.of(
                    "contents",
                    List.of(
                            Map.of(
                                    "parts",
                                    List.of(
                                            Map.of(
                                                    "text", finalPrompt
                                            )
                                    )
                            )
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            try {

                ResponseEntity<Map> response =
                        restTemplate.postForEntity(url, entity, Map.class);

                List<?> candidates =
                        (List<?>) response.getBody().get("candidates");

                if (candidates == null || candidates.isEmpty()) {
                    return new AIRecommendation("No AI response.", null);
                }

                Map<?, ?> candidate =
                        (Map<?, ?>) candidates.get(0);

                Map<?, ?> content =
                        (Map<?, ?>) candidate.get("content");

                List<?> parts =
                        (List<?>) content.get("parts");

                if (parts == null || parts.isEmpty()) {
                    return new AIRecommendation("No AI response.", null);
                }

                Map<?, ?> answer =
                        (Map<?, ?>) parts.get(0);

                String json = answer.get("text").toString();

                GeminiResponse aiResponse = new GeminiResponse();

                try {

                    json = json.replace("```json", "")
                            .replace("```", "")
                            .trim();

                    int msgStart = json.indexOf("\"message\":\"") + 11;
                    int msgEnd = json.indexOf("\",", msgStart);

                    int productStart = json.indexOf("\"product\":\"") + 11;
                    int productEnd = json.indexOf("\"", productStart);

                    aiResponse.setMessage(json.substring(msgStart, msgEnd));
                    aiResponse.setProduct(json.substring(productStart, productEnd));

                } catch (Exception ex) {
                    return new AIRecommendation(json, null);
                }

                Product product = productRepository
                        .findByNameIgnoreCase(aiResponse.getProduct())
                        .orElse(null);

                if (product == null) {
                    return new AIRecommendation(aiResponse.getMessage(), null);
                }

                return new AIRecommendation(
                        aiResponse.getMessage(),
                        product
                );

            } catch (Exception e) {

                e.printStackTrace();

                return new AIRecommendation(
                        e.toString(),
                        null
                );
            }
        }
    }

    public CandleDesignRecommendation recommendCandleDesign() {

        String prompt = """
            You are a luxury candle designer for Viha Luxe.

            Recommend one premium candle design.

            Return ONLY valid JSON.

            Format:

            {
              "size":"Small (100g) or Medium (200g) or Large (350g)",
              "jar":"Glass Jar or Ceramic Jar or Luxury Gold Jar or Matte Black Jar",
              "wax":"Soy Wax or Beeswax or Coconut Wax",
              "fragrance":"Lavender or Vanilla or Rose or Ocean Breeze or Citrus or Royal Oud",
              "color":"Hex Color",
              "wick":"Cotton Wick or Wooden Wick",
              "label":"Luxury or Minimal or Floral",
              "message":"Short luxury message"
            }

            Don't return markdown.

            Don't return explanation.

            Return only JSON.
            """;

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                        + apiKey;

        Map<String, Object> body = Map.of(
                "contents",
                List.of(
                        Map.of(
                                "parts",
                                List.of(
                                        Map.of(
                                                "text", prompt
                                        )
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        try {

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, entity, Map.class);

            List<?> candidates =
                    (List<?>) response.getBody().get("candidates");

            if (candidates == null || candidates.isEmpty()) {
                return null;
            }

            Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);

            Map<?, ?> content =
                    (Map<?, ?>) candidate.get("content");

            List<?> parts =
                    (List<?>) content.get("parts");

            if (parts == null || parts.isEmpty()) {
                return null;
            }

            Map<?, ?> answer =
                    (Map<?, ?>) parts.get(0);

            String json = answer.get("text").toString();

            json = json.replace("```json", "")
                    .replace("```", "")
                    .trim();

            CandleDesignRecommendation recommendation =
                    new CandleDesignRecommendation();

            recommendation.setSize(
                    extract(json, "size"));

            recommendation.setJar(
                    extract(json, "jar"));

            recommendation.setWax(
                    extract(json, "wax"));

            recommendation.setFragrance(
                    extract(json, "fragrance"));

            recommendation.setColor(
                    extract(json, "color"));

            recommendation.setWick(
                    extract(json, "wick"));

            recommendation.setLabel(
                    extract(json, "label"));

            recommendation.setMessage(
                    extract(json, "message"));

            return recommendation;

        }

        catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }

    private String extract(String json, String key) {

        try {

            String search = "\"" + key + "\":\"";

            int start = json.indexOf(search);

            if (start == -1)
                return "";

            start += search.length();

            int end = json.indexOf("\"", start);

            return json.substring(start, end);

        }

        catch (Exception e) {

            return "";

        }

    }

    public String generateMessage(String fragrance,
                                  String labelStyle) {

        String prompt = """
            You are a luxury candle gifting expert.

            Write one elegant personalized candle message.

            Candle Fragrance: %s

            Label Style: %s

            Rules:

            Maximum 25 words.

            Luxury tone.

            Emotional.

            Return ONLY the message.

            No quotes.

            No markdown.
            """.formatted(fragrance, labelStyle);

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key="
                        + apiKey;

        Map<String, Object> body = Map.of(
                "contents",
                List.of(
                        Map.of(
                                "parts",
                                List.of(
                                        Map.of(
                                                "text", prompt
                                        )
                                )
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(body, headers);

        try {

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(url, entity, Map.class);

            List<?> candidates =
                    (List<?>) response.getBody().get("candidates");

            Map<?, ?> candidate =
                    (Map<?, ?>) candidates.get(0);

            Map<?, ?> content =
                    (Map<?, ?>) candidate.get("content");

            List<?> parts =
                    (List<?>) content.get("parts");

            Map<?, ?> answer =
                    (Map<?, ?>) parts.get(0);

            return answer.get("text").toString()
                    .replace("```", "")
                    .replace("text", "")
                    .trim();

        }

        catch (Exception e) {

            e.printStackTrace();

            return "May every flicker bring warmth and happiness.";

        }

    }
}