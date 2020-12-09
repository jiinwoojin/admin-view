
package com.jiin.admin.milsymbol.model;


import com.jiin.admin.converter.gss.GssLayer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="wssl", namespace="urn:icops.cngsol.co.kr")
@XmlAccessorType(XmlAccessType.FIELD)
public class ICOPSWssl {

    @XmlElement(name="wsi", namespace="urn:icops.cngsol.co.kr")
    private List<ICOPSWsi> wsi;

    public List<ICOPSWsi> getWsi() {
        return wsi;
    }

    public void setWsi(List<ICOPSWsi> wsi) {
        this.wsi = wsi;
    }
}
