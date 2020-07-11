package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MapVersionDTO {
    private Long id;
    private Long mapId;
    private Double version;
    private String zipFilePath;
    private Long zipFileSize;
    private Date uploadDate;

    public MapVersionDTO() {
        this.version = 1.0;
    }

    public MapVersionDTO(long id, Long mapId, Double version, String zipFilePath, Long zipFileSize, Date uploadDate) {
        this.id = id;
        this.mapId = mapId;
        this.version = version;
        this.zipFilePath = zipFilePath;
        this.zipFileSize = zipFileSize;
        this.uploadDate = uploadDate;
    }
}
