package com.jiin.admin.milsymbol.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "wsi")
@XmlAccessorType(XmlAccessType.FIELD)
public class ICOPSWsi {
    //<wsi cd='S*Z*------*****' as='S' ss='F' st='1' sc='2' hl='0' nll='미식별' ncl='Unknown' />
    /**
     * cd : Symbol code
     */
    @XmlAttribute
    private String cd;
    /**
     * as : Apply state
     */
    @XmlAttribute
    private String as;
    /**
     * ss : Symbol state
     */
    @XmlAttribute
    private String ss;
    /**
     * st : Symbol type
     */
    @XmlAttribute
    private String st;
    /**
     * sc : Symbol Category
     */
    @XmlAttribute
    private String sc;
    /**
     * hl : Hierarchical level
     */
    @XmlAttribute
    private String hl;
    /**
     * nll : name in local language name
     */
    @XmlAttribute
    private String nll;
    /**
     * ncl : name in common language name
     */
    @XmlAttribute
    private String ncl;

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getAs() {
        return as;
    }

    public void setAs(String as) {
        this.as = as;
    }

    public String getSs() {
        return ss;
    }

    public void setSs(String ss) {
        this.ss = ss;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(String sc) {
        this.sc = sc;
    }

    public String getHl() {
        return hl;
    }

    public void setHl(String hl) {
        this.hl = hl;
    }

    public String getNll() {
        return nll;
    }

    public void setNll(String nll) {
        this.nll = nll;
    }

    public String getNcl() {
        return ncl;
    }

    public void setNcl(String ncl) {
        this.ncl = ncl;
    }
}
