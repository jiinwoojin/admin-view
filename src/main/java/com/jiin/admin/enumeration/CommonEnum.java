package com.jiin.admin.enumeration;

public enum CommonEnum {
    // 공통 사용 상수 (계속 작업 중)
    SRS_EPSG_4326("EPSG:4326"),
    SRS_EPSG_3857("EPSG:3857"),
    SRS_EPSG_900913("EPSG:900913");

    private final String value;

    public String getValue() {
        return value;
    }

    CommonEnum (String value) {
        this.value = value;
    }
}
