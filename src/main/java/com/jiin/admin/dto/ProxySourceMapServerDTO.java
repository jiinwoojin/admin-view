package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxySourceMapServerDTO extends ProxySourceDTO {
    private String requestMap;
    private String requestLayers;
    private String mapServerBinary;
    private String mapServerWorkDir;

    public ProxySourceMapServerDTO(){
        super();
    }

    public ProxySourceMapServerDTO(Long id, String name, String type, Boolean selected, Boolean isDefault, String requestMap, String requestLayers, String mapServerBinary, String mapServerWorkDir) {
        super(id, name, type, selected, isDefault);
        this.requestMap = requestMap;
        this.requestLayers = requestLayers;
        this.mapServerBinary = mapServerBinary;
        this.mapServerWorkDir = mapServerWorkDir;
    }
}
