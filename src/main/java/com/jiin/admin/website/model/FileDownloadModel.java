package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDownloadModel {
    /**
     * CS 상환도 마지막 Update 정보
     */
    private String lastUpdate;

    /**
     * CS 상황도 MAP 정보
     */
    private String map;

    /**
     * CS 상황도 현재 MAP 버전 정보
     */
    private Double currentVersion;
}
