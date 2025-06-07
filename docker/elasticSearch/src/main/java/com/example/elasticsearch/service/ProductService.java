package com.example.elasticsearch.service;

import com.example.elasticsearch.document.ProductDocument;
import com.example.elasticsearch.dto.ProductDTO;
import com.example.elasticsearch.entity.Product;
import com.example.elasticsearch.repository.ProductDocumentRepository;
import com.example.elasticsearch.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDocumentRepository productDocumentRepository;

    public ProductService(ProductRepository productRepository,
                          ProductDocumentRepository productDocumentRepository) {
        this.productRepository = productRepository;
        this.productDocumentRepository = productDocumentRepository;
    }

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

    // 검색, 조회 기능 등 추가 가능
}
