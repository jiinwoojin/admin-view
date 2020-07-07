package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayerRowModel {
    private String name;
    private String description;
    private String projection;
    private String middleFolder;
    private String type;
    private String originalDataPath;

    public LayerRowModel(){

    }

    public LayerRowModel(String name, String description, String projection, String middleFolder, String type, String originalDataPath) {
        this.name = name;
        this.description = description;
        this.projection = projection;
        this.middleFolder = middleFolder;
        this.type = type;
        this.originalDataPath = originalDataPath;
    }
}
