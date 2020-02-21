package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Getter
@Setter
public class PaginationModel {
    private long pg; // Page
    private long sz; // Size
    private long ob; // Order By
    private String st; // Search By text

    public PaginationModel(){
        this.sz = 10L;
    }

    public PaginationModel(long pg, long sz, long ob, String st){
        this.pg = pg;
        this.sz = sz;
        this.ob = ob;
        this.st = st;
    }

    public String getQueryString() {
        String url = "";
        try {
            String temp = (this.st == null) ? "" : URLEncoder.encode(this.st, "UTF-8");
            url = String.format("pg=%d&sz=%d&ob=%d&st=%s", this.pg, this.sz, this.ob, temp);
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return url;
    }
}
