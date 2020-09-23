package com.jiin.admin.converter.gss;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "Layer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssLayer {
    @XmlAttribute(name = "Category")
    private String Category;
    @XmlAttribute(name = "Name")
    private String Name;
    @XmlAttribute(name = "FACC")
    private String FACC;
    @XmlAttribute(name = "GeometryType")
    private String GeometryType;
    @XmlAttribute(name = "SHPSource")
    private String SHPSource;
    @XmlAttribute(name = "GDBSource")
    private String GDBSource;
    @XmlAttribute(name = "Map")
    private String Map;
    @XmlAttribute(name = "DisplayType")
    private String DisplayType;
    @XmlAttribute(name = "LabelColumn")
    private String LabelColumn;
    @XmlElement
    private List<GssFeature> Feature;

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFACC() {
        return FACC;
    }

    public void setFACC(String FACC) {
        this.FACC = FACC;
    }

    public String getGeometryType() {
        return GeometryType;
    }

    public void setGeometryType(String geometryType) {
        GeometryType = geometryType;
    }

    public String getSHPSource() {
        return SHPSource;
    }

    public void setSHPSource(String SHPSource) {
        this.SHPSource = SHPSource;
    }

    public String getGDBSource() {
        return GDBSource;
    }

    public void setGDBSource(String GDBSource) {
        this.GDBSource = GDBSource;
    }

    public String getMap() {
        return Map;
    }

    public void setMap(String map) {
        Map = map;
    }

    public String getDisplayType() {
        return DisplayType;
    }

    public void setDisplayType(String displayType) {
        DisplayType = displayType;
    }

    public List<GssFeature> getFeature() {
        return Feature;
    }

    public void setFeature(List<GssFeature> feature) {
        Feature = feature;
    }

    public String getLabelColumn() {
        return LabelColumn;
    }

    public void setLabelColumn(String labelColumn) {
        LabelColumn = labelColumn;
    }
}
