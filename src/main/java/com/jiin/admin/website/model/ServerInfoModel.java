package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerInfoModel {
    private String name; // Server 이름
    private String type; // SI vs N-SI
    private String ipAddress; // 접속 IP 주소
    private String username; // 사용자 이름
    private String password; // 비밀번호

    public ServerInfoModel(){

    }

    public ServerInfoModel(String name, String type, String ipAddress, String username, String password) {
        this.name = name;
        this.type = type;
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
    }
}
