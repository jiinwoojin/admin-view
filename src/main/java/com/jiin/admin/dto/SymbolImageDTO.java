package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SymbolImageDTO {
    private Long id;
    private String name;
    private String description;
    private Date registTime;
    private Date updateTime;
    private String imageFilePath;
    private String image2xFilePath;
    private String jsonFilePath;
    private String json2xFilePath;
    private boolean isDefault;

    public SymbolImageDTO(){

    }

    public SymbolImageDTO(Long id, String name, String description, Date registTime, Date updateTime, String imageFilePath, String image2xFilePath, String jsonFilePath, String json2xFilePath, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.registTime = registTime;
        this.updateTime = updateTime;
        this.imageFilePath = imageFilePath;
        this.image2xFilePath = image2xFilePath;
        this.jsonFilePath = jsonFilePath;
        this.json2xFilePath = json2xFilePath;
        this.isDefault = isDefault;
    }
}
