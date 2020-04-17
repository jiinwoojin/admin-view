package com.jiin.admin.website.model.yaml;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MapProxyYamlModel {
    private Map<String, Object> services;
    private List<ProxyLayerYamlModel> layers;
    private Map<String, ProxyCacheYamlModel> caches;
    private Map<String, ProxySourceYamlModel> sources;

    public MapProxyYamlModel(){
        this.services = new HashMap<>();
        this.layers = new ArrayList<>();
        this.caches = new HashMap<>();
        this.sources = new HashMap<>();
    }
}
