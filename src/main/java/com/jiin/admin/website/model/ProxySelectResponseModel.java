package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProxySelectResponseModel {
    private List<Map<String, Object>> layers;
    private List<String> sources;
    private List<Map<String, Object>> caches;

    public ProxySelectResponseModel(){
        this.layers = new ArrayList<>();
        this.sources = new ArrayList<>();
        this.caches = new ArrayList<>();
    }

    public ProxySelectResponseModel(List<Map<String, Object>> layers, List<String> sources, List<Map<String, Object>> caches){
        this.layers = layers;
        this.sources = sources;
        this.caches = caches;
    }
}
