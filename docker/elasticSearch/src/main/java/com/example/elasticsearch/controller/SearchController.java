package com.example.elasticsearch.controller;

import com.example.elasticsearch.dto.PageRequestDTO;
import com.example.elasticsearch.dto.PageResponseDTO;
import com.example.elasticsearch.dto.ProductDTO;
import com.example.elasticsearch.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;



@Slf4j
@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class SearchController {

   private final SearchService searchService;

    // 검색 페이지 진입 (뷰만)
    @GetMapping("/search")
    public String search() {
        return "/searchList";
    }


    // Ajax용 데이터 페칭
    @GetMapping("/ajaxSearchList")
    @ResponseBody
    public PageResponseDTO<ProductDTO> ajaxSearchList(
            PageRequestDTO pageRequestDTO,
            @RequestParam(value = "view", defaultValue = "list") String view
    ) {
        log.info("PageRequestDTO: {}", pageRequestDTO);
        log.info("view: {}", view);

        PageResponseDTO pageResponseDTO = searchService.simpleSearchProducts(pageRequestDTO);

        log.info("pageResponseDTO: {}", pageResponseDTO);

        pageResponseDTO.setView(view);
        pageResponseDTO.setSortType(pageRequestDTO.getSortType());
        pageResponseDTO.setPeriod(pageRequestDTO.getPeriod());

        return pageResponseDTO;
    }

}

