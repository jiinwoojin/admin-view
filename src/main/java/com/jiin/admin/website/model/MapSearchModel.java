package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Getter
@Setter
public class MapSearchModel extends PaginationModel {
    private String sDate; // Upload Start Date
    private String eDate; // Upload End Date
    private String iType; // Image Type (png, jpeg)
    private String units; // Unit (KM, MILEs, FEETs etc)

    public MapSearchModel(){
        super();
        this.units = "ALL"; // Default Value
    }

    public MapSearchModel(int pg, int sz, int ob, int sb, String st, String sDate, String eDate, String iType, String units){
        super(pg, sz, ob, sb, st);
        this.sDate = sDate;
        this.eDate = eDate;
        this.iType = iType;
        this.units = units;
    }

    public String getQueryString() {
        String url = "";
        try {
            String temp = (super.getSt() == null) ? "" : URLEncoder.encode(super.getSt(), "UTF-8");
            url = String.format("pg=%d&sz=%d&ob=%d&sb=%d&st=%s&sDate=%s&eDate=%s&iType=%s&units=%s", super.getPg(), super.getSz(), super.getOb(), super.getSb(), temp, this.sDate, this.eDate, this.iType, this.units);
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return url;
    }
}
