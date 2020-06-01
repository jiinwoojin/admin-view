package com.jiin.admin.website.model;

import com.jiin.admin.vo.ServerCenterInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerCenterInfoModel {
    private String method;
    private String name;
    private String ip;
    private String zone;
    private String kind;
    private String description;

    public ServerCenterInfoModel(){

    }

    public ServerCenterInfoModel(String method, String name, String ip, String zone, String kind, String description){
        this.method = method;
        this.name = name;
        this.ip = ip;
        this.zone = zone;
        this.kind = kind;
        this.description = description;
    }

    public static ServerCenterInfo convertDTO(ServerCenterInfoModel model){
        if(model == null) return null;
        else {
            return new ServerCenterInfo(model.getName(), model.getIp(), model.getZone(), model.getKind(), model.getDescription());
        }
    }
}
