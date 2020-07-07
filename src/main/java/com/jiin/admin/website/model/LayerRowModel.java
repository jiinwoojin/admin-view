package com.jiin.admin.website.model;

import com.jiin.admin.dto.LayerDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LayerRowModel {
    private String name;
    private String description;
    private String projection;
    private String type;
    private String middleFolder;
    private String filename;

    public LayerRowModel(){

    }

    public LayerRowModel(String name, String description, String projection, String type, String middleFolder, String filename) {
        this.name = name;
        this.description = description;
        this.projection = projection;
        this.type = type;
        this.middleFolder = middleFolder;
        this.filename = filename;
    }

    public static LayerDTO convertDTO(LayerRowModel model){
        if(model == null) return null;
        return new LayerDTO(0L, model.getName(), model.getDescription(), model.getProjection().toLowerCase(), model.getMiddleFolder(), model.getType());
    }
}
