package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.gis.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("map")
public class MapMainController {
    @Autowired
    private MapService mapService;

    @RequestMapping("list")
    public String mapListPage(Model model){
        model.addAttribute("maps", mapService.findAllMapData());
        return "page/map/list";
    }

    @RequestMapping("create")
    public String mapCreatePage(Model model){
        return "page/map/form";
    }

    @RequestMapping("edit")
    public String mapEditPage(Model model, @RequestParam String name){
        model.addAttribute("map", mapService.findByName(name));
        return "page/map/form";
    }
}
