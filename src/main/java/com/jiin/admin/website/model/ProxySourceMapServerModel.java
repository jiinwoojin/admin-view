package com.jiin.admin.website.model;

import com.jiin.admin.dto.ProxySourceMapServerDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxySourceMapServerModel {
    private long id;
    private String method;
    private String name;
    private String type;
    private String requestMap;
    private String requestLayers;
    private String mapServerBinary;
    private String mapServerWorkDir;

    public ProxySourceMapServerModel() {
        this.method = "INSERT";
        this.type = "mapserver";
    }

    public ProxySourceMapServerModel(long id, String name, String type, String requestMap, String requestLayers, String mapServerBinary, String mapServerWorkDir) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.requestMap = requestMap;
        this.requestLayers = requestLayers;
        this.mapServerBinary = mapServerBinary;
        this.mapServerWorkDir = mapServerWorkDir;
    }

    public static ProxySourceMapServerDTO convertDTO(ProxySourceMapServerModel model) {
        if (model == null) return null;
        return new ProxySourceMapServerDTO(model.getId(), model.getName(), model.getType(), false, false, model.getRequestMap(), model.getRequestLayers(), model.getMapServerBinary(), model.getMapServerWorkDir());
    }
}
