package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.view.service.DataCountService;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("home")
public class MVCHomeController {
    @Autowired
    private ServerCenterInfoService serverCenterInfoService;

    @Autowired
    private DataCountService dataCountService;

    @RequestMapping(value = { "guest", "user" })
    public String homeMainPageForGuest(Model model){
        model.addAttribute("counter", dataCountService.loadDataCounter());
        model.addAttribute("serverMap", serverCenterInfoService.loadDataMapZoneBase());
        model.addAttribute("local", serverCenterInfoService.loadLocalInfoData());
        model.addAttribute("zones", serverCenterInfoService.loadZoneList());
        return "page/home/dashboard";
    }
}
