

function toggleView(view) {
    const wrapper = document.getElementById('productWrapper');
    const toGridBtn = document.getElementById('toGrid');
    const toListBtn = document.getElementById('toList');

    wrapper.classList.remove('list', 'grid');
    wrapper.classList.add(view);

    if (view === 'grid') {
        toGridBtn.style.display = 'none';
        toListBtn.style.display = 'inline-block';
    } else {
        toGridBtn.style.display = 'inline-block';
        toListBtn.style.display = 'none';
    }

    // 현재 URL에 'view' 파라미터 추가/수정
    const currentUrl = new URL(window.location.href);
    currentUrl.searchParams.set('view', view);
    history.replaceState(null, null, currentUrl); // 페이지 리로드 없이 URL 업데이트
}

// DOM 로드 후 실행
document.addEventListener("DOMContentLoaded", function () {
    const productSearchForm = document.getElementById('productSearchForm');

    if (productSearchForm) {
        productSearchForm.addEventListener('submit', function(event) {
            event.preventDefault();
            console.log("두 번째 폼(#productSearchForm) 제출됨");
            loadProducts({ pg: '1' });
        });
    } else {
        console.error("ID가 'productSearchForm'인 폼을 찾을 수 없습니다.");
    }

    // 상품 목록 로드
    loadProducts(`&sortType=salesCount&period=`);

    // 카테고리 아코디언 기능
    const parents = document.querySelectorAll(".category .parent");
    parents.forEach(parent => {
        parent.style.cursor = "pointer";
        parent.addEventListener("click", () => {
            const currentChildren = parent.nextElementSibling;
            document.querySelectorAll(".category .children").forEach(children => {
                if (children !== currentChildren) {
                    children.style.display = "none";
                }
            });
            if (currentChildren && currentChildren.classList.contains("children")) {
                const isVisible = currentChildren.style.display === "block";
                currentChildren.style.display = isVisible ? "none" : "block";
            }
        });
    });

    function attachProductClickHandler(containerSelector, cardSelector) {
        const container = document.querySelector(containerSelector);
        if (!container) {
            console.error('attachProductClickHandler: 컨테이너 엘리먼트를 찾을 수 없습니다.');
            return;
        }

        container.addEventListener('click', function (e) {
            e.preventDefault();
            let target = e.target.closest(cardSelector);
            if (!target) {
                console.error('attachProductClickHandler: 카드 엘리먼트를 찾을 수 없습니다.', e.target);
                return;
            }
            const prodNo = target.dataset.prodno;
            if (prodNo) {
                location.href = `/product/view?prodNo=${prodNo}`;
            } else {
                console.error('attachProductClickHandler: 상품 ID를 찾을 수 없습니다.', target);
            }
        });
    }

    attachProductClickHandler('.best-grid', '.best-card');
    attachProductClickHandler('#productWrapper', '.product-item');

    const salesPeriodSelect = document.getElementById('salesPeriodSelect');
    const reviewPeriodSelect = document.getElementById('reviewPeriodSelect');
    const sortLinks = document.querySelectorAll('.nav a[data-sort]');

    function getView() {
        const wrapper = document.getElementById('productWrapper');
        return wrapper.classList.contains('grid') ? 'grid' : 'list';
    }

    // 판매 기간 선택 이벤트
    salesPeriodSelect.addEventListener('change', function() {
        const period = this.value;
        const currentUrl = new URL(window.location.href);
        currentUrl.searchParams.set('sortType', 'mostSales');
        currentUrl.searchParams.set('period', period);
        currentUrl.searchParams.set('pg', '1');
        history.pushState(null, null, currentUrl);
        loadProducts('');
        resetOtherSortOptions('mostSales');
    });

    // 후기 기간 선택 이벤트
    reviewPeriodSelect.addEventListener('change', function() {
        const period = this.value;
        const currentUrl = new URL(window.location.href);
        currentUrl.searchParams.set('sortType', 'manyReviews');
        currentUrl.searchParams.set('period', period);
        currentUrl.searchParams.set('pg', '1');
        history.pushState(null, null, currentUrl);
        loadProducts('');
        resetOtherSortOptions('manyReviews');
    });

    // 정렬 링크 클릭 이벤트
    sortLinks.forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            const sortType = this.getAttribute('data-sort');
            let period = '';
            if (sortType === 'lowPrice' || sortType === 'highPrice') {
                period = salesPeriodSelect.value;
                // 후기 기간 셀렉트 박스 초기화
                reviewPeriodSelect.selectedIndex = 0;
            } else if (sortType === 'highRating') {
                period = reviewPeriodSelect.value;
                // 판매 기간 셀렉트 박스 초기화
                salesPeriodSelect.selectedIndex = 0;
            } else if (sortType === 'latest') {
                period = '';
                // 판매 기간, 후기 기간 셀렉트 박스 초기화
                salesPeriodSelect.selectedIndex = 0;
                reviewPeriodSelect.selectedIndex = 0;
            }

            // 현재 URL 가져오기
            const currentUrl = new URL(window.location.href);
            // sortType 파라미터 설정
            currentUrl.searchParams.set('sortType', sortType);
            // period 파라미터 설정
            currentUrl.searchParams.set('period', period);
            // 페이지 번호는 1로 초기화 (새로운 정렬이므로 첫 페이지부터 보여주는 것이 일반적)
            currentUrl.searchParams.set('pg', '1');

            // history.pushState를 사용하여 URL 업데이트 (페이지 리로드 없이)
            history.pushState(null, null, currentUrl);

            // 변경된 URL의 파라미터를 기반으로 상품 목록 로드
            loadProducts('');
        });
    });

    function resetOtherSortOptions(currentSortType) {
        if (currentSortType === 'mostSales') {
            salesPeriodSelect.selectedIndex = 0; // 판매 많은 순 초기화
            reviewPeriodSelect.selectedIndex = 0; // 후기 기간 초기화
        } else if (currentSortType === 'manyReviews') {
            salesPeriodSelect.selectedIndex = 0; // 판매 많은 순 초기화
            reviewPeriodSelect.selectedIndex = 0; // 후기 기간 초기화
        } else if (currentSortType === 'lowPrice' || currentSortType === 'highPrice') {
            salesPeriodSelect.selectedIndex = 0; // 판매 많은 순 초기화
            reviewPeriodSelect.selectedIndex = 0; // 후기 기간 초기화
        } else if (currentSortType === 'highRating') {
            salesPeriodSelect.selectedIndex = 0; // 판매 많은 순 초기화
            reviewPeriodSelect.selectedIndex = 0; // 후기 기간 초기화
        } else if (currentSortType === 'latest') {
            salesPeriodSelect.selectedIndex = 0; // 판매 많은 순 초기화
            reviewPeriodSelect.selectedIndex = 0; // 후기 기간 초기화
        }
    }

    function updatePagination(data) {
        const pageContainer = document.querySelector('.page');
        pageContainer.innerHTML = ''; // 기존 페이지네이션 초기화

        // 이전 페이지 링크
        if (data.prev) {
            const prevLink = document.createElement('a');
            prevLink.href = `#`;
            prevLink.classList.add('prev');
            prevLink.textContent = '<';
            prevLink.addEventListener('click', (e) => {
                e.preventDefault();
                loadProducts(`&pg=${data.start - 1}&sortType=${data.sortType}&period=${data.period}`);
            });
            pageContainer.appendChild(prevLink);
        }

        // 페이지 번호 링크
        for (let i = data.start; i <= data.end; i++) {
            const pageLink = document.createElement('a');
            pageLink.href = `#`;
            pageLink.textContent = i;
            if (i === data.pg) {
                pageLink.classList.add('current');
            } else {
                pageLink.classList.add('num');
                pageLink.addEventListener('click', (e) => {
                    e.preventDefault();
                    loadProducts(`&pg=${i}&sortType=${data.sortType}&period=${data.period}`);
                });
            }
            pageContainer.appendChild(pageLink);
        }

        // 다음 페이지 링크
        if (data.next) {
            const nextLink = document.createElement('a');
            nextLink.href = `#`;
            nextLink.classList.add('next');
            nextLink.textContent = '>';
            nextLink.addEventListener('click', (e) => {
                e.preventDefault();
                loadProducts(`&pg=${data.end + 1}&sortType=${data.sortType}&period=${data.period}`);
            });
            pageContainer.appendChild(nextLink);
        }
    }

    function formatNumber(number) {
        return number.toLocaleString();
    }

    function loadProducts(params) {
        const currentUrl = new URL(window.location.href);
        const sortTypeParam = currentUrl.searchParams.get('sortType');
        const periodParam = currentUrl.searchParams.get('period') || '';
        const keywordParam = currentUrl.searchParams.get('keyword') || '';
        const pgParam = new URLSearchParams(params).get('pg') || '1';
        const subKeywordInputElement = document.getElementById('subKeywordInput');
        const subKeywordParam = subKeywordInputElement ? subKeywordInputElement.value.trim() : '';

        if (!keywordParam.trim() && !subKeywordParam) {
            const productListContainer = document.querySelector('#productWrapper .product-list');
            const pageContainer        = document.querySelector('.page');
            productListContainer.innerHTML = `
            <div class="no-results-message">
              먼저 검색어를 입력해주세요.
            </div>`;
            if (pageContainer) pageContainer.innerHTML = '';
            return;
        }

        const selectedFilters = [];
        document.querySelectorAll('.search_filters input[name="filter"]:checked').forEach(checkbox => {
            selectedFilters.push(checkbox.value);
        });
        const searchTypeParam = selectedFilters.join(',');

        let minPriceParam = '';
        let maxPriceParam = '';
        const priceFilterCheckbox = document.getElementById('priceFilterCheckbox');

        if (priceFilterCheckbox && priceFilterCheckbox.checked) {
            const minPriceInput = document.getElementById('minPriceInput');
            const maxPriceInput = document.getElementById('maxPriceInput');
            if (minPriceInput && minPriceInput.value) {
                minPriceParam = minPriceInput.value;
            }
            if (maxPriceInput && maxPriceInput.value) {
                maxPriceParam = maxPriceInput.value;
            }
        }

        const queryParams = [
            `view=${encodeURIComponent(getView())}`,
            `pg=${encodeURIComponent(pgParam)}`,
            `sortType=${encodeURIComponent(sortTypeParam)}`,
            `period=${encodeURIComponent(periodParam)}`
        ];

        if (keywordParam) {
            console.log("keywordParam: "+keywordParam)
            queryParams.push(`keyword=${encodeURIComponent(keywordParam)}`);
        }

        if (searchTypeParam) {
            queryParams.push(`searchType=${encodeURIComponent(searchTypeParam)}`);
        }

        if (priceFilterCheckbox && priceFilterCheckbox.checked) {
            if (minPriceParam) {
                queryParams.push(`minPrice=${encodeURIComponent(minPriceParam)}`);
            }
            if (maxPriceParam) {
                queryParams.push(`maxPrice=${encodeURIComponent(maxPriceParam)}`);
            }
        }

        if (subKeywordParam) {
            queryParams.push(`subKeyword=${encodeURIComponent(subKeywordParam)}`);
        }

        const url = `/product/ajaxSearchList?${queryParams.join('&')}`;

        fetch(url)
            .then(response => response.json())
            .then(data => {
                const totalItems = data.total !== undefined ? data.total : (data.totalElements !== undefined ? data.totalElements : 0);

                const totalCountSpan = document.querySelector('span.total');
                if (totalCountSpan) {
                    totalCountSpan.textContent = `(총: ${totalItems.toLocaleString()}건)`;
                }

                const productListContainer = document.querySelector('#productWrapper .product-list');
                productListContainer.innerHTML = '';

                const pageContainer = document.querySelector('.page');

                if (totalItems === 0) {
                    const noResultsMessage = document.createElement('div');
                    noResultsMessage.classList.add('no-results-message');
                    noResultsMessage.textContent = '검색결과가 없습니다.';
                    productListContainer.appendChild(noResultsMessage);

                    if (pageContainer) {
                        pageContainer.innerHTML = '';
                    }
                } else {
                    data.dtoList.forEach(product => {
                        const productItem = document.createElement('div');
                        productItem.classList.add('product-item');
                        productItem.setAttribute('data-prodno', product.prodNo);
                        productItem.innerHTML = `
                    <img class="product-img" src="${product.snameList}" alt="${product.prodName}" />
                    <div class="product-info">
                        <span class="vendor-name">${product.company}</span><br>
                        <span class="pname">${product.prodName}</span>
                        <div class="meta">
                            <span>★ ${product.ratingAvg !== null ? product.ratingAvg.toFixed(1) : 0}</span>&nbsp;|&nbsp;리뷰 <span>${product.reviewCount}</span>
                        </div>
                    </div>
                    <div class="product-price">
                        <del>${formatNumber(product.prodPrice)}원</del><br>
                        <div class="icons">
                            <button><img src="/images/product/icon_favorite.png" alt="찜"></button>
                            <button><img src="/images/product/icon_cart.png" alt="장바구니"></button>
                        </div>
                    </div>
                `;
                        productListContainer.appendChild(productItem);
                    });

                    if (pageContainer) {
                        updatePagination(data);
                    }
                    attachProductClickHandler('#productWrapper', '.product-item');
                }
            })
            .catch(error => {
                console.error('Ajax 요청 실패:', error);
                // 사용자에게도 오류 메시지를 보여주는 것이 좋습니다.
                const productListContainer = document.querySelector('#productWrapper .product-list');
                if (productListContainer) {
                    productListContainer.innerHTML = '<div class="error-message">상품을 불러오는 데 실패했습니다. 다시 시도해주세요.</div>';
                }
                const pageContainer = document.querySelector('.page');
                if (pageContainer) {
                    pageContainer.innerHTML = ''; // 오류 시 페이지네이션도 비움
                }
            });
    }

});