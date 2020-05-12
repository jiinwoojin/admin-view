package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerConnectionModel {
    private Long id;
    private String key; // find by key
    private String title; // Server 이름
    private String type; // SI vs N-SI
    private String ipAddress; // 접속 IP 주소
    private String username; // 사용자 이름
    private String password; // 비밀번호

    public ServerConnectionModel(){

    }

    public ServerConnectionModel(Long id, String key, String title, String type, String ipAddress, String username, String password) {
        this.id = id;
        this.key = key;
        this.title = title;
        this.type = type;
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
    }
}
