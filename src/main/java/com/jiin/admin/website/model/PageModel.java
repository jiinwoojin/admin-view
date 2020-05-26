package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Getter
@Setter
public abstract class PageModel {
    private int pg; // Page
    private int sz; // Size
    private int ob; // Order By
    private int sb; // Search By
    private String st; // Search By text

    private int recordCount;

    public PageModel(){
        this.pg = 1;
        this.sz = 8;
    }

    public PageModel(int sz){
        this.pg = 1;
        this.sz = sz;
    }

    public PageModel(int pg, int sz, int ob, int sb, String st){
        this.pg = pg;
        this.sz = sz;
        this.ob = ob;
        this.sb = sb;
        this.st = st;
    }
}
