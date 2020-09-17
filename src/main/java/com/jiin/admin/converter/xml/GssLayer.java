package com.jiin.admin.converter.xml;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "Layer")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class GssLayer {
    @XmlAttribute(name = "Category")
    private String category;
    @XmlAttribute(name = "Name")
    private String name;
    @XmlAttribute(name = "FACC")
    private String facc;
    @XmlAttribute(name = "GeometryType")
    private String geometryType;
    @XmlAttribute(name = "SHPSource")
    private String shpSource;
    @XmlAttribute(name = "GDBSource")
    private String gdbSource;
    @XmlAttribute(name = "Map")
    private String map;
    @XmlAttribute(name = "DisplayType")
    private String displayType;
    @XmlElement(name="Feature")
    private List<GssFeature> features;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFacc() {
        return facc;
    }

    public void setFacc(String facc) {
        this.facc = facc;
    }

    public String getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(String geometryType) {
        this.geometryType = geometryType;
    }

    public String getShpSource() {
        return shpSource;
    }

    public void setShpSource(String shpSource) {
        this.shpSource = shpSource;
    }

    public String getGdbSource() {
        return gdbSource;
    }

    public void setGdbSource(String gdbSource) {
        this.gdbSource = gdbSource;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
