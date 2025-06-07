package com.example.elasticsearch.dto;

import co.elastic.clients.elasticsearch.ml.DataframeAnalyticsSummary;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
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

}
