package com.optiprice.controller;

import com.optiprice.model.MasterProduct;
import com.optiprice.repository.MasterProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller // Note: Not @RestController
@RequiredArgsConstructor
public class WebController {

    private final MasterProductRepository masterRepo;

    @GetMapping("/")
    public String home(Model model, @RequestParam(value = "query", required = false) String query) {
        if (query != null && !query.isEmpty()) {
            List<MasterProduct> results = masterRepo.findByGenericNameContainingIgnoreCase(query);
            model.addAttribute("products", results);
            model.addAttribute("searchQuery", query);
        }
        return "index";
    }

    @GetMapping("/product")
    public String productDetails(@RequestParam("id") Long id, Model model) {
        MasterProduct product = masterRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));

        model.addAttribute("master", product);



        return "details";
    }
}