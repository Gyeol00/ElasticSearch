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

    // 기본 생성자
    public SearchLog() {}

    // 전체 필드 생성자
    public SearchLog(String id, String query, Instant timestamp) {
        this.id = id;
        this.query = query;
        this.timestamp = timestamp;
    }

    // getters / setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
