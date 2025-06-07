package com.example.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.time.Instant;
@Document(indexName = "search_logs")
public class SearchLog {
    @Id
    private String id;
    @Field(type = FieldType.Keyword)
    private String query;
    @Field(type = FieldType.Date)
    private Instant timestamp;
    // getters & setters
}