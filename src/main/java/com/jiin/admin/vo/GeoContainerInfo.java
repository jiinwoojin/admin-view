package com.jiin.admin.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeoContainerInfo {
    private String name; // Container 이름
    private String status; // Container 상태
    private int port; // PORT 가 0 인 경우는 Docker.

    public GeoContainerInfo() {
        this.name = "UNKNOWN";
        this.status = "UNKNOWN";
        this.port = -1;
    }

    public GeoContainerInfo(String name, String status, int port) {
        this.name = name;
        this.status = status;
        this.port = port;
    }
}
