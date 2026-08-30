package com.vihaluxe.controller;

import com.vihaluxe.dto.CandleDesignRecommendation;
import com.vihaluxe.ai.AIRecommendation;
import com.vihaluxe.ai.GeminiService;
import com.vihaluxe.model.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AIController {

    private final GeminiService geminiService;

    public AIController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping("/ai")
    public String aiPage() {
        return "ai";
    }

    @PostMapping("/ai")
    public String askAI(@RequestParam String prompt,
                        Model model) {

        AIRecommendation result = geminiService.askGemini(prompt);

        model.addAttribute("question", prompt);

        if(result.getProduct()!=null){

            model.addAttribute("product", result.getProduct());

        }

        model.addAttribute("answer", result.getMessage());

        return "ai";
    }

    @PostMapping("/ai/chat")
    @ResponseBody
    public String chat(@RequestParam String prompt){

        AIRecommendation result = geminiService.askGemini(prompt);

        if(result.getProduct()==null){

            return """
                <div class='alert alert-warning'>
                    %s
                </div>
                """.formatted(result.getMessage());
        }

        Product p=result.getProduct();

        return """
<div class="card">

<img src="/images/%s"
class="card-img-top"
style="height:220px;object-fit:cover;">

<div class="card-body">

<h5>%s</h5>

<p>%s</p>

<p class="fw-bold text-success">
₹ %.0f
</p>

<a href="/product/%d"
class="btn btn-warning w-100">

View Product

</a>

</div>

</div>
"""
                .formatted(

                        p.getImageUrl(),
                        p.getName(),
                        result.getMessage(),
                        p.getPrice(),
                        p.getId()

                );

    }

    @PostMapping("/ai/recommend-design")
    @ResponseBody
    public CandleDesignRecommendation recommendDesign() {

        return geminiService.recommendCandleDesign();

    }

    @PostMapping("/ai/generate-message")
    @ResponseBody
    public String generateMessage(@RequestParam String fragrance,
                                  @RequestParam String labelStyle) {

        return geminiService.generateMessage(fragrance, labelStyle);

    }

}