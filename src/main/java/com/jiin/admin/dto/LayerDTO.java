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

    public LayerDTO(){

    }
}
