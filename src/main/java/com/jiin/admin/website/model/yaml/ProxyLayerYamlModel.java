package com.jiin.admin.website.model.yaml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxyLayerYamlModel {
    private String name;
    private String title;
    private String[] sources;

    public ProxyLayerYamlModel(){
        this.sources = new String[0];
    }

    public ProxyLayerYamlModel(String name, String title, String[] sources){
        this.name = name;
        this.title = title;
        this.sources = sources;
    }
}
