package com.jiin.admin.converter.gss;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "MapLayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssRootLayer extends GssContainer {
    private List<GssLayer> Layer;

    public List<GssLayer> getLayer() {
        return Layer;
    }

    public void setLayer(List<GssLayer> layer) {
        Layer = layer;
    }
}
