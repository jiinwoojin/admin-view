package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

// Port 변경이 진행될 수 있어 아래와 같은 조치를 고려해야 함.
// 포트 별 버전 관리 (ServicePortVerN (N >= 2))
@Getter
@Setter
public class ServicePortDTO {
    private Long id;
    private Long svrId;
    private String postgreSQLPort;
    private String watchdogPort;
    private String watchdogHbPort;
    private String pcpProcessPort;
    private String pgPool2Port;
    private String adminServerPort;
    private String mapProxyPort;
    private String mapServerPort;
    private String vectorTilePort;
    private String jiinHeightPort;
    private String losPort;
    private String minioPort;
    private String mapnikPort;
    private String syncthingTcpPort;
    private String syncthingUdpPort;

    public ServicePortDTO(){

    }

    public ServicePortDTO(Long id, Long svrId, String sqlOSMPort, String sqlBasicPort, String watchdogPort, String watchdogHbPort, String pcpProcessPort, String pgPool2Port, String adminServerPort, String mapProxyPort, String mapServerPort, String mapnikPort, String terrainServerPort, String jiinHeightPort, String tegolaPort, String syncthingTcpPort, String syncthingUdpPort, String rabbitMQPort1, String rabbitMQPort2) {
        this.id = id;
        this.svrId = svrId;
        this.watchdogPort = watchdogPort;
        this.watchdogHbPort = watchdogHbPort;
        this.pcpProcessPort = pcpProcessPort;
        this.pgPool2Port = pgPool2Port;
        this.adminServerPort = adminServerPort;
        this.mapProxyPort = mapProxyPort;
        this.mapServerPort = mapServerPort;
        this.mapnikPort = mapnikPort;
        this.jiinHeightPort = jiinHeightPort;
        this.syncthingTcpPort = syncthingTcpPort;
        this.syncthingUdpPort = syncthingUdpPort;
    }
}
