package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProxySelectRequestModel {
    private List<String> layers;
    private List<String> sources;
    private List<String> caches;

    public ProxySelectRequestModel() {
        this.layers = new ArrayList<>();
        this.sources = new ArrayList<>();
        this.caches = new ArrayList<>();
    }

    public ProxySelectRequestModel(List<String> layers, List<String> sources, List<String> caches) {
        this.layers = layers;
        this.sources = sources;
        this.caches = caches;
    }
}
