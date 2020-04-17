package com.jiin.admin.website.model.yaml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestYamlModel {
    private String map;
    private String layers;

    public RequestYamlModel(){

    }

    public RequestYamlModel(String map, String layers){
        this.map = map;
        this.layers = layers;
    }
}
