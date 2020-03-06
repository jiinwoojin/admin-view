package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.view.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller
@RequestMapping("manage")
public class ManageController {
    @Resource
    private ManageService service;

    @RequestMapping("source-manage")
    public String source(Model model){
        model.addAttribute("sources", service.getSourceList());
        return "page/manage/source-manage";
    }

    @RequestMapping("layer-manage")
    public String layer(Model model){
        model.addAttribute("layers", service.getLayerList());
        return "page/manage/layer-manage";
    }
}
