package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.view.service.ServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("server")
public class SystemMainController {
    @Autowired
    private ServerInfoService serverInfoService;

    @RequestMapping("service-info")
    public String serviceInfoPage(Model model){
        return "page/system/service-info";
    }

    @RequestMapping("service-manage")
    public String serviceManagePage(Model model){
        return "page/system/service-manage";
    }

    @RequestMapping("service-address")
    public String addressConfigPage(Model model){
        model.addAttribute("connections", serverInfoService.getOwnRelateConnectionsList());
        model.addAttribute("ports", serverInfoService.getServicePortInfo());
        return "page/system/service-address";
    }
}
