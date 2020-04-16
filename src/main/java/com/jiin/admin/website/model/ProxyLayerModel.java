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
    private List<String> proxyCaches;
    private Boolean isDefault;

    public ProxyLayerModel(){
        this.proxySources = new ArrayList<>();
        this.proxyCaches = new ArrayList<>();
        this.isDefault = false;
    }

    public ProxyLayerModel(long id, String proxyLayerName, String proxyLayerTitle, List<String> proxySources, List<String> proxyCaches){
        this.id = id;
        this.proxyLayerName = proxyLayerName;
        this.proxyLayerTitle = proxyLayerTitle;
        this.proxySources = proxySources;
        this.proxyCaches = proxyCaches;
        this.isDefault = false;
    }
}
