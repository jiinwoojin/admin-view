package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.view.service.ProxyCacheService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("display")
public class MVCDisplayController {
    @Resource
    private ProxyCacheService proxyCacheService;

    // 2차원 지도 - Openlayers
    @RequestMapping("ol-display")
    public String openlayersDisplayView(Model model) {
        return "page/display/ol-display";
    }

    // 2차원 지도 - Mapbox
    @RequestMapping("mapbox-display")
    public String mapboxDisplayView(Model model) {
        return "page/display/mapbox-display";
    }

    // 3차원 지도
    @RequestMapping("cesium-display")
    public String cesiumDisplayView(Model model) {
        return "page/display/cesium-display";
    }

    // 일반 지도 URL
    @RequestMapping("common-map")
    public String commonMapDisplayView(Model model) {
        model.addAttribute("layers", proxyCacheService.loadDataListBySelected("LAYERS", true));
        return "page/display/common-map";
    }
}
