# jiinwoojin | admin-view

    [classpath:data/default-map-proxy.yaml]
    파일파싱하여
    [/data/jiapp/data_dir/proxy/mapproxy.yaml]
    덮어씁니다.
    [default-map-proxy.yaml] 에 존재하는 레이어 및 소스는 편집이 불가합니다.

## 적용라이브러리

	1. Spring Boot
	2. Gradle
 	3. BootStrap free template - https://startbootstrap.com/themes/sb-admin-2/
	4. thymeleaf & thymeleaf-layout-dialect
 	5. Openlayers
 	6. jQuery
 	7. DataTables
 	8. Chart.js
 	
## 기본세팅

	1. Main Class : com.jiin.admin.starter.AdminWebApplication
	2. 환경설정 : resources/application.yml
	3. 포트 : 8077

## 기본실행

	Gradle > Tasks > application > bootRun
	
## 기본주소

	http://localhost:8077/view/home/user
	http://localhost:8077/server/server-state

## JPA 활성
	
	활성 데이터베이스 > com.jiin.admin.config.db.BaseDatabase
	엔티티패키지 > com.jiin.admin.entity

## 공통함수

    jiCommon.reloadDocker() - restart Docker

## 심볼관리
    
    http://localhost:8077/view/symbol/list
    엔티티 > com.jiin.admin.entity.MapSymbol(TableName = "MAP_SYMBOL")
    JPA 활용 구동시 초기 데이터 입력 - com.jiin.admin.config.BootingService

## UI 작업물 주소

    http://127.0.0.1:8077/view/s00-dash-board
        - Home Page (Dashboard)
    
    http://127.0.0.1:8077/view/s01-data-list
        - 지도 데이터 목록 페이지
        
    http://127.0.0.1:8077/view/s01-data-detail
        - 지도 데이터 정보 조회 페이지
    
    http://127.0.0.1:8077/view/s01-data-form
        - 지도 데이터 추가 / 편집 페이지
        
    http://127.0.0.1:8077/view/s02-file-converter
        - 스타일 파일 컨버터 페이지
        
    http://127.0.0.1:8077/view/s02-file-generator
        - Mapfile 생성 페이지
        
    http://127.0.0.1:8077/view/s02-file-list
        - Mapfile 연동 레스터, 벡터 데이터 조회 페이지
        
    http://127.0.0.1:8077/view/s02-file-upload
        - Mapfile 연동 레스터, 벡터 데이터 업로드 페이지
        
    http://127.0.0.1:8077/view/s03-map-cache-seed
        - Map Proxy 기반 Cache Seed 관리 페이지
        
    http://127.0.0.1:8077/view/s03-map-cesium-js
        - 3차원 지도 도시 페이지 (2 차원 지도 도시와 연동 계획)
    	
    http://127.0.0.1:8077/view/s03-map-config
        - Map Proxy 기반 지도 서비스 설정 페이지 
        
    http://127.0.0.1:8077/view/s03-map-mapbox-gl
        - Mapbox GL 엔진 렌더링 페이지
        
    http://127.0.0.1:8077/view/s03-map-openlayers
        - Openlayers 엔진 렌더링 페이지
        
    http://127.0.0.1:8077/view/s03-map-overlay
        - 2 차원 지도 도시 페이지 (중첩 도시, 군대부호, 투명도)
        
    http://127.0.0.1:8077/view/s04-system-history-config
        - Map Server, Map Proxy, Tegola 등 LOG 디렉토리 관리 페이지
        
    http://127.0.0.1:8077/view/s04-system-history-list
        - 파일 동기화 및 업로딩 이력 페이지
    
    http://127.0.0.1:8077/view/s04-system-status
        - Map Server, Map Proxy, Tegola 등 서버 작동 확인 페이지
        
    http://127.0.0.1:8077/view/s04-system-version
        - 시스템 버전 정보 조회 페이지
        
    http://127.0.0.1:8077/view/s05-sync-database
        - PGPool2 기반 DB 동기화 페이지
        
    http://127.0.0.1:8077/view/s05-sync-file
        - Syncthing 기반 파일 동기화 페이지
        
    http://127.0.0.1:8077/view/s06-auth-list
        - 회원 목록 조회 페이지
        
    http://127.0.0.1:8077/view/s06-auth-login
        - 회원 로그인 페이지
        
    http://127.0.0.1:8077/view/s06-auth-sign-up
        - 회원 가입 페이지
        
    http://127.0.0.1:8077/view/s07-virtualize-mapbox-ext
        - Mapbox 기반 시각화 구현 페이지
        
    http://127.0.0.1:8077/view/s07-virtualize-ol-ext
        - Openlayers 기반 시각화 구현 페이지

## 시연 페이지 주소
    
    Spring Secutiry 적용
        ID : admin / PW : jiin0701
        ID : user / PW : jiin0701
    
    http://127.0.0.1:8077/view/home/guest
    http://127.0.0.1:8077/view/home/user
        - 대시보드 페이지
        
    http://127.0.0.1:8077/view/display/2d-map
        - 상황도 도시 페이지
        
    http://127.0.0.1:8077/view/map/list
        - 지도 데이터 > 지도 목록
    
    http://127.0.0.1:8077/view/map/create
        - 지도 데이터 > 지도 생성
              
    http://127.0.0.1:8077/view/map/layers
        - 지도 데이터 > 레이어 관리
        
    http://127.0.0.1:8077/view/cache/layers
        - 지도 데이터 > 캐시 레이어 관리
        
    http://127.0.0.1:8077/view/cache/grids
        - 지도 데이터 > 캐시 GRIDS 관리
        
    http://127.0.0.1:8077/view/cache/seeds
        - 지도 데이터 > 캐시 SEEDS 관리