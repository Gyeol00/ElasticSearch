package com.example.elasticsearch.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "product_20240607_test")
public class ProductDocument {

    @Id
    private Long id;

    @Field(name = "prodName", type = FieldType.Text,
            analyzer = "autocomplete_analyzer",
            searchAnalyzer = "standard")
    private String prodName;

    @Field(name = "company", type = FieldType.Keyword)
    private String company;

    @Field(name = "ratingAvg", type = FieldType.Double)
    private Double ratingAvg;

    @Field(name = "reviewCount", type = FieldType.Integer)
    private Integer reviewCount;

    @Field(name = "prodPrice", type = FieldType.Integer)
    private Integer prodPrice;

    @Field(name = "snameList", type = FieldType.Keyword)
    private String snameList;

    @Field(name = "link", type = FieldType.Keyword)
    private String link;

    @Field(name = "salesCount", type = FieldType.Integer)
    private Integer salesCount;

    // 기본 생성자
    public ProductDocument() {
    }
}