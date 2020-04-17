package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.entity.MapEntity;
import com.jiin.admin.website.model.LayerSearchModel;
import com.jiin.admin.website.model.MapSearchModel;
import com.jiin.admin.website.view.service.ManageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.text.ParseException;

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
    public String map(Model model, MapSearchModel mapSearchModel) {
        model.addAttribute("mapserverBinary", mapserverBinary);
        model.addAttribute("mapserverWorkingDir", dataPath + "/tmp");
        model.addAttribute("cacheType", "map");
        model.addAttribute("cacheDirectory", dataPath + "/cache");

        model.addAttribute("resMap", service.getMapListByPaginationModel(mapSearchModel));
        model.addAttribute("obList", service.mapOrderByOptions());
        model.addAttribute("sbList", service.mapSearchByOptions());
        model.addAttribute("message", session.message());

        return "page/manage/map-manage";
    }

    @RequestMapping("map-form")
    public String mapForm(Model model, @ModelAttribute MapEntity map) {
        model.addAttribute("layers", service.getLayerList());

        return "page/manage/map-form";
    }

    @PostMapping("add-map")
    public String addMap(@Valid MapEntity map, @RequestParam("layerList") String layerList) throws IOException {
        boolean result = service.addMap(map, layerList);
        session.message(String.format("MAP [%s] 추가 %s하였습니다.", map.getName(), (result ? "성공" : "실패")));
        return "redirect:map-manage?pg=1&sz=9&iType=ALL&units=ALL";
    }

    /*@PostMapping("add-source")
    public String addSource(@RequestParam("name") String name,
                            @RequestParam("type") String type,
                            @RequestParam("desc") String desc,
                            @RequestParam("file") MultipartFile file){
        boolean result = service.addSource(name,type,desc,file);
        session.message(String.format("MAP [%s] 추가 %s하였습니다.",name,(result ? "성공" : "실패")));
        return "redirect:map-manage";
    }*/

    @ResponseBody
    @PostMapping("del-map")
    public boolean delMap(@RequestParam("mapId") Long mapId){
        return service.delMap(mapId);
    }

    @RequestMapping("layer-manage")
    public String layer(Model model, LayerSearchModel layerSearchModel) throws ParseException {
        model.addAttribute("resMap", service.getLayerListByPaginationModel(layerSearchModel));
        model.addAttribute("obList", service.layerOrderByOptions());
        model.addAttribute("sbList", service.layerSearchByOptions());
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
        return "redirect:layer-manage?pg=1&sz=9&lType=ALL";
    }

    @ResponseBody
    @PostMapping("del-layer")
    public boolean delLayer(@RequestParam("layerId") Long layerId) {
        return service.delLayer(layerId);
    }
}
