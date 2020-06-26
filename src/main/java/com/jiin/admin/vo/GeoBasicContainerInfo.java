package com.jiin.admin.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeoBasicContainerInfo {
    private String container;
    private String version;
    private String license;

    public GeoBasicContainerInfo(){

    }

    public GeoBasicContainerInfo(String container, String version, String license){
        this.container = container;
        this.version = version;
        this.license = license;
    }
}
