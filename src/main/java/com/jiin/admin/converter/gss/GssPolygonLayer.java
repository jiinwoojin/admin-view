package com.jiin.admin.converter.gss;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "PolygonLayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssPolygonLayer {
    @XmlAttribute
    private String type;
    private String Picture;
    private String Color;
    private Boolean TextureFill;
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

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public Boolean getTextureFill() {
        return TextureFill;
    }

    public void setTextureFill(Boolean textureFill) {
        TextureFill = textureFill;
    }
}
