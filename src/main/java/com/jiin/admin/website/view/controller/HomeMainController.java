package com.jiin.admin.website.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("home")
public class HomeMainController {
    @RequestMapping("guest")
    public String homeMainPageForGuest(Model model){
        return "page/home/guest";
    }

    @RequestMapping("user")
    public String homeMainPageForUser(Model model){
        return "page/home/user";
    }
}
