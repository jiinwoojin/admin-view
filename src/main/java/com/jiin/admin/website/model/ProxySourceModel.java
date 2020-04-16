package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxySourceModel {
    private long id;
    private String proxySourceName;
    private String proxySourceType;
    private String requestMap;
    private String requestLayers;
    private String mapServerBinary;
    private String mapServerWorkDir;
    private Boolean isDefault;

    public ProxySourceModel(){
        this.proxySourceType = "mapserver";
        this.isDefault = false;
    }

    public ProxySourceModel(long id, String proxySourceName, String proxySourceType, String requestMap, String requestLayers, String mapServerBinary, String mapServerWorkDir){
        this.id = id;
        this.proxySourceName = proxySourceName;
        this.proxySourceType = proxySourceType;
        this.requestMap = requestMap;
        this.requestLayers = requestLayers;
        this.mapServerBinary = mapServerBinary;
        this.mapServerWorkDir = mapServerWorkDir;
        this.isDefault = false;
    }
}
