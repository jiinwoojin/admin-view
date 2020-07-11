package com.jiin.admin.website.model;

import com.jiin.admin.Constants;
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
    private String dataFileFullPath;

    public LayerRowModel() {

    }

    public LayerRowModel(String name, String description, String projection, String type, String dataFileFullPath) {
        this.name = name;
        this.description = description;
        this.projection = projection;
        this.type = type;
        this.dataFileFullPath = dataFileFullPath;
    }

    public static LayerDTO convertDTO(LayerRowModel model, String dataPath) {
        if (model == null) {
            return null;
        }

        String middlePath = model.getDataFileFullPath().replace(dataPath, "");
        middlePath = middlePath.replaceFirst(String.format("%s/", Constants.DATA_PATH), "");

        String[] split = middlePath.split("/");
        String filename = split[split.length - 1];
        middlePath = middlePath.replace(String.format("/%s", filename), "");

        return new LayerDTO(0L, model.getName(), model.getDescription(), model.getProjection().toLowerCase(), middlePath, model.getType());
    }
}
