package com.jiin.admin.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GeoDockerContainerInfo extends GeoContainerInfo {
    private boolean run;
    private boolean restart;
    private boolean dead;
    private Date startTime;
    private Date finishTime;

    public GeoDockerContainerInfo() {
        super("UNKNOWN", "UNKNOWN", 0);
        this.run = false;
        this.restart = false;
        this.dead = true;
        this.startTime = null;
        this.finishTime = null;
    }

    public GeoDockerContainerInfo(String name, String status, boolean run, boolean restart, boolean dead, Date startTime, Date finishTime) {
        super(name, status, 0);
        this.run = run;
        this.restart = restart;
        this.dead = dead;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}
