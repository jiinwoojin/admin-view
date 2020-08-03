package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Getter
@Setter
public class SymbolPositionPageModel {
    private long imgId;
    private int pstPg;
    private int pstSz;
    private int pstOb;
    private String pstSt;

    public SymbolPositionPageModel(){
        this.pstPg = 1;
        this.pstSz = 10;
        this.pstOb = 0;
        this.pstSt = "";
    }

    public SymbolPositionPageModel(long imgId, int pstPg, int pstSz, int pstOb, String pstSt) {
        this.imgId = imgId;
        this.pstPg = pstPg;
        this.pstSz = pstSz;
        this.pstOb = pstOb;
        this.pstSt = pstSt;
    }

    public String getQueryString() {
        String url = "";
        try {
            String temp = (this.pstSt == null) ? "" : URLEncoder.encode(this.pstSt, "UTF-8");
            url = String.format("imgId=%d&pstPg=%d&pstSz=%d&pstOb=%d&pstSt=%s", this.imgId, this.pstPg, this.pstSz, this.pstOb, temp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}
