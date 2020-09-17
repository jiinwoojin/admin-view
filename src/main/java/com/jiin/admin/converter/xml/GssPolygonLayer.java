package com.jiin.admin.converter.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "LineLayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssPolygonLayer {
    private String type;
    private String Picture;
    private String TextureFill;
    private String Transparent;
    private List<GssLineLayer> LineLayer;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getTextureFill() {
        return TextureFill;
    }

    public void setTextureFill(String textureFill) {
        TextureFill = textureFill;
    }

    public String getTransparent() {
        return Transparent;
    }

    public void setTransparent(String transparent) {
        Transparent = transparent;
    }

    public List<GssLineLayer> getLineLayer() {
        return LineLayer;
    }

    public void setLineLayer(List<GssLineLayer> lineLayer) {
        LineLayer = lineLayer;
    }
}
