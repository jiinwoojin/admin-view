package com.jiin.admin.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ServerCenterInfo {
    private String key;
    private String name;
    private String ip;
    private String zone;
    private String kind;
    private String description;

    public ServerCenterInfo() {

    }

    public ServerCenterInfo(String key, String name, String ip, String zone, String kind, String description) {
        this.key = key;
        this.name = name;
        this.ip = ip;
        this.zone = zone;
        this.kind = kind;
        this.description = description;
    }

    public static ServerCenterInfo convertDTO(String key, Map<String, Object> map) {
        if (map == null) return null;
        return new ServerCenterInfo(
            key, (String) map.get("name"), (String) map.get("ip"), (String) map.get("zone"), (String) map.get("kind"), (String) map.get("description")
        );
    }

    public static Map<String, Object> convertMap(ServerCenterInfo serverCenterInfo) {
        if (serverCenterInfo == null) return null;
        return new HashMap<String, Object>() {{
            put("name", serverCenterInfo.getName());
            put("ip", serverCenterInfo.getIp());
            put("zone", serverCenterInfo.getZone());
            put("kind", serverCenterInfo.getKind());
            put("description", serverCenterInfo.getDescription());
        }};
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServerCenterInfo)) return false;
        ServerCenterInfo svr = (ServerCenterInfo) o;
        return svr.getKey().equals(this.getKey()) || (svr.getName().equals(this.getName()) && svr.getIp().equals(this.getIp()) && svr.getZone().equals(this.getZone()) && svr.getKind().equals(this.getKind()));
    }
}
