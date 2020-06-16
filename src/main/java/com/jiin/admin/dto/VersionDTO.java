package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VersionDTO {
    private String map;

    private Double version;

    private Long zipFileSize;

    private String zipFilePath;
}
