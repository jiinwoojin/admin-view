package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerConnectionDTO {
    private Long id;
    private String key;
    private String title;
    private String type;
    private String ipAddress;
    private String username;
    private String password;
    private String port;

    public ServerConnectionDTO(){

    }

    public ServerConnectionDTO(Long id, String key, String title, String type, String ipAddress, String username, String password, String port) {
        this.id = id;
        this.key = key;
        this.title = title;
        this.type = type;
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        this.port = port;
    }
}
