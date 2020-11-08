package com.jiin.admin.converter.gss;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "PointLayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssPointLayer {
    @XmlAttribute
    private String type;
    private String Color;
    private String Size;
    private String Shape;
    private String Picture;
    private String Transparent;
    public GssPointLayer() { }
    public GssPointLayer(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getShape() {
        return Shape;
    }

    public void setShape(String shape) {
        Shape = shape;
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
}
