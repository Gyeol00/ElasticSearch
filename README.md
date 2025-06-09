# Naver Shopping Data Crawling & Search Platform

## ✅ 프로젝트 개요
네이버 공식 쇼핑 API를 활용한 상품 데이터 수집 후 MySQL에 저장하고, Logstash를 통해 실시간으로 Elasticsearch에 적재합니다.  
Elasticsearch에는 노리(Nori) 형태소 분석기를 적용해 정교한 한글 기반 검색 기능을 구현하였으며, Spring Boot 기반 REST API로 Elasticsearch 검색 기능을 클라이언트 화면에 연동했습니다.<br>
또한, Kibana 대시보드를 통해 데이터 모니터링과 시각화가 가능합니다.<br>
**총 3만 건의 데이터를 약 1.65초 만에 처리하여 빠르고 안정적인 검색 성능을 확인했습니다.**

## 1️⃣ 주요 기능
- 네이버 공식 쇼핑 API를 활용한 상품 데이터 수집
- 수집한 데이터를 MySQL에 실시간 저장
- Logstash를 통한 데이터 적재 자동화 및 처리 시간 단축
- Elasticsearch 기반 실시간 검색 및 노리 형태소 분석 적용
- Spring Boot 기반 API로 클라이언트 화면에 검색 결과 연동
- Kibana 대시보드로 시각화 및 모니터링
- (개발중) Edge NGram 자동 연관 검색어 기능

## 2️⃣ 아키텍처 및 기술 스택
- Docker 기반 컨테이너 구성: MySQL, Logstash, Elasticsearch, Spring Boot API, Kibana
- 기술 스택: Java, Spring Boot, Elasticsearch (노리 형태소 분석기), Logstash, Docker, Kibana
- 데이터 흐름: 네이버 쇼핑 API 호출 → MySQL 저장 → Logstash → Elasticsearch(노리 분석기 적용) 적재 → Kibana 시각화 → Spring API로 클라이언트 화면에 검색 결과 노출

## 3️⃣ 실행 및 설치 방법
### Docker-compose 실행 방법
레포지토리 클론 후 search/docker 폴더 안에서 터미널 연 후, docker-compose up -d 명령어 실행
### kibana 접속 방법
docker 실행 후 https://localhost:5601 검색, 접속되면 kibana 연결 완료

## 4️⃣ ElasticSearch 인덱스 및 노리 형태소 분석기 설정
1. **MySQL 데이터 관리**
   - 수집한 데이터를 저장할 테이블 설계 및 생성

2. **Logstash를 통한 실시간 데이터 적재**
   - pipeline 폴더 안 logstash.conf (input, filter, output 등) 설정
   - JDBC input 플러그인을 사용해 MySQL 데이터 실시간 수집
   - 필터를 활용한 데이터 정제 및 변환 (필요 시)
   - Elasticsearch output 플러그인을 통해 데이터 적재  
   - Docker Compose 환경에서 Logstash 서비스 구성 및 연동  

3. **Elasticsearch 노리 형태소 분석기 적용**
   - Elasticsearch에 `analysis-nori` 플러그인 설치 및 활성화  
   - 한글 형태소 분석을 위한 커스텀 분석기 설정 포함 인덱스 생성  
   - 노리 분석기가 적용된 필드 매핑 지정  
   - 정교한 한글 텍스트 검색 기능 구현  

4. **Kibana 시각화**
   <!-- - Elasticsearch 인덱스 연동  -->
   - kibana 명령어를 통한 실시간 데이터 시각화 
   <!-- - 모니터링 알림 및 경고 설정 (필요 시) -->

## 5️⃣ 주요 화면 및 결과
### 정상적으로 문서가 색인되어 있고, 검색이 가능한 상태인지 확인
**GET product_20240607_test/_search
{
  "query": {
    "match_all": {}
  },
  "size": 1
}**
<br>위 명령어로 kibana에서 확인 시
<img width="1352" alt="스크린샷 2025-06-08 오후 4 18 36" src="https://github.com/user-attachments/assets/009e5e06-c19c-461c-a9c8-502c42a2d1bc" />
<img width="1352" alt="스크린샷 2025-06-08 오후 4 18 48" src="https://github.com/user-attachments/assets/17f606be-66cd-43f5-ad05-3aa864eef6e2" />
위와 같은 결과를 확인 할 수 있으며 **1건만 조회한 결과** 데이터가 잘 나오는 것을 확인할 수 있음

---

### Logstash를 이용한 실시간 데이터 적재 및 시간 단축
**(GET product_20240607_test/_stats/indexing?pretty)**
위 명령어로 kibana에서 확인 시
<img width="1352" alt="스크린샷 2025-06-08 오후 4 05 49" src="https://github.com/user-attachments/assets/d9a9e0eb-6447-4e50-aa1d-a978d2399ff0" />
위와 같은 결과를 확인 할 수 있음
| 항목                              | 설명                                 |
| ------------------------------- | ---------------------------------- |
| `"index_total": 277938`         | 지금까지 색인된 문서 총 개수 (약 27만 건)         |
| `"index_time_in_millis": 15291` | 총 색인 작업에 걸린 누적 시간 (15.2초)          |
| `"index_current": 0`            | 현재 진행 중인 색인 작업 수 (0 → 현재 대기 상태)    |
| `"index_failed": 0`             | 색인 실패 건수 (정상적으로 잘 처리 중)            |
| `"is_throttled": false`         | 색인 제한(Throttling) 여부 (없음)          |
| `"write_load": 0.02711...`      | Elasticsearch의 쓰기 부하(Load) 지표 (낮음) |

➡️ 현재 **Logstash가 계속 MySQL 소스에서 데이터를 수집하여 Elasticsearch로 색인 중**이며 실패없이 잘 돌아가고 있음을 확인할 수 있음

**그렇다면, 3만건 데이터를 넣을 때 걸리는 시간은?** <br>
index_total: 277,938건<br>
index_time_in_millis: 15,291ms (즉, 15.291초)

**1건 처리 시간** <br>
277938 건<br>
15291 ms<br>
​≈0.055 ms/건

**3만건 처리 시간** <br>
30,000 건×0.055 ms≈1,650 ms=약 1.65초

#### ⭐️ 결론, **3만 건의 데이터를 처리하는 데 약 1.65초 정도 소요**

---

### 사용자 검색 화면 구성 및 검색 결과 출력
#### ➡️ "운동" 검색 시 운동 키워드가 들어간 상품들이 검색되며 검색창 텍스트 옆 상품 총 갯수가 뜨는 것을 확인할 수 있음
![_______________________________2025-06-09_________________9 53 23_720](https://github.com/user-attachments/assets/9bbb45bd-3232-408b-b45d-5cd68746b6e2)
![_______________________________2025-06-09_________________9 54 03_720](https://github.com/user-attachments/assets/3cdcf97e-6f6b-4301-aafd-f658bce8d894)

#### ➡️ 페이징 처리
![_______________________________2025-06-09_________________9 54 12_720](https://github.com/user-attachments/assets/59f62ba8-26b9-447c-94c3-96a0aa70f29c)
![_______________________________2025-06-09_________________9 54 19_720](https://github.com/user-attachments/assets/49c7dcd2-6256-4a30-b412-118090d9d76b)

#### ➡️ "귀걸" 검색 시 귀걸 키워드가 들어간 상품들이 검색됨
![_______________________________2025-06-09_________________9 54 44_720](https://github.com/user-attachments/assets/47757e66-bb1b-48bb-8680-0c18bb620a50)
![_______________________________2025-06-09_________________9 54 53_720](https://github.com/user-attachments/assets/38f391f7-7a61-4eec-87fb-6dd90d9c8ae8)



---

### (개발중) Edge NGram 연관 검색어 - 현재 Edge NGram 인덱스 생성 및 적용까지 완료되어 있으며, API 사용하여 클라이언트 화면에서 검색 시 연관 검색어 뜨면 구현 완료

### Edge N-Gram Tokenization 결과

**GET product_20240607_test/_analyze
{
  "analyzer": "autocomplete_analyzer",
  "text": "스와로브스키"
}**
kibana에서 위 명령어 입력 시
<img width="1352" alt="스크린샷 2025-06-08 오후 4 23 25" src="https://github.com/user-attachments/assets/de32991a-badd-4121-a100-6bd15f7860bf" />
<img width="1352" alt="스크린샷 2025-06-08 오후 4 23 36" src="https://github.com/user-attachments/assets/a9075199-14e0-4179-be72-a41713434081" />
위와 같은 결과를 볼 수 있으며, Edge NGram 기반으로 위와 같이 토큰화되어 **사용자가 "스"만 입력해도 "스와로브스키"와 매칭 가능하게 동작함**

### 아래는 ElasticSearch에 적재되어 있는 데이터로 확인한 결과 (캡쳐본은 결과의 일부)
**GET product_20240607_test/_search
{
  "query": {
    "match": {
      "product_name.autocomplete": "이지"
    }
  }
}**
<img width="1352" alt="스크린샷 2025-06-08 오후 4 29 39" src="https://github.com/user-attachments/assets/d31c22d9-7bcf-4efd-99c8-f4c791985074" />
<img width="1352" alt="스크린샷 2025-06-08 오후 4 29 49" src="https://github.com/user-attachments/assets/b79a49b5-3489-4f8e-873d-573ade121fff" />

### 아래와 같이 데이터 검색 결과 확인 가능 (max 10)
| 제품명                                                                                                                 | 가격      | 리뷰 수 | 평점    | 판매 수  | 판매처           | 링크                                                                                                             |
| ------------------------------------------------------------------------------------------------------------------- | ------- | ---- | ----- | ----- | ------------- | -------------------------------------------------------------------------------------------------------------- |
| ![이미지](https://shopping-phinf.pstatic.net/main_5270651/52706514198.1.jpg) 이지듀 병원전용 이지듀 MD 보습크림 85g                  | 23,800원 | 423  | ⭐ 1.5 | 1,700 | G마켓           | [바로가기](https://link.gmarket.co.kr/gate/pcs?item-no=2344993917&sub-id=1003&service-code=10000003&lcd=100000005) |
| ![이미지](https://shopping-phinf.pstatic.net/main_5192946/51929469078.20241213220153.jpg) 이지듀 디더블유 이지에프 크림 화이트 토닝 50ml | 11,700원 | 835  | ⭐ 4.0 | 1,343 | 네이버           | [바로가기](https://search.shopping.naver.com/catalog/51929469078)                                                  |
| ![이미지](https://shopping-phinf.pstatic.net/main_5418601/54186015008.jpg) 이지드로잉 태블릿 1060 PLUS 온라인 수업                  | 48,900원 | 72   | ⭐ 1.3 | 3,775 | 옥션            | [바로가기](https://link.auction.co.kr/gate/pcs?item-no=B562737270&sub-id=1&service-code=10000003)                  |
| ![이미지](https://shopping-phinf.pstatic.net/main_8900308/89003088861.jpg) 이지듀 새살연고 egf 재생크림                           | 19,500원 | 360  | ⭐ 3.2 | 661   | 라벤더홀릭         | [바로가기](https://smartstore.naver.com/main/products/11458578496)                                                 |
| ![이미지](https://shopping-phinf.pstatic.net/main_5476797/54767972210.20250523172527.jpg) 루보 이지스윙 안전문 S                | 29,900원 | 351  | ⭐ 1.3 | 404   | 네이버           | [바로가기](https://search.shopping.naver.com/catalog/54767972210)                                                  |
| ![이미지](https://shopping-phinf.pstatic.net/main_8789482/87894829055.jpg) 이지듀MD 크림 85g                                | 21,500원 | 809  | ⭐ 0.3 | 4,758 | CL-컴퍼니        | [바로가기](https://smartstore.naver.com/main/products/10350324582)                                                 |
| ![이미지](https://shopping-phinf.pstatic.net/main_5417769/54177690491.jpg) 이지드로잉 태블릿 1060 Plus                         | 51,900원 | 200  | ⭐ 1.5 | 1,301 | 옥션            | [바로가기](https://link.auction.co.kr/gate/pcs?item-no=B635360908&sub-id=1&service-code=10000003)                  |
| ![이미지](https://shopping-phinf.pstatic.net/main_8750160/87501606558.jpg) 이지듀 egf 재생 기미 크림                            | 19,900원 | 919  | ⭐ 0.8 | 192   | 효과 효능 후기 좋은 팜 | [바로가기](https://smartstore.naver.com/main/products/9957104285)                                                  |
| ![이미지](https://shopping-phinf.pstatic.net/main_5418359/54183598537.jpg) 이지드로잉 태블릿 1060 Plus 원격수업                    | 48,900원 | 321  | ⭐ 0.2 | 2,228 | 옥션            | [바로가기](https://link.auction.co.kr/gate/pcs?item-no=C268643712&sub-id=1&service-code=10000003)                  |
| ![이미지](https://shopping-phinf.pstatic.net/main_5416818/54168184922.jpg) 이지드로잉 태블릿 1060 Plus 원격수업                    | 48,900원 | 459  | ⭐ 0.2 | 1,330 | G마켓           | [바로가기](https://link.gmarket.co.kr/gate/pcs?item-no=1473925619&sub-id=1003&service-code=10000003&lcd=100000055) |


## 6️⃣ 개발 중인 기능 및 향후 계획
**(개발중)** Edge NGram 연관 검색어 기능 구현 중<br>
**(계획)** Terms Aggregation 기반 인기 검색어 기능 구현

<!-- ## 7️⃣ 기타 참고사항 (Additional Notes)
로그스태시를 활용한 데이터 적재 시간 단축 방법 간단 소개

형태소 분석기 세팅 팁-->
