package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerFormModel {
    private Long id;
    private String saveType;
    private String key; // find by key
    private String title; // Server 이름
    private String serverType; // SI vs N-SI
    private String ipAddress; // 접속 IP 주소
    private String username; // 사용자 이름
    private String password; // 비밀번호
    private String postgreSQLPort = "5432";
    private String watchdogPort = "9000";
    private String watchdogHbPort = "9694";
    private String pcpProcessPort = "9898";
    private String pgPool2Port = "9999";
    private String adminServerPort = "11110";
    private String mapProxyPort = "11120";
    private String mapServerPort = "11130";
    private String vectorTilePort = "11140";
    private String jiinHeightPort = "11150";
    private String losPort = "11160";
    private String minioPort = "11170";
    private String mapnikPort = "11190";
    private String syncthingTcpPort = "22000";
    private String syncthingUdpPort = "21027";
}
