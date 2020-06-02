package com.jiin.admin.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GeoDockerContainerInfo {
    private String name; // Container 이름
    private String status; // 프로세스 상태
    private boolean run;
    private boolean restart;
    private boolean dead;
    private Date startTime;
    private Date finishTime;

    public GeoDockerContainerInfo(){
        this.name = "UNKNOWN";
        this.status = "UNKNOWN";
        this.run = false;
        this.restart = false;
        this.dead = true;
        this.startTime = null;
        this.finishTime = null;
    }

    public GeoDockerContainerInfo(String name, String status, boolean run, boolean restart, boolean dead, Date startTime, Date finishTime) {
        this.name = name;
        this.status = status;
        this.run = run;
        this.restart = restart;
        this.dead = dead;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}
