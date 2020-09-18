package com.jiin.admin.converter.gss;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement(name = "Feature")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssFeature {
    @XmlAttribute(name = "Name")
    private String Name;
    @XmlAttribute(name = "Description")
    private String Description;
    @XmlAttribute(name = "GeometryStyle")
    private String GeometryStyle;
    @XmlAttribute(name = "LabelStyle")
    private String LabelStyle;
    @XmlElement(name = "VVTStyle")
    private List<GssVVTStyle> VVTStyle;
    private String style;


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getGeometryStyle() {
        return GeometryStyle;
    }

    public void setGeometryStyle(String geometryStyle) {
        GeometryStyle = geometryStyle;
    }

    public String getLabelStyle() {
        return LabelStyle;
    }

    public void setLabelStyle(String labelStyle) {
        LabelStyle = labelStyle;
    }

    public List<GssVVTStyle> getVVTStyle() {
        return VVTStyle;
    }

    public void setVVTStyle(List<GssVVTStyle> VVTStyle) {
        this.VVTStyle = VVTStyle;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
