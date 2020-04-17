package com.jiin.admin.website.model.yaml;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProxyCacheYamlModel {
    private String[] grids;
    private Integer[] meta_size;
    private Integer meta_buffer;
    private String[] sources;
    private CacheYamlModel cache;

    public ProxyCacheYamlModel(){
        this.grids = new String[] { "GLOBAL_GEODETIC" };
        this.meta_size = new Integer[] { 0, 0 };
        this.meta_buffer = 0;
        this.sources = new String[0];
        this.cache = new CacheYamlModel();
    }

    public ProxyCacheYamlModel(String proxyCacheType, String proxyCacheDirectory, Integer metaSizeX, Integer metaSizeY, Integer metaBuffer, String[] proxySources){
        this.grids = new String[] { "GLOBAL_GEODETIC" };
        this.meta_size = new Integer[] { metaSizeX, metaSizeY };
        this.meta_buffer = metaBuffer;
        this.sources = proxySources;
        this.cache = new CacheYamlModel(proxyCacheType, proxyCacheDirectory);
    }
}
