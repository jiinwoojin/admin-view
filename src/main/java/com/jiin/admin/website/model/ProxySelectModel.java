package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProxySelectModel {
    private List<String> layers;
    private List<String> sources;
    private List<String> caches;

    public ProxySelectModel(){
        this.layers = new ArrayList<>();
        this.sources = new ArrayList<>();
        this.caches = new ArrayList<>();
    }

    public ProxySelectModel(List<String> layers, List<String> sources, List<String> caches){
        this.layers = layers;
        this.sources = sources;
        this.caches = caches;
    }
}
