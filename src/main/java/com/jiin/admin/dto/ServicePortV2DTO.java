package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

// Port 변경이 진행될 수 있어 아래와 같은 조치를 고려해야 함.
// 포트 별 버전 관리 (ServicePortVerN (N >= 2))
@Getter
@Setter
public class ServicePortV2DTO {
    private Long id;
    private Long svrId;
    private String sqlOSMPort = "5430";
    private String sqlBasicPort = "5432";
    private String watchdogPort = "9000";
    private String watchdogHbPort = "9694";
    private String pcpProcessPort = "9898";
    private String pgPool2Port = "9999";
    private String adminServerPort = "11110";
    private String mapProxyPort = "11120";
    private String mapServerPort = "11130";
    private String mapnikPort = "11140";
    private String terrainServerPort = "11150";
    private String jiinHeightPort = "11160";
    private String tegolaPort = "11170";
    private String syncthingTcpPort = "22000";
    private String syncthingUdpPort = "21027";
    private String rabbitMQPort1 = "5672";
    private String rabbitMQPort2 = "15672";

    public ServicePortV2DTO(){

    }

    public ServicePortV2DTO(Long id, Long svrId, String sqlOSMPort, String sqlBasicPort, String watchdogPort, String watchdogHbPort, String pcpProcessPort, String pgPool2Port, String adminServerPort, String mapProxyPort, String mapServerPort, String mapnikPort, String terrainServerPort, String jiinHeightPort, String tegolaPort, String syncthingTcpPort, String syncthingUdpPort, String rabbitMQPort1, String rabbitMQPort2) {
        this.id = id;
        this.svrId = svrId;
        this.sqlOSMPort = sqlOSMPort;
        this.sqlBasicPort = sqlBasicPort;
        this.watchdogPort = watchdogPort;
        this.watchdogHbPort = watchdogHbPort;
        this.pcpProcessPort = pcpProcessPort;
        this.pgPool2Port = pgPool2Port;
        this.adminServerPort = adminServerPort;
        this.mapProxyPort = mapProxyPort;
        this.mapServerPort = mapServerPort;
        this.mapnikPort = mapnikPort;
        this.terrainServerPort = terrainServerPort;
        this.jiinHeightPort = jiinHeightPort;
        this.tegolaPort = tegolaPort;
        this.syncthingTcpPort = syncthingTcpPort;
        this.syncthingUdpPort = syncthingUdpPort;
        this.rabbitMQPort1 = rabbitMQPort1;
        this.rabbitMQPort2 = rabbitMQPort2;
    }
}
