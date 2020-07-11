package com.jiin.admin.website.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("display")
public class MVCDisplayController {
    @RequestMapping("2d-map")
    public String twoDimensionDisplayView(Model model) {
        return "page/display/2d-map";
    }

    @RequestMapping("3d-map")
    public String threeDimensionDisplayView(Model model) {
        return "page/display/3d-map";
    }

    @RequestMapping("ol-display")
    public String openlayersDisplayView(Model model) {
        return "page/display/ol-display";
    }

    @RequestMapping("mapbox-display")
    public String mapboxDisplayView(Model model) {
        return "page/display/mapbox-display";
    }

    @RequestMapping("cesium-display")
    public String cesiumDisplayView(Model model) {
        return "page/display/cesium-display";
    }

    // 상황도 도시 (Openlayers)
    @RequestMapping("symbol-map")
    public String symbolizeDisplayView(Model model) {
        return "page/display/symbol-map";
    }

    // 공통 지도 : 2 차원 + 3 차원 (Mapbox GL JS, Cesium)
    @RequestMapping("common-map")
    public String commonMapDisplayView(Model model) {
        return "page/display/common-map";
    }

    // 가시화 지도 (ol-ext or Mapbox JS)
    @RequestMapping("ext-map")
    public String extensionVisualizeDisplayView(Model model) {
        return "page/display/ext-map";
    }
}
