package com.jiin.admin.website.model.yaml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CacheYamlModel {
    private String type;
    private String directory;

    public CacheYamlModel(){
    }

    public CacheYamlModel(String type, String directory){
        this.type = type;
        this.directory = directory;
    }
}
