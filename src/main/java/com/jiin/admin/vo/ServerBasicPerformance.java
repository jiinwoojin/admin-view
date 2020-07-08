package com.jiin.admin.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerBasicPerformance {
    private String serverName;
    private String status;

    // RAM 정보
    private long usedMemory;
    private long totalMemory;
    private long availableMemory;

    // Disk 정보
    private double usedCapacity;
    private double totalCapacity;

    // CPU 정보
    private double cpuUsage;

    // 접속자 수
    private long connections;

    public ServerBasicPerformance() {
        this.serverName = "UNKNOWN";
        this.usedMemory = -1L;
        this.totalMemory = -1L;
        this.availableMemory = -1L;
        this.totalCapacity = -1L;
        this.usedCapacity = -1L;
        this.cpuUsage = -1.0f;
        this.connections = -1L;
    }
}
