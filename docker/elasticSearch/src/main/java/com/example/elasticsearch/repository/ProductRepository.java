package com.example.elasticsearch.repository;

import com.example.elasticsearch.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 주어진 링크(link)에 해당하는 상품이 이미 존재하는지 확인합니다.
     *
     * @param link 상품 상세 페이지 URL
     * @return 존재하면 true, 없으면 false
     */
    boolean existsByLink(String link);

    /**
     * 링크로 조회할 필요가 있으면 Optional<Product> 반환
     */
    Optional<Product> findByLink(String link);
}
