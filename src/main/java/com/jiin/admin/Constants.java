package com.jiin.admin;

import com.jiin.admin.website.model.OptionModel;

import java.util.Arrays;
import java.util.List;

public class Constants {
    // MapServer (MAP, LAYER) 관련 상수 값
    public interface MAPSERVER {
        interface MAP_VERSION {
            // MAP VERSION 관리 디렉토리
            String DATA_PATH = "/mapsets";
        }

        interface LAYER_VERSION {
            // LAYER VERSION 기본 수치
            Double DEFAULT_VERSION = 1.0;

            // LAYER VERSION 증가 수치
            Double ASCEND_VERSION = 0.1;
        }

        interface MAP {
            // MAP *.map 파일 확장자
            String MAP_SUFFIX = ".map";

            // MAP *.map 파일 관리 디렉토리
            String MAP_DATA_PATH = "/mapserver";

            // MAP 기본 파일 (WORLD 데이터)
            String DEFAULT_DATA_NAME = "world";

            // MAP 검색 - 키워드 옵션
            List<OptionModel> SB_OPTIONS = Arrays.asList(
                new OptionModel("-- 검색 키워드 선택 --", 0),
                new OptionModel("MAP 이름", 1),
                new OptionModel("MAP 등록자", 2),
                new OptionModel("MAP 좌표 체계", 3)
            );

            // MAP 검색 - 정렬 옵션
            List<OptionModel> OB_OPTIONS = Arrays.asList(
                new OptionModel("-- 정렬 방식 선택 --", 0),
                new OptionModel("ID 순서 정렬", 1),
                new OptionModel("이름 순서 정렬", 2),
                new OptionModel("등록 기간 역순 정렬", 3)
            );

            // MAP 이미지 포맷
            String IMAGE_PNG = "png";
            String IMAGE_JPEG = "jpeg";

            // EPSG:4326 MIN X ~ MAX Y
            String EPSG_4326_MIN_X = "-180";
            String EPSG_4326_MIN_Y = "-90";
            String EPSG_4326_MAX_X = "180";
            String EPSG_4326_MAX_Y = "90";

            // EPSG:3857 MIN X ~ MAX Y
            String EPSG_3857_MIN_X = "-20026376.39";
            String EPSG_3857_MIN_Y = "-20048966.10";
            String EPSG_3857_MAX_X = "20026376.39";
            String EPSG_3857_MAX_Y = "20048966.10";

            // LAYER 검색 - 정렬 옵션
            List<String> UNITS = Arrays.asList("DD", "FEET", "INCHES", "KILOMETERS", "METERS", "MILES", "NAUTICALMILES");
        }

        interface VRT {
            String VRT_FILE_PATH = String.format("%s/vrt", LAYER.ORIGIN_DATA_PATH);
            String VRT_SUFFIX = ".vrt";
            String TMP_DIR_PATH = "/tmp";
        }

        interface LAYER {
            // LAYER 종류
            String RASTER = "RASTER";
            String VECTOR = "VECTOR";
            String CADRG = "CADRG";

            // LAYER 원본 파일 관리 디렉토리
            String ORIGIN_DATA_PATH = "/data";

            // LAYER *.lay 파일 관리 디렉토리
            String LAY_DATA_PATH = "/mapserver/layer";

            // LAYER *.lay 파일 확장자
            String LAY_SUFFIX = ".lay";

            // LAYER 기본 파일 (WORLD 데이터)
            String DEFAULT_DATA_NAME = "world";
            String DEFAULT_MIDDLE_PATH = "NE2";
            String DEFAULT_FILE_NAME = "NE2_HR_LC_SR_W_DR.tif";

            // LAYER 검색 - 키워드 옵션
            List<OptionModel> SB_OPTIONS = Arrays.asList(
                new OptionModel("-- 검색 키워드 선택 --", 0),
                new OptionModel("LAYER 이름", 1),
                new OptionModel("LAYER 등록자", 2),
                new OptionModel("LAYER 좌표 체계", 3)
            );

            // LAYER 검색 - 정렬 옵션
            List<OptionModel> OB_OPTIONS = Arrays.asList(
                new OptionModel("-- 정렬 방식 선택 --", 0),
                new OptionModel("ID 순서 정렬", 1),
                new OptionModel("이름 순서 정렬", 2),
                new OptionModel("등록 기간 역순 정렬", 3)
            );
        }

        interface COMMON {
            String EPSG_4326 = "epsg:4326";
            String EPSG_3857 = "epsg:3857";
        }
    }

    // MapProxy (CACHE) 관련 상수 값
    public interface MAPPROXY {
        interface PROXY_LAYER {
            // 추출 중...
        }

        interface PROXY_SOURCE {
            // PROXY SOURCE 종류
            String WMS = "wms";
            String MAPSERVER = "mapserver";

            // PROXY SOURCE WMS 버전
            String WMS_VER_1_3_0 = "1.3.0";
            String WMS_VER_1_1_1 = "1.1.1";

            // PROXY SOURCE WMS REQUEST URL
            String WMS_REQUEST_URL = "mapserver/cgi-bin/mapserv?";

            // PROXY SOURCE WMS CONCURRENT REQUESTS
            Integer WMS_CONCURRENT_REQUESTS = 4;

            // PROXY SOURCE WMS HTTP CLIENT TIMEOUT
            Integer WMS_HTTP_CLIENT_TIMEOUT = 600;

            // 추출 중...
        }

        interface PROXY_CACHE {
            // 추출 중...
        }

        interface COMMON {
            // SRS TYPE
            String EPSG_4326 = "EPSG:4326";
            String EPSG_3857 = "EPSG:3857";
            String EPSG_900913 = "EPSG:900913";

            // 추출 중...
        }
    }

    // Mapnik (SYMBOL) 관련 상수 값
    public interface MAPNIK {
        // 추출 중...
    }

    // Admin Server 관련 상수 값
    public interface ADMIN_SERVER {
        // 추출 중...
    }

    // 아래에 있는 상수 데이터는 enumeration 패키지 안에 '곧' 정리 될 예정입니다.

    public static final String SESSION_MESSAGE = "___SESSION_MESSAGE";

    /**
     * DATA 파일 경로
     */
    public static final String DATA_PATH = "/data";

    /**
     * CADRG 타입 데이터 경로 관련
     */
    public static final String CADRG_DEFAULT_EXECUTE_DIRECTORY = "/RPF";

    public static final String CADRG_DEFAULT_EXECUTE_FILE = "/A.TOC";

    /**
     * LAYER 파일 경로
     */
    public static final String LAY_FILE_PATH = "/mapserver/layer";

    public static final String LAY_SUFFIX = ".lay";

    /**
     * MAP 파일 경로
     */
    public static final String MAP_FILE_PATH = "/mapserver";

    public static final String MAP_SUFFIX = ".map";

    public static final String MAP_SERVER_WMS_URL = "mapserver/cgi-bin/mapserv?";

    /**
     * PROXY YAML 파일 경로
     */
    public static final String PROXY_SETTING_FILE_PATH = "/proxy";

    public static final String PROXY_SETTING_FILE_NAME = "mapproxy.yaml";

    public static final String PROXY_CACHE_DIRECTORY = "/cache";

    /**
     * PROXY SEED YAML 정보
     */
    public static final String SEED_SETTING_FILE_NAME = "seed.yaml";

    /**
     * SERVER INFO 정보
     */
    public static final String SERVER_INFO_FILE_PATH = "/conf";

    public static final String SERVER_INFO_FILE_NAME = "server_info.yaml";

    /**
     * MAP VERSION 관리 정보
     */
    public static final String MAP_VERSION_FILE_PATH = "/mapsets";

    public static final Double DEFAULT_LAYER_VERSION = 1.0;

    public static final Double ASCEND_LAYER_VERSION = 0.1;

    /**
     * VRT 정보
     */
    public static final String VRT_FILE_PATH = String.format("%s/%s", DATA_PATH, "vrt");

    public static final String VRT_SUFFIX = ".vrt";

    public static final String TMP_DIR_PATH = "/tmp";

    /**
     * MAPNIK SYMBOL 데이터 정보
     */
    public static final String SYMBOL_FILE_PATH = "/html";

    public static final String PNG_SUFFIX = ".png";

    public static final String PNG_2X_SUFFIX = "@2x.png";

    public static final String JSON_SUFFIX = ".json";

    public static final String JSON_2X_SUFFIX = "@2x.json";

    public static final Integer DEFAULT_SYMBOL_PIXEL_RATIO = 1;

    /**
     * 좌표계 설정
     */
    public static final String EPSG_4326 = "EPSG:4326";

    public static final String EPSG_3857 = "EPSG:3857";

    public static final String EPSG_900913 = "EPSG:900913";
}
