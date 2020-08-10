package com.jiin.admin.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SeedContainerInfo {
    private String name;
    private String id;
    private String image;
    private String ports;
    private String status;
    private Date createdAt;
    private boolean isDefault;

    public SeedContainerInfo(){

    }

    public SeedContainerInfo(String name, String id, String image, String ports, String status, Date createdAt, boolean isDefault) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.ports = ports;
        this.status = status;
        this.createdAt = createdAt;
        this.isDefault = isDefault;
    }
}
