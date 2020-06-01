package com.jiin.admin.vo;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ServerCenterInfo {
    private String name;
    private String ip;
    private String zone;
    private String kind;
    private String description;

    public ServerCenterInfo(){

    }

    public ServerCenterInfo(String name, String ip, String zone, String kind, String description){
        this.name = name;
        this.ip = ip;
        this.zone = zone;
        this.kind = kind;
        this.description = description;
    }

    public static ServerCenterInfo convertDTO(Map<String, Object> map){
        if(map == null) return null;
        return new ServerCenterInfo(
            (String) map.get("name"), (String) map.get("ip"), (String) map.get("zone"), (String) map.get("kind"), (String) map.get("description")
        );
    }

    public static Map<String, Object> convertMap(ServerCenterInfo serverCenterInfo) {
        if(serverCenterInfo == null) return null;
        return new HashMap<String, Object>() {{
            put("name", serverCenterInfo.getName());
            put("ip", serverCenterInfo.getIp());
            put("zone", serverCenterInfo.getZone());
            put("kind", serverCenterInfo.getKind());
            put("description", serverCenterInfo.getDescription());
        }};
    }
}
