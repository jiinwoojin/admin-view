package com.jiin.admin.converter.gss;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "MapLayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssMapLayer extends GssContainer {
    private List<GssGroup> Group;
    private List<GssLayer> Layer;

    public List<GssLayer> getLayer() {
        return Layer;
    }

    public void setLayer(List<GssLayer> layer) {
        Layer = layer;
    }

    public List<GssGroup> getGroup() {
        return Group;
    }

    public void setGroup(List<GssGroup> group) {
        Group = group;
    }
}
