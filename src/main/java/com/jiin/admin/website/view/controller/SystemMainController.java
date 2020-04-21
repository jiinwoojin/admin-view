package com.jiin.admin.website.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("server")
public class SystemMainController {
    @RequestMapping("service-shutdown")
    public String serviceShutdownPage(Model model){
        return "page/system/service-shutdown";
    }

    @RequestMapping("service-restart")
    public String serviceRestartPage(Model model){
        return "page/system/service-restart";
    }

    @RequestMapping("service-info")
    public String serviceInfoPage(Model model){
        return "page/system/service-info";
    }
}
