package com.jiin.admin.converter.gss;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "Group")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssGroup {
    @XmlAttribute(name = "Category")
    private String Category;
    @XmlAttribute(name = "Name")
    private String Name;
    @XmlAttribute(name = "GeometryType")
    private String GeometryType;
    @XmlElement
    private List<GssLayer> Layer;

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

    public String getGeometryType() {
        return GeometryType;
    }

    public void setGeometryType(String geometryType) {
        GeometryType = geometryType;
    }

    public List<GssLayer> getLayer() {
        return Layer;
    }

    public void setLayer(List<GssLayer> layer) {
        Layer = layer;
    }
}
