package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDownloadModel {
    /**
     * CS 상황도 MAP 정보
     */
    private String map;

    /**
     * down 받을 version
     */
    private Double version;
}
