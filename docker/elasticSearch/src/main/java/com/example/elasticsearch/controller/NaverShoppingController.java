package com.example.elasticsearch.controller;

import com.example.elasticsearch.service.NaverShoppingApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/search")
public class NaverShoppingController {

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
}