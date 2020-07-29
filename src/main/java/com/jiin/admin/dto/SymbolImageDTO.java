package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class SymbolImageDTO {
    private Long id;
    private String name;
    private String description;
    private Date registTime;
    private String registorId;
    private String registorName;
    private Date updateTime;
    private String imageFilePath;
    private String image2xFilePath;
    private String jsonFilePath;
    private String json2xFilePath;
    private boolean isDefault;

    List<SymbolPositionDTO> positions;

    public SymbolImageDTO(){
        this.positions = new ArrayList<>();
    }

    public SymbolImageDTO(Long id, String name, String description, Date registTime, String registorId, String registorName, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.registTime = registTime;
        this.registorId = registorId;
        this.registorName = registorName;
        this.isDefault = isDefault;
    }

    public SymbolImageDTO(Long id, String name, String description, Date registTime, String registorId, String registorName, Date updateTime, String imageFilePath, String image2xFilePath, String jsonFilePath, String json2xFilePath, boolean isDefault, List<SymbolPositionDTO> positions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.registTime = registTime;
        this.registorId = registorId;
        this.registorName = registorName;
        this.updateTime = updateTime;
        this.imageFilePath = imageFilePath;
        this.image2xFilePath = image2xFilePath;
        this.jsonFilePath = jsonFilePath;
        this.json2xFilePath = json2xFilePath;
        this.isDefault = isDefault;
        this.positions = positions;
    }
}
