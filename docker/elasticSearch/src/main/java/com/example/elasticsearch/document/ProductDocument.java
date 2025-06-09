package com.example.elasticsearch.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "product_*")
public class ProductDocument {

    @Id
    private Long id;

    @Field(name = "prod_name", type = FieldType.Text, analyzer = "nori_analyzer")
    private String prodName;

    @Field(name = "company", type = FieldType.Text, analyzer = "nori_analyzer")
    private String company;

    @Field(name = "rating_avg", type = FieldType.Double)
    private Double ratingAvg;

    @Field(name = "review_count", type = FieldType.Integer)
    private Integer reviewCount;

    @Field(name = "prod_price", type = FieldType.Integer)
    private Integer prodPrice;

    @Field(name = "sname_list", type = FieldType.Text)
    private String snameList;

    @Field(name = "link", type = FieldType.Text)
    private String link;

    @Field(name = "sales_count", type = FieldType.Integer) // <-- 이 부분 중요
    private Integer salesCount;

}