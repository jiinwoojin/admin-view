package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {
    private Long id;
    private String title;
    private String label;
    private Boolean mapBasic;
    private Boolean mapManage;
    private Boolean layerManage;
    private Boolean cacheManage;
    private Boolean accountManage;

    public RoleDTO() {

    }

    public RoleDTO(Long id, String title, String label, Boolean mapBasic, Boolean mapManage, Boolean layerManage, Boolean cacheManage, Boolean accountManage) {
        this.id = id;
        this.title = title;
        this.label = label;
        this.mapBasic = mapBasic;
        this.mapManage = mapManage;
        this.layerManage = layerManage;
        this.cacheManage = cacheManage;
        this.accountManage = accountManage;
    }
}
