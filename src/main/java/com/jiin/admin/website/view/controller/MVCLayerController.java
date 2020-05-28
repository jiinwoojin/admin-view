package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.website.model.LayerPageModel;
import com.jiin.admin.website.view.service.LayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
     * @param model Model, layerPageModel MapPageModel
     */
    @RequestMapping("layer-list")
    private String pageLayerList(Model model, LayerPageModel layerPageModel){
        model.addAttribute("resMap", layerService.loadDataListAndCountByPaginationModel(layerPageModel));
        model.addAttribute("obList", layerService.loadOrderByOptionList());
        model.addAttribute("sbList", layerService.loadSearchByOptionList());
        model.addAttribute("message", session.message());
        return "page/manage/layer-list";
    }
}
