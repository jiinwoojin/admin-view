package com.jiin.admin.enumeration;

// Admin Server 관련 Enumeration
public enum AdminServerEnum {
    // 사용자 SESSION MESSAGE
    SESSION_MESSAGE("___SESSION_MESSAGE"),


    // 사용자 및 관리자 관련 기능
    ACCOUNT_ADMIN_DEFAULT_USERNAME("admin"),
    ACCOUNT_ADMIN_DEFAULT_PASSWORD("jiin0701"),
    ACCOUNT_ADMIN_DEFAULT_NAME("관리자"),

    ACCOUNT_USER_DEFAULT_USERNAME("user"),
    ACCOUNT_USER_DEFAULT_PASSWORD("jiin0701"),
    ACCOUNT_USER_DEFAULT_NAME("사용자"),

    ROLE_DEFAULT_ADMIN_TYPE("ADMIN"),
    ROLE_DEFAULT_ADMIN_TYPE_NAME("관리자"),

    ROLE_DEFAULT_USER_TYPE("USER"),
    ROLE_DEFAULT_USER_TYPE_NAME("사용자"),


    // 서비스 관리 기능
    SERVICE_NAME_MAP_SERVER("MapServer"),
    SERVICE_NAME_MAP_PROXY("MapProxy"),
    SERVICE_NAME_JIIN_HEIGHT("Ji-in Height"),
    SERVICE_NAME_RABBIT_MQ("RabbitMQ"),
    SERVICE_NAME_NGINX("NGINX"),

    SERVICE_NAME_POSTGRE_SQL_OSM("PostgreSQL OSM"),
    SERVICE_NAME_POSTGRE_SQL_BASIC("PostgreSQL Basic"),
    SERVICE_NAME_PG_POOL("PGPool"),
    SERVICE_NAME_TEGOLA("Tegola"),
    SERVICE_NAME_TERRAIN_SERVER("Terrain Server"),
    SERVICE_NAME_SYNCTHING("Syncthing"),

    SERVICE_DOCKER_STATUS_START("START"),
    SERVICE_DOCKER_STATUS_STOP("STOP"),
    SERVICE_DOCKER_STATUS_RUNNING("RUNNING"),
    SERVICE_DOCKER_STATUS_EXITED("EXITED"),

    SERVICE_ANYTHING_UNKNOWN("UNKNOWN"),


    // 서버 주소 관리 기능
    SERVER_SETTING_FILE_PATH("/conf"),
    SERVER_SETTING_FILE_NAME("server_info.yaml"),
    SERVER_TYPE_REMOTE("remote"),
    SERVER_TYPE_LOCAL("local");


    private final String value;

    public String getValue() {
        return value;
    }

    AdminServerEnum (String value) {
        this.value = value;
    }
}
