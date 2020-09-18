package com.jiin.admin.converter.mapbox;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiin.admin.converter.gss.GssContainer;
import com.jiin.admin.converter.gss.GssLayer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MapboxRoot {
    private Integer version;
    private String name;
    private Map metadata = new HashMap();
    private Map sources = new HashMap();
    private String sprite;
    private String glyphs;
    private List<MapboxLayer> layers = new ArrayList<>();
    private String id;
    @JsonIgnore
    private List<String> layerIds = new ArrayList<>();



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map getMetadata() {
        return metadata;
    }

    public void setMetadata(Map metadata) {
        this.metadata = metadata;
    }

    public Map getSources() {
        return sources;
    }

    public void setSources(Map sources) {
        this.sources = sources;
    }

    public String getSprite() {
        return sprite;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    public String getGlyphs() {
        return glyphs;
    }

    public void setGlyphs(String glyphs) {
        this.glyphs = glyphs;
    }

    public List<MapboxLayer> getLayers() {
        return layers;
    }

    public void setLayers(List<MapboxLayer> layers) {
        this.layers = layers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void putSources(String name, MapboxSource source) {
        this.sources.put(name,source);
    }

    public void addLayers(MapboxLayer layer) {
        this.layerIds.add(layer.getId());
        this.layers.add(layer);
    }
    public boolean existLayerId(String layerId) {
        return this.layerIds.contains(layerIds);
    }
}
