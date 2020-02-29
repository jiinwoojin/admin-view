package com.jiin.admin.website.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("display")
public class DisplayMainController {
    @RequestMapping("2d-map")
    public String twoDimensionDisplayView(Model model){
        return "page/display/2d-map";
    }

    @RequestMapping("3d-map")
    public String threeDimensionDisplayView(Model model){
        return "page/display/3d-map";
    }

    @RequestMapping("ol-display")
    public String openlayersDisplayView(Model model){
        return "page/display/ol-display";
    }

    @RequestMapping("mapbox-display")
    public String mapboxDisplayView(Model model) {
        return "page/display/mapbox-display";
    }

    @RequestMapping("cs-display")
    public String cesiumDisplayView(Model model){
        return "page/display/cs-display";
    }

    @RequestMapping("common-map")
    public String commonMapDisplayView(Model model) {
        return "page/display/common-map";
    }
}
