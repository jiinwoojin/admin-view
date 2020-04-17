package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProxyCacheDTO { // MyBatis Based DTO
    private Long id;
    private String name;
    private Integer metaSizeX;
    private Integer metaSizeY;
    private Integer metaBuffer;
    private List<ProxySourceDTO> sources;
    private List<ProxyCacheDTO> caches;
    private String cacheType;
    private String cacheDirectory;
    private Boolean selected;
    private Boolean isDefault;

    public ProxyCacheDTO(){
        this.sources = new ArrayList<>();
        this.caches = new ArrayList<>();
    }

    public ProxyCacheDTO(Long id, String name, Integer metaSizeX, Integer metaSizeY, Integer metaBuffer, List<ProxySourceDTO> sources, List<ProxyCacheDTO> caches, String cacheType, String cacheDirectory, Boolean selected, Boolean isDefault){
        this.id = id;
        this.name = name;
        this.metaSizeX = metaSizeX;
        this.metaSizeY = metaSizeY;
        this.metaBuffer = metaBuffer;
        this.sources = sources;
        this.caches = caches;
        this.cacheType = cacheType;
        this.cacheDirectory = cacheDirectory;
        this.selected = selected;
        this.isDefault = isDefault;
    }
}
