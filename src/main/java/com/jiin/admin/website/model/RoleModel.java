package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleModel {
    private String title;
    private String label;
    private boolean mapBasic;
    private boolean mapManage;
    private boolean layerManage;
    private boolean cacheManage;
    private boolean accountManage;

    public RoleModel() {

    }

    public RoleModel(String title, String label, boolean mapBasic, boolean mapManage, boolean layerManage, boolean cacheManage, boolean accountManage) {
        this.title = title;
        this.label = label;
        this.mapBasic = mapBasic;
        this.mapManage = mapManage;
        this.layerManage = layerManage;
        this.cacheManage = cacheManage;
        this.accountManage = accountManage;
    }
}
