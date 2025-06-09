package com.example.elasticsearch.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prodName;

    private String company;

    private Double ratingAvg;

    private Integer reviewCount = 0;

    private Integer prodPrice;

    private String snameList;

    private String link;

    private Integer salesCount = 0;
}
