package com.jiin.admin.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SynchronizeBasicInfo {
    private String id;
    private String filePath;
    private String recentlyDate;
    private String recentlyFile;

    public SynchronizeBasicInfo(){
        this.id = "UNKNOWN";
        this.filePath = "UNKNOWN";
        this.recentlyDate = "UNKNOWN";
        this.recentlyFile = "UNKNOWN";
    }
}
