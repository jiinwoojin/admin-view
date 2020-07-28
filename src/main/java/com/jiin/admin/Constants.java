package com.jiin.admin;

public class Constants {
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
}
