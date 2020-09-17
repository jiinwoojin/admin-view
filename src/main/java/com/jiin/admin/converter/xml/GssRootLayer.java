package com.jiin.admin.converter.xml;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "MapLayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssRootLayer extends GssContainer {
    @XmlElement(name="Layer")
    private List<GssLayer> layers;

    public List<GssLayer> getLayers() {
        return layers;
    }

    public void setLayers(List<GssLayer> layers) {
        this.layers = layers;
    }
}
