package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.website.model.LayerPageModel;
import com.jiin.admin.website.view.service.LayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Controller
@RequestMapping("manage")
public class MVCLayerController {
    @Autowired
    private LayerService layerService;

    @Resource
    private SessionService session;

    /**
     * ABC.lay 데이터 목록 조회
     * @param model Model, layerPageModel LayerPageModel
     */
    @RequestMapping("layer-list")
    private String pageLayerList(Model model, LayerPageModel layerPageModel){
        model.addAttribute("resMap", layerService.loadDataListAndCountByPaginationModel(layerPageModel));
        model.addAttribute("obList", layerService.loadOrderByOptionList());
        model.addAttribute("sbList", layerService.loadSearchByOptionList());
        model.addAttribute("message", session.message());
        return "page/manage/layer-list";
    }

    /**
     * ABC.lay 데이터 추가
     * @Param layerPageModel LayerPageModel, dataFile MultipartFile, layerPageModel LayerPageModel
     */
    @PostMapping("layer-create")
    public String postLayerCreate(LayerDTO layerDTO, @RequestParam("data_file") MultipartFile dataFile, LayerPageModel layerPageModel){
        boolean result = layerService.createData(layerDTO, dataFile);
        session.message(String.format("LAYER [%s] 추가 %s하였습니다.", layerDTO.getName(), (result ? "성공" : "실패")));
        return String.format("redirect:layer-list?%s", layerPageModel.getQueryString());
    }

    /**
     * ABC.lay 데이터 수정
     * @Param layerPage
     */
    @PostMapping("layer-update")
    public String postLayerUpdate(LayerDTO layerDTO, @RequestParam("data_file") MultipartFile dataFile, LayerPageModel layerPageModel){
        boolean result = layerService.setData(layerDTO, dataFile);
        session.message(String.format("LAYER [%s] 수정 %s하였습니다.", layerDTO.getName(), (result ? "성공" : "실패")));
        return String.format("redirect:layer-list?%s", layerPageModel.getQueryString());
    }

    /**
     * ABC.lay 데이터 삭제
     * @Param layerPage
     */
    @ResponseBody
    @PostMapping("layer-delete")
    public boolean restLayerDelete(@RequestParam long layerId){
        return layerService.removeData(layerId);
    }
}
