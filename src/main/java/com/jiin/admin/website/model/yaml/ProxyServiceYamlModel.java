package com.jiin.admin.website.model.yaml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxyServiceYamlModel {
    private String[] versions;
    private String[] srs;
    private String[] bbox_srs;
    private String[] image_formats;

    public ProxyServiceYamlModel(){
        this.versions = new String[0];
        this.srs = new String[0];
        this.bbox_srs = new String[0];
        this.image_formats = new String[0];
    }

    public ProxyServiceYamlModel(String[] versions, String[] srs, String[] bbox_srs, String[] image_formats){
        this.versions = versions;
        this.srs = srs;
        this.bbox_srs = bbox_srs;
        this.image_formats = image_formats;
    }
}
