package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.dto.MapDTO;
import com.jiin.admin.website.model.MapPageModel;
import com.jiin.admin.website.view.service.LayerService;
import com.jiin.admin.website.view.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("manage")
public class MVCMapController {
    @Autowired
    private MapService mapService;

    @Autowired
    private LayerService layerService;

    @Resource
    private SessionService session;

    /**
     * ABC.map 데이터 목록 조회
     * @param model Model, mapPageModel MapPageModel
     */
    @RequestMapping("map-list")
    public String pageMapList(Model model, MapPageModel mapPageModel){
        model.addAttribute("resMap", mapService.loadDataListAndCountByPaginationModel(mapPageModel));
        model.addAttribute("obList", mapService.loadOrderByOptionList());
        model.addAttribute("sbList", mapService.loadSearchByOptionList());
        model.addAttribute("message", session.message());
        model.addAttribute("qs", mapPageModel.getQueryString());
        return "page/manage/refactoring/map-list";
    }

    /**
     * ABC.map 데이터 생성 - Page
     * @param model Model, mapDTO MapDTO, mapPageModel MapPageModel
     */
    @RequestMapping("map-create")
    public String pageMapCreate(Model model, @ModelAttribute MapDTO mapDTO, MapPageModel mapPageModel){
        model.addAttribute("layers", layerService.loadDataList());
        model.addAttribute("qs", mapPageModel.getQueryString());

        return "page/manage/refactoring/map-create";
    }

    /**
     * ABC.map 데이터 생성 - POST
     * @param mapDTO MapDTO, relations String JSON
     */
    @PostMapping("map-create")
    public String postMapCreate(@Valid MapDTO mapDTO, @RequestParam("layerList") String relations) throws IOException {
        boolean result = mapService.createData(mapDTO, relations);
        session.message(String.format("MAP [%s] 추가 %s 하였습니다.", mapDTO.getName(), (result ? "성공" : "실패")));
        return "redirect:map-list?pg=1&sz=8&iType=ALL&units=ALL";
    }

    /**
     * ABC.map 데이터 수정 - Page
     * @param model Model, id long, mapDTO MapDTO, mapPageModel MapPageModel
     */
    @RequestMapping("map-update")
    public String pageMapUpdate(Model model, @RequestParam long id, MapPageModel mapPageModel){
        model.addAttribute("mapDTO", mapService.loadDataById(id));
        model.addAttribute("selectLayers", layerService.loadDataListByMapId(id));
        model.addAttribute("layers", layerService.loadDataList());
        model.addAttribute("qs", mapPageModel.getQueryString());

        return "page/manage/refactoring/map-update";
    }

    /**
     * ABC.map 데이터 수정 - POST
     * @param mapDTO MapDTO, relations String JSON
     */
    @PostMapping("map-update")
    public String postMapUpdate(@Valid MapDTO mapDTO, @RequestParam("layerList") String relations) throws IOException {
        boolean result = mapService.setData(mapDTO, relations);
        session.message(String.format("MAP [%s] 수정 %s 하였습니다.", mapDTO.getName(), (result ? "성공" : "실패")));
        return "redirect:map-list?pg=1&sz=8&iType=ALL&units=ALL";
    }

    /**
     * ABC.map 데이터 삭제 - POST
     * @param mapId long
     */
    @ResponseBody
    @PostMapping("map-delete")
    public boolean restMapDelete(@RequestParam Long mapId) throws IOException {
        return mapService.removeData(mapId);
    }
}
