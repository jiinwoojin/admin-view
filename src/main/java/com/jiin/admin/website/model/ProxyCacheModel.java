package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProxyCacheModel {
    private long id;
    private String proxyCacheName;
    private String proxyCacheType;
    private String proxyCacheDirectory;
    private Integer metaSizeX;
    private Integer metaSizeY;
    private Integer metaBuffer;
    private List<String> proxySourcesWithCaches;
    private Boolean isDefault;

    public ProxyCacheModel(){
        this.proxyCacheType = "file";
        this.proxySourcesWithCaches = new ArrayList<>();
        this.isDefault = false;
    }

    public ProxyCacheModel(long id, String proxyCacheName, String proxyCacheType, String proxyCacheDirectory, Integer metaSizeX, Integer metaSizeY, Integer metaBuffer, List<String> proxySourcesWithCaches){
        this.id = id;
        this.proxyCacheName = proxyCacheName;
        this.proxyCacheType = proxyCacheType;
        this.proxyCacheDirectory = proxyCacheDirectory;
        this.metaSizeX = metaSizeX;
        this.metaSizeY = metaSizeY;
        this.metaBuffer = metaBuffer;
        this.proxySourcesWithCaches = proxySourcesWithCaches;
        this.isDefault = false;
    }
}
