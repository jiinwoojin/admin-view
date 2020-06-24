package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MapDTO {
    private Long id;
    private String name;
    private String mapFilePath;
    private String minX;
    private String maxX;
    private String minY;
    private String maxY;
    private String units;
    private String projection;
    private String description;
    private String imageType;
    private boolean isDefault;
    private String registorId;
    private String registorName;
    private Date registTime;

    private Date updateTime;

    private String vrtFilePath;

    public MapDTO(){

    }
}
