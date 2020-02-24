package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.gis.StatusService;
import com.jiin.admin.website.security.AccountService;
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

    @RequestMapping(value = { "guest", "user" })
    public String homeMainPageForGuest(Model model){
        model.addAttribute("status_server", statusService.centerStatusCheck());
        model.addAttribute("status_synchronize", statusService.centerSynchronizeCheck());
        model.addAttribute("account_counter", accountService.countWithAccountType());
        return "page/home/dashboard";
    }
}
