package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.model.LayerSearchModel;
import com.jiin.admin.website.model.OptionModel;
import com.jiin.admin.website.server.service.TegolaService;
import com.jiin.admin.website.view.service.ManageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/layer")
public class LayerRestController {

    @Resource
    private TegolaService tgservice;

    @Resource
    private ManageService manageService;

    @PostMapping("load-tegola-config")
    public Map loadTegolaConfig(){
        return tgservice.loadTegolaConfig();
    }

    @GetMapping("list")
    public Map<String, Object> layerList(LayerSearchModel pagination) throws ParseException {
        return manageService.getLayerListByPaginationModel(pagination);
    }

    @GetMapping("options/ob")
    public List<OptionModel> orderByOptions(){
        return manageService.orderByOptions();
    }

    @GetMapping("options/sb")
    public List<OptionModel> searchByOptions(){
        return manageService.searchByOptions();
    }
}
