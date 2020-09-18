package com.jiin.admin.converter.gss;

import javax.xml.bind.annotation.*;
import java.util.List;


@XmlRootElement(name = "MapStyle")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssRootStyle extends GssContainer {
    private List<GssStyle> Style;

    public List<GssStyle> getStyle() {
        return Style;
    }

    public void setStyle(List<GssStyle> style) {
        Style = style;
    }
}
