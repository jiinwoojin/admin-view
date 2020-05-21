package com.jiin.admin.website.server.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeoManagerProcessInfo {
    private String status; // 프로세스 상태
    private String sTime; // 실행 시작 시간
    private String exeTime; // 총 실행 시각 (M 시간 N 분 단위)
    private String cmd; // 실행 명령어

    public GeoManagerProcessInfo(){

    }

    public GeoManagerProcessInfo(String status, String sTime, String exeTime, String cmd){
        this.status = status;
        this.sTime = sTime;
        this.exeTime = exeTime;
        this.cmd = cmd;
    }
}
