package com.example.elasticsearch.dto;

import com.example.elasticsearch.document.ProductDocument;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductDTO {
    private Long id;
    private String prodName;
    private String company;
    private Double ratingAvg;
    private Integer reviewCount;
    private Integer prodPrice;
    private String snameList;
    private String link;
    private Integer salesCount;



    public static ProductDTO fromDocument(ProductDocument productDocument) {
        if (productDocument == null) {
            return null;
        }
        return ProductDTO.builder()
                .id(productDocument.getId())
                .prodName(productDocument.getProdName())
                .company(productDocument.getCompany())
                .ratingAvg(productDocument.getRatingAvg())
                .reviewCount(productDocument.getReviewCount())
                .prodPrice(productDocument.getProdPrice())
                .snameList(productDocument.getSnameList())
                .link(productDocument.getLink())
                .salesCount(productDocument.getSalesCount())
                .build();
    }
}
