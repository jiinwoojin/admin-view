package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class LayerDTO {
    private Long id;
    private String name;
    private String description;
    private String projection;
    private String middleFolder;
    private String type;
    private String layerFilePath;
    private String dataFilePath;
    private boolean isDefault;
    private String registorId;
    private String registorName;
    private Date registTime;
    private Date updateTime;
    private Double version;

    public LayerDTO() {

    }

    public LayerDTO(Long id, String name, String description, String projection, String middleFolder, String type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.projection = projection;
        this.middleFolder = middleFolder;
        this.type = type;
    }
}
