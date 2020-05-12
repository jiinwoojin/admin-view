package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.view.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("server")
public class SystemMainController {
    @Autowired
    private DashboardService dashboardService;

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
        model.addAttribute("connections", dashboardService.getOwnRelateConnectionsList());
        return "page/system/service-address";
    }
}
