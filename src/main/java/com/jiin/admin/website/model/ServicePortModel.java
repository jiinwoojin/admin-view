package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServicePortModel {
    private Long id;
    private Long svrId;
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

    public ServicePortModel(){

    }

    public ServicePortModel(Long id, Long svrId, String postgreSQLPort, String watchdogPort, String watchdogHbPort, String pcpProcessPort, String pgPool2Port, String adminServerPort, String mapProxyPort, String mapServerPort, String vectorTilePort, String jiinHeightPort, String losPort, String minioPort, String mapnikPort, String syncthingTcpPort, String syncthingUdpPort) {
        this.id = id;
        this.svrId = svrId;
        this.postgreSQLPort = postgreSQLPort;
        this.watchdogPort = watchdogPort;
        this.watchdogHbPort = watchdogHbPort;
        this.pcpProcessPort = pcpProcessPort;
        this.pgPool2Port = pgPool2Port;
        this.adminServerPort = adminServerPort;
        this.mapProxyPort = mapProxyPort;
        this.mapServerPort = mapServerPort;
        this.vectorTilePort = vectorTilePort;
        this.jiinHeightPort = jiinHeightPort;
        this.losPort = losPort;
        this.minioPort = minioPort;
        this.mapnikPort = mapnikPort;
        this.syncthingTcpPort = syncthingTcpPort;
        this.syncthingUdpPort = syncthingUdpPort;
    }
}
