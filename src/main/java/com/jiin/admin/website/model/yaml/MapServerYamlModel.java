package com.jiin.admin.website.model.yaml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapServerYamlModel {
    private String working_dir;
    private String binary;

    public MapServerYamlModel(){

    }

    public MapServerYamlModel(String working_dir, String binary){
        this.working_dir = working_dir;
        this.binary = binary;
    }
}
