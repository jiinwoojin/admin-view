package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxySourceModelV2 {
    private long id;
    private String method;
    private String name;
    private String type;
    private String requestMap;
    private String requestLayers;
    private String mapServerBinary;
    private String mapServerWorkDir;

    public ProxySourceModelV2(){
        this.id = 0L;
        this.method = method;
    }

    public ProxySourceModelV2(long id, String method, String name, String type, String requestMap, String requestLayers, String mapServerBinary, String mapServerWorkDir) {
        this.id = id;
        this.method = method;
        this.name = name;
        this.type = type;
        this.requestMap = requestMap;
        this.requestLayers = requestLayers;
        this.mapServerBinary = mapServerBinary;
        this.mapServerWorkDir = mapServerWorkDir;
    }
}
