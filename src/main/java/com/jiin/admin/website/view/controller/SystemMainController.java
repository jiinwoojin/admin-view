package com.jiin.admin.website.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("server")
public class SystemMainController {
    @RequestMapping("service-info")
    public String serviceInfoPage(Model model){
        return "page/system/service-info";
    }

    @RequestMapping("service-manage")
    public String serviceManagePage(Model model){
        return "page/system/service-manage";
    }
}
