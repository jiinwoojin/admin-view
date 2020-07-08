package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Getter
@Setter
public class LayerPageModel extends PageModel {
    private String sDate; // Upload Start Date
    private String eDate; // Upload End Date
    private String lType; // Raster / Vector Type

    public LayerPageModel() {
        super();
        this.sDate = "";
        this.eDate = "";
        this.lType = "ALL"; // Default Value
    }

    public LayerPageModel(int pg, int sz, int ob, int sb, String st, String sDate, String eDate, String lType) {
        super(pg, sz, ob, sb, st);
        this.sDate = sDate;
        this.eDate = eDate;
        this.lType = lType;
    }

    public String getQueryString() {
        String url = "";
        try {
            String temp = (super.getSt() == null) ? "" : URLEncoder.encode(super.getSt(), "UTF-8");
            url = String.format("pg=%d&sz=%d&ob=%d&sb=%d&st=%s&sDate=%s&eDate=%s&lType=%s", super.getPg(), super.getSz(), super.getOb(), super.getSb(), temp, this.sDate, this.eDate, this.lType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}
