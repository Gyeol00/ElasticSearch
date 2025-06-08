package com.example.elasticsearch.controller;

import com.example.elasticsearch.dto.ProductDTO;
import com.example.elasticsearch.service.NaverShoppingApiService;
import com.example.elasticsearch.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final ProductService productService;

    @Autowired
    private NaverShoppingApiService naverShoppingApiService;

    // 기존 단일 검색 API
    @GetMapping("/shopping")
    public String searchShopping(String query, int display, int start) {
        naverShoppingApiService.searchAndSaveProducts(query, display, start);
        return "Single query search triggered for: " + query;
    }

    // 모든 키워드에 대해 100개씩 수집 시작 API
    @GetMapping("/shopping/all")
    public String searchAllKeywords() {
        log.info("searchAllKeywords() 호출됨");
        naverShoppingApiService.searchAndSaveProductsForKeywords();
        return "Started search and save for all keywords.";
    }

    @GetMapping("/products/autocomplete")
    public List<String> autocomplete(@RequestParam String keyword) {
        return productService.autocomplete(keyword);
    }


}