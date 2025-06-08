package com.example.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.elasticsearch.document.ProductDocument;
import com.example.elasticsearch.dto.ProductDTO;
import com.example.elasticsearch.entity.Product;
import com.example.elasticsearch.repository.ProductDocumentRepository;
import com.example.elasticsearch.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.elasticsearch.client.elc.Queries.matchQuery;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDocumentRepository productDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public ProductDTO saveProduct(ProductDTO productDTO) {
        // 1) Entity 변환 및 DB 저장
        Product product = Product.builder()
                .id(productDTO.getId())
                .prodName(productDTO.getProdName())
                .company(productDTO.getCompany())
                .ratingAvg(productDTO.getRatingAvg())
                .reviewCount(productDTO.getReviewCount())
                .prodPrice(productDTO.getProdPrice())
                .snameList(productDTO.getSnameList())
                .link(productDTO.getLink())
                .salesCount(productDTO.getSalesCount())
                .build();

        product = productRepository.save(product);

        // 2) Elasticsearch Document 변환 및 저장
        ProductDocument document = new ProductDocument();
        document.setId(product.getId());
        document.setProdName(product.getProdName());
        document.setCompany(product.getCompany());
        document.setRatingAvg(product.getRatingAvg());
        document.setReviewCount(product.getReviewCount());
        document.setProdPrice(product.getProdPrice());
        document.setSnameList(product.getSnameList());
        document.setLink(product.getLink());
        document.setSalesCount(product.getSalesCount());

        productDocumentRepository.save(document);

        // 3) DTO 반환
        return productDTO.toBuilder().id(product.getId()).build();
    }


    public List<String> autocomplete(String keyword) {
        // 1. Elasticsearch의 네이티브 match 쿼리를 생성합니다.
        co.elastic.clients.elasticsearch._types.query_dsl.Query esQuery = QueryBuilders.match(m -> m
                .field("product_name.autocomplete")
                .query(keyword)
        );

        // 2. Spring Data Elasticsearch의 Query 객체로 래핑합니다.
        NativeQuery query = NativeQuery.builder()
                .withQuery(esQuery)
                .withPageable(PageRequest.of(0, 10)) // 결과는 10개로 제한
                .build();

        // 3. 검색을 실행합니다.
        SearchHits<ProductDocument> searchHits = elasticsearchOperations.search((org.springframework.data.elasticsearch.core.query.Query) query, ProductDocument.class);

        // 검색 결과에서 상품명만 추출하여 반환
        return searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent().getProdName())
                .distinct() // 중복 제거
                .collect(Collectors.toList());
    }
}
