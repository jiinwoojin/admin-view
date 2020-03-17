package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.entity.Map;
import com.jiin.admin.website.view.service.ManageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping("manage")
public class ManageController {

    @Value("${project.data-path}")
    private String dataPath;

    @Value("${project.mapserver.binary}")
    private String mapserverBinary;

    @Resource
    private SessionService session;

    @Resource
    private ManageService service;

    @RequestMapping("map-manage")
    public String map(Model model){
        model.addAttribute("sources", service.getSourceList());
        model.addAttribute("mapserverBinary", mapserverBinary);
        model.addAttribute("mapserverWorkingDir", dataPath + "/tmp");
        model.addAttribute("cacheType", "map");
        model.addAttribute("cacheDirectory", dataPath + "/cache");
        model.addAttribute("message", session.message());

        return "page/manage/map-manage";
    }

    @RequestMapping("map-form")
    public String mapForm(Model model) {
        model.addAttribute("map", new Map());
        model.addAttribute("layers", service.getLayerList());

        return "page/manage/map-form";
    }

    @PostMapping("add-source")
    public String addSource(@RequestParam("name") String name,
                            @RequestParam("type") String type,
                            @RequestParam("desc") String desc,
                            @RequestParam("file") MultipartFile file){
        boolean result = service.addSource(name,type,desc,file);
        session.message(String.format("MAP SOURCE [%s] 추가 %s하였습니다.",name,(result ? "성공" : "실패")));
        return "redirect:map-manage";
    }

    @ResponseBody
    @PostMapping("del-source")
    public boolean delSource(@RequestParam("sourceId") Long sourceId){
        return service.delSource(sourceId);
    }

    @RequestMapping("layer-manage")
    public String layer(Model model){
        model.addAttribute("layers", service.getLayerList());
        //model.addAttribute("sources", service.getSourceList());
        model.addAttribute("message", session.message());
        return "page/manage/layer-manage";
    }

    @PostMapping("add-layer")
    public String addLayer(@RequestParam("name") String name,
                           @RequestParam("description") String description,
                           @RequestParam(value = "projection", defaultValue = "epsg:4326") String projection,
                           @RequestParam("middle_folder") String middle_folder,
                           @RequestParam("type") String type,
                           @RequestParam("data_file") MultipartFile data_file) throws IOException {
        boolean result = service.addLayer(name, description, projection, middle_folder, type, data_file);
        session.message(String.format("LAYER [%s] 추가 %s하였습니다.",name,(result ? "성공" : "실패")));
        return "redirect:layer-manage";
    }

    @ResponseBody
    @PostMapping("del-layer")
    public boolean delLayer(@RequestParam("layerId") Long layerId) {
        return service.delLayer(layerId);
    }
}
