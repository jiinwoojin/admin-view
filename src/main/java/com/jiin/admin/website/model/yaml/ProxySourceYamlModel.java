package com.jiin.admin.website.model.yaml;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ProxySourceYamlModel {
    private String type;
    private RequestYamlModel req;
    private MapServerYamlModel mapserver;

    public ProxySourceYamlModel(){
        this.type = "mapserver";
        this.req = new RequestYamlModel();
        this.mapserver = new MapServerYamlModel();
    }

    public ProxySourceYamlModel(String type, String requestMap, String requestLayers, String mapServerBinary, String mapServerWorkDir){
        this.type = type;
        this.req = new RequestYamlModel(requestMap, requestLayers);
        this.mapserver = new MapServerYamlModel(mapServerWorkDir, mapServerBinary);
    }
}
