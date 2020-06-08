package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProxyCacheModelV2 {
    private long id;
    private String method;
    private String name;
    private String cacheType;
    private String cacheDirectory;
    private Integer metaSizeX;
    private Integer metaSizeY;
    private Integer metaBuffer;
    private List<String> sources;

    public ProxyCacheModelV2(){
        this.id = 0L;
        this.method = "INSERT";
        this.cacheType = "file";
        this.sources = new ArrayList<>();
    }

    public ProxyCacheModelV2(long id, String method, String name, String cacheType, String cacheDirectory, Integer metaSizeX, Integer metaSizeY, Integer metaBuffer, List<String> proxySourcesWithCaches){
        this.id = id;
        this.method = method;
        this.name = name;
        this.cacheType = cacheType;
        this.cacheDirectory = cacheDirectory;
        this.metaSizeX = metaSizeX;
        this.metaSizeY = metaSizeY;
        this.metaBuffer = metaBuffer;
        this.sources = proxySourcesWithCaches;
    }
}
