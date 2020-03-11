package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.gis.CacheService;
import com.jiin.admin.website.util.MapProxyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("cache")
public class CacheMainController {
    @Autowired
    private CacheService cacheService;

    @RequestMapping("layers")
    public String cacheSourcesPage(Model model){
        model.addAttribute("layer", cacheService.getCachedLayerData());
        model.addAttribute("request", cacheService.getCachedRequestData());
        model.addAttribute("boundingBox", cacheService.getBoundingBoxInfoWithCrs());
        model.addAttribute("serviceURL", MapProxyUtil.getServiceURL());
        return "page/cache/layers";
    }

    @RequestMapping("grids")
    public String cacheGridsPage(Model model){
        return "page/cache/grids";
    }

    @RequestMapping("seeds")
    public String cacheConfigPage(Model model){
        return "page/cache/seeds";
    }
}
