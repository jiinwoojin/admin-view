package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxySourceDTO {
    private Long id;
    private String name;
    private String type;
    private String requestMap;
    private String requestLayers;
    private String mapServerBinary;
    private String mapServerWorkDir;
    private Boolean selected;
    private Boolean isDefault;

    public ProxySourceDTO(){

    }

    public ProxySourceDTO(Long id, String name, String type, String requestMap, String requestLayers, String mapServerBinary, String mapServerWorkDir, Boolean selected, Boolean isDefault){
        this.id = id;
        this.name = name;
        this.type = type;
        this.requestMap = requestMap;
        this.requestLayers = requestLayers;
        this.mapServerBinary = mapServerBinary;
        this.mapServerWorkDir = mapServerWorkDir;
        this.selected = selected;
        this.isDefault = isDefault;
    }
}
