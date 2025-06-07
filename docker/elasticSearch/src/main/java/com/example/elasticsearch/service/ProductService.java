package com.example.elasticsearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.elasticsearch.document.ProductDocument;
import com.example.elasticsearch.dto.ProductDTO;
import com.example.elasticsearch.entity.Product;
import com.example.elasticsearch.repository.ProductDocumentRepository;
import com.example.elasticsearch.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDocumentRepository productDocumentRepository;
    private final ElasticsearchClient elasticsearchClient;

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

    public List<String> autocompleteProdName(String prefix) throws IOException {
        SearchResponse<ProductDocument> response = elasticsearchClient.search(s -> s
                        .index("product_20240607_test")
                        .query(q -> q
                                .prefix(p -> p
                                        .field("prodName")
                                        .value(prefix)
                                )
                        )
                        .size(10),  // 최대 10개 추천
                ProductDocument.class);

        // 추천 결과에서 prodName만 추출
        return response.hits().hits().stream()
                .map(hit -> hit.source().getProdName())
                .distinct()  // 중복 제거
                .toList();
    }

}
