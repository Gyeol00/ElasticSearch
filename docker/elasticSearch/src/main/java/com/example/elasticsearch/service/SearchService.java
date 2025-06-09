package com.example.elasticsearch.service;

import com.example.elasticsearch.document.ProductDocument;
import com.example.elasticsearch.dto.PageRequestDTO;
import com.example.elasticsearch.dto.PageResponseDTO;
import com.example.elasticsearch.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ElasticsearchOperations esOps;

    public PageResponseDTO<ProductDTO> simpleSearchProducts(PageRequestDTO req) {
        // 1) 페이징 설정
        Pageable pageable = PageRequest.of(req.getPg() - 1, req.getSize());

        // 2) Criteria 조립: 기본 검색 조건 (빈 Criteria로 시작)
        Criteria criteria = new Criteria();

        // 3) subKeyword로 상품명과 회사명 동시 검색
        // req.getSubKeyword()에 값이 있을 때만 검색 조건을 추가합니다.
        if (StringUtils.hasText(req.getSubKeyword())) {
            String keywordToSearch = req.getSubKeyword();

            // 'prod_name' 또는 'company' 필드에서 subKeyword를 포함하는지 검색
            // 'contains'는 Elasticsearch에서 'match' 쿼리로 변환되며, 해당 필드에 적용된 분석기를 사용합니다.
            criteria = criteria.or("prod_name").contains(keywordToSearch)
                    .or("company").contains(keywordToSearch);
        }

        // 4) CriteriaQuery 생성
        CriteriaQuery query = new CriteriaQuery(criteria, pageable);

        // 5) 실행 & DTO 변환
        SearchHits<ProductDocument> hits = esOps.search(query, ProductDocument.class);
        List<ProductDTO> dtos = hits.get().map(SearchHit::getContent)
                .map(ProductDTO::fromDocument)
                .toList();
        long total = hits.getTotalHits();

        // 6) PageResponseDTO 반환
        return new PageResponseDTO<>(req, dtos, (int) total);
    }
}