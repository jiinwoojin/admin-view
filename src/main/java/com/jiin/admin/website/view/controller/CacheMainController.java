package com.jiin.admin.website.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("cache")
public class CacheMainController {
    @RequestMapping("layers")
    public String cacheSourcesPage(Model model){
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
