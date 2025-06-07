package com.example.elasticsearch.repository;

import com.example.elasticsearch.document.SearchLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchLogRepository extends ElasticsearchRepository<SearchLog, String> {
}
