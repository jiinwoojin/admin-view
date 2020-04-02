package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Getter
@Setter
public class PaginationModel {
    private int pg; // Page
    private int sz; // Size
    private int ob; // Order By
    private int sb; // Search By
    private String st; // Search By text

    private int recordCount;

    public PaginationModel(){
        this.sz = 5;
    }

    public PaginationModel(int sz){
        this.sz = sz;
    }

    public PaginationModel(int pg, int sz, int ob, int sb, String st){
        this.pg = pg;
        this.sz = sz;
        this.ob = ob;
        this.sb = sb;
        this.st = st;
    }

    public String getQueryString() {
        String url = "";
        try {
            String temp = (this.st == null) ? "" : URLEncoder.encode(this.st, "UTF-8");
            url = String.format("pg=%d&sz=%d&ob=%d&sb=%d&st=%s", this.pg, this.sz, this.ob, this.sb, temp);
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return url;
    }
}
