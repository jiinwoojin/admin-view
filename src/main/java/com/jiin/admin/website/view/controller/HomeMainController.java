package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.gis.StatusService;
import com.jiin.admin.website.security.AccountService;
import com.jiin.admin.website.view.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("home")
public class HomeMainController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private DashboardService dashboardService;

    @RequestMapping(value = { "guest", "user" })
    public String homeMainPageForGuest(Model model){
        model.addAttribute("serverRelation", dashboardService.getConnectRelations());
        model.addAttribute("performanceMap", dashboardService.getEachFirstConnectionsMap());
        model.addAttribute("status_synchronize", statusService.centerSynchronizeCheck());
        model.addAttribute("counter", dashboardService.getDataCounter());
        return "page/home/dashboard";
    }
}
