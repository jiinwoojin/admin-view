package com.jiin.admin.converter.mapbox;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MapboxLayer {
    private String id;
    private String type;
    private String source;
    @JsonProperty("source-layer")
    private String sourceLayer;
    @JsonProperty("filter")
    private List<Object> filter;
    @JsonProperty("layout")
    private MapboxLayout layout;
    @JsonProperty("paint")
    private MapboxPaint paint;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceLayer() {
        return sourceLayer;
    }

    public void setSourceLayer(String sourceLayer) {
        this.sourceLayer = sourceLayer;
    }


    public MapboxLayout getLayout() {
        return layout;
    }

    public void setLayout(MapboxLayout layout) {
        this.layout = layout;
    }

    public MapboxPaint getPaint() {
        return paint;
    }

    public void setPaint(MapboxPaint paint) {
        this.paint = paint;
    }

    public List<Object> getFilter() {
        return filter;
    }

    public void setFilter(List<Object> filter) {
        this.filter = filter;
    }
}
