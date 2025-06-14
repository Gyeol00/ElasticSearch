package com.example.search.service;

import com.example.search.entity.Product;
import com.example.search.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Value;


@Slf4j
@Service
public class NaverShoppingApiService {

    @Autowired
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${naver.api.client.id}")
    private String clientId;

    @Value("${naver.api.client.secret}")
    private String clientSecret;

    public void searchAndSaveProductsForKeywords() {
        String[] keywords = {
                "메이크업", "향수", "스킨케어", "여성아우터", "여성상의",
                "여성하의", "남성셔츠", "남성슬랙스", "남성가디건", "남성베스트",
                "모바일", "테블릿", "카메라", "음향기기", "신선식품", "가공식품",
                "건강식품", "여성운동화", "남성운동화", "여성캔버스화", "남성캔버스화", "여성샌들", "남성샌들",
                "강아지", "고양이", "뜨개", "취미", "캠핑", "모자", "목걸이", "귀걸이"
        };

        for (String keyword : keywords) {
            for (int start = 1; start <= 1001; start += 100) {
                searchAndSaveProducts(keyword, 100, start);
            }

        }
    }

    public void searchAndSaveProducts(String query, int display, int start) {
        try {
            String apiURL = "https://openapi.naver.com/v1/search/shop.json?query=" +
                    java.net.URLEncoder.encode(query, "UTF-8") +
                    "&display=" + display + "&start=" + start;

            log.info("네이버 API 요청 URL: {}", apiURL);
            log.info("ClientId: '{}'", clientId);

            java.net.URL url = new java.net.URL(apiURL);
            java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            int responseCode = con.getResponseCode();
            log.info("네이버 API 응답 코드: {}", responseCode);

            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }
                br.close();
                log.error("네이버 API 에러 응답: {}", errorResponse.toString());
                return; // 에러 시 처리 종료
            }

            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            log.debug("네이버 API 응답 JSON: {}", response.toString());

            JsonNode root = objectMapper.readTree(response.toString());
            JsonNode items = root.path("items");
            log.info("네이버 API로부터 {}개 아이템 수신", items.size());

            int savedCount = 0;
            for (JsonNode item : items) {
                String title = item.path("title").asText().replaceAll("<.*?>", "");
                String link = item.path("link").asText();
                String mallName = item.path("mallName").asText("");
                int price = item.path("lprice").asInt(0);

                if (!productRepository.existsByLink(link)) {
                    Product product = Product.builder()
                            .productName(title)
                            .link(link)
                            .mallName(mallName)
                            .price(price)
                            .keyword(query)
                            .build();

                    productRepository.save(product);
                    savedCount++;

                    log.info("DB 저장 완료 - 상품명: {}, 링크: {}, 가격: {}, 키워드: {}", title, link, price, query);
                } else {
                    log.debug("이미 존재하는 상품 - 링크: {}", link);
                }
            }

            log.info("키워드 '{}' - start {}에서 저장된 상품 개수: {}", query, start, savedCount);

        } catch (Exception e) {
            log.error("검색 및 저장 중 오류 발생", e);
        }
    }

}
