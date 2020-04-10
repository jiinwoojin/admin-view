package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProxyLayerModel {
    private long id;
    private String proxyLayerName;
    private String proxyLayerTitle;
    private List<String> proxySources;

    public ProxyLayerModel(){
        this.proxySources = new ArrayList<>();
    }

    public ProxyLayerModel(long id, String proxyLayerName, String proxyLayerTitle, List<String> proxySources){
        this.id = id;
        this.proxyLayerName = proxyLayerName;
        this.proxyLayerTitle = proxyLayerTitle;
        this.proxySources = proxySources;
    }
}
