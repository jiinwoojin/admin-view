package com.jiin.admin.enumeration;

// Mapnik 관련 Enumeration (SYMBOL 관리)
public enum MapboxEnum {
    // SYMBOL 데이터 관리
    SYMBOL_FILE_PATH("/html"),

    SYMBOL_DEFAULT_PIXEL_RATIO("1"),

    PNG_SUFFIX(".png"),
    PNG_2X_SUFFIX("@2x.png"),
    JSON_SUFFIX(".json"),
    JSON_2X_SUFFIX("@2x.json");


    private final String value;

    public String getValue() {
        return value;
    }

    MapboxEnum(String value) {
        this.value = value;
    }
}
