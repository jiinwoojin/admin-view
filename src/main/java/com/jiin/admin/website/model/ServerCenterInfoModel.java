package com.jiin.admin.website.model;

import com.jiin.admin.vo.ServerCenterInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ServerCenterInfoModel {
    private String method;
    private String key;
    private String name;
    private String ip;
    private String zone;
    private String kind;
    private String description;

    public ServerCenterInfoModel() {

    }

    public ServerCenterInfoModel(String method, String key, String name, String ip, String zone, String kind, String description) {
        this.method = method;
        this.key = key;
        this.name = name;
        this.ip = ip;
        this.zone = zone;
        this.kind = kind;
        this.description = description;
    }

    public static ServerCenterInfo convertDTO(ServerCenterInfoModel model) {
        if (model == null) return null;
        else {
            return new ServerCenterInfo(model.getKey(), model.getName(), model.getIp(), model.getZone(), model.getKind(), model.getDescription());
        }
    }

    public static ServerCenterInfoModel convertModel(String method, ServerCenterInfo dto) {
        if (dto == null) return null;
        else {
            return new ServerCenterInfoModel(method, dto.getKey(), dto.getName(), dto.getIp(), dto.getZone(), dto.getKind(), dto.getDescription());
        }
    }

    public static Map<String, String> convertMap(ServerCenterInfoModel model) {
        return new HashMap<String, String>() {
            {
                put("method", model.getMethod());
                put("key", model.getKey());
                put("name", model.getName());
                put("ip", model.getIp());
                put("zone", model.getZone());
                put("kind", model.getKind());
                put("description", model.getDescription());
            }
        };
    }
}
