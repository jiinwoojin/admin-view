package com.jiin.admin.enumeration;

// Map Server 관련 Enumeration (MAP, LAYER 관리)
public enum MapServerEnum {
    // MAP 파일 버전 관리
    MAP_VERSION_DATA_PATH("/mapsets"),


    // LAYER 파일 버전 관리
    LAYER_DEFAULT_VERSION("1.0"),
    LAYER_ASCEND_VERSION("0.1"),


    // MAP 파일 관리
    MAP_SUFFIX(".map"),
    MAP_FILE_PATH("/mapserver"),
    MAP_DEFAULT_NAME("world"),

    MAP_IMAGE_PNG("png"),
    MAP_IMAGE_JPEG("jpeg"),

    MAP_EPSG_4326_MIN_X("-180"),
    MAP_EPSG_4326_MIN_Y("-90"),
    MAP_EPSG_4326_MAX_X("180"),
    MAP_EPSG_4326_MAX_Y("90"),

    MAP_EPSG_3857_MIN_X("-20026376.39"),
    MAP_EPSG_3857_MIN_Y("-20048966.10"),
    MAP_EPSG_3857_MAX_X("20026376.39"),
    MAP_EPSG_3857_MAX_Y("20048966.10"),


    // LAYER 파일 관리
    LAYER_TYPE_RASTER("RASTER"),
    LAYER_TYPE_VECTOR("VECTOR"),
    LAYER_TYPE_CADRG("CADRG"),

    LAYER_FILE_PATH("/mapserver/layer"),
    LAYER_DATA_PATH("/data"),

    LAYER_SUFFIX(".lay"),
    LAYER_DEFAULT_NAME("world"),
    LAYER_DEFAULT_MIDDLE_PATH("NE2"),
    LAYER_DEFAULT_FILENAME("NE2_HR_LC_SR_W_DR.tif"),

    // CADRG 파일 관련
    CADRG_DEFAULT_EXECUTE_DIRECTORY("/RPF"),
    CADRG_DEFAULT_EXECUTE_FILE("A.TOC"),


    // VRT 파일 관리 (CADRG 관련)
    VRT_FILE_PATH("/data/vrt"),
    VRT_SUFFIX(".vrt"),
    TEMP_DIR_PATH("/tmp"),


    // 공통 사용 상수
    SRS_EPSG_4326("epsg:4326"),
    SRS_EPSG_3857("epsg:3857");


    private final String value;

    public String getValue() {
        return value;
    }

    MapServerEnum (String value) {
        this.value = value;
    }
}
