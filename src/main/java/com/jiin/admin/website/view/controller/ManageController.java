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

    // MAP 파일 목록
    @RequestMapping("map-manage")
    public String map(Model model, MapSearchModel mapSearchModel) {
        model.addAttribute("resMap", service.getMapListByPaginationModel(mapSearchModel));
        model.addAttribute("obList", service.mapOrderByOptions());
        model.addAttribute("sbList", service.mapSearchByOptions());
        model.addAttribute("message", session.message());

        model.addAttribute("qs", mapSearchModel.getQueryString());
        return "page/manage/map-manage";
    }

    // MAP 파일 추가 페이지
    @RequestMapping("map-form")
    public String mapForm(Model model, @ModelAttribute MapEntity map, MapSearchModel mapSearchModel) {
        model.addAttribute("layers", service.getLayerList());
        model.addAttribute("qs", mapSearchModel.getQueryString());
        return "page/manage/map-form";
    }

    // MAP 파일 추가 POST 동작
    @PostMapping("add-map")
    public String addMap(@Valid MapEntity map, @RequestParam("layerList") String layerList) throws IOException {
        boolean result = service.addMap(map, layerList);
        session.message(String.format("MAP [%s] 추가 %s하였습니다.", map.getName(), (result ? "성공" : "실패")));
        return "redirect:map-manage?pg=1&sz=9&iType=ALL&units=ALL";
    }

    // MAP 파일 수정 페이지
    @RequestMapping("map-edit")
    public String mapEditPage(Model model, @RequestParam long id, MapSearchModel mapSearchModel){
        model.addAttribute("mapEntity", service.findMapEntityById(id));
        model.addAttribute("selectLayers", service.findLayerEntitiesByMapId(id));
        model.addAttribute("layers", service.getLayerList());
        model.addAttribute("qs", mapSearchModel.getQueryString());
        return "page/manage/map-edit";
    }

    // MAP 파일 수정 POST 동작
    @PostMapping("update-map")
    public String updateMap(@Valid MapEntity map, @RequestParam("layerList") String layerList) throws IOException {
        boolean result = service.updateMap(map, layerList);
        session.message(String.format("MAP [%s] 수정 %s하였습니다.", map.getName(), (result ? "성공" : "실패")));
        return "redirect:map-manage?pg=1&sz=9&iType=ALL&units=ALL";
    }

    // MAP 파일 삭제 REST API
    @ResponseBody
    @PostMapping("del-map")
    public boolean delMap(@RequestParam("mapId") Long mapId){
        return service.delMap(mapId);
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

    // LAYER 파일 목록
    @RequestMapping("layer-manage")
    public String layer(Model model, LayerSearchModel layerSearchModel) throws ParseException {
        model.addAttribute("resMap", service.getLayerListByPaginationModel(layerSearchModel));
        model.addAttribute("obList", service.layerOrderByOptions());
        model.addAttribute("sbList", service.layerSearchByOptions());
        model.addAttribute("message", session.message());
        return "page/manage/layer-manage";
    }

    // LAYER 파일 추가 POST 동작
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

    // LAYER 파일 수정 POST 동작
    @PostMapping("update-layer")
    public String updateLayer(@RequestParam("id") long id,
                              @RequestParam("name") String name,
                              @RequestParam("description") String description,
                              @RequestParam(value = "projection", defaultValue = "epsg:4326") String projection,
                              @RequestParam("middle_folder") String middle_folder,
                              @RequestParam("type") String type,
                              @RequestParam("data_file") MultipartFile data_file, LayerSearchModel layerSearchModel) throws IOException {
        boolean result = service.updateLayer(id, name, description, projection, middle_folder, type, data_file);
        session.message(String.format("LAYER [%s] 수정 %s하였습니다.",name,(result ? "성공" : "실패")));
        return "redirect:layer-manage?" + layerSearchModel.getQueryString();
    }

    // LAYER 파일 삭제 REST API
    @ResponseBody
    @PostMapping("del-layer")
    public boolean delLayer(@RequestParam("layerId") Long layerId) {
        return service.delLayer(layerId);
    }
}
