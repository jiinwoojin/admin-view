package com.jiin.admin.website.model;

import com.jiin.admin.dto.ProxyCacheDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProxyCacheModel {
    private long id;
    private String method;
    private String name;
    private String cacheType;
    private String cacheDirectory;
    private Integer metaSizeX;
    private Integer metaSizeY;
    private Integer metaBuffer;
    private String format;
    private List<String> sources;

    public ProxyCacheModel(){
        this.id = 0L;
        this.method = "INSERT";
        this.cacheType = "file";
        this.sources = new ArrayList<>();
    }

    public ProxyCacheModel(long id, String method, String name, String cacheType, String cacheDirectory, Integer metaSizeX, Integer metaSizeY, Integer metaBuffer, String format, List<String> sources){
        this.id = id;
        this.method = method;
        this.name = name;
        this.cacheType = cacheType;
        this.cacheDirectory = cacheDirectory;
        this.metaSizeX = metaSizeX;
        this.metaSizeY = metaSizeY;
        this.metaBuffer = metaBuffer;
        this.format = format;
        this.sources = sources;
    }

    // convertDTO 에서는 caches 데이터가 무의미해서 new ArrayList() 로 두었다.
    public static ProxyCacheDTO convertDTO(ProxyCacheModel model){
        if(model == null) return null;
        return new ProxyCacheDTO(model.getId(), model.getName(), model.getMetaSizeX(), model.getMetaSizeY(), model.getMetaBuffer(), new ArrayList(), model.getCacheType(), model.getCacheDirectory(), model.getFormat(), false, false);
    }
}
