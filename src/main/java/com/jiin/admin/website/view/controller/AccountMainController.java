package com.jiin.admin.website.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("account")
public class AccountMainController {
    @RequestMapping("list")
    public String accountListViewPage(Model model){
        return "page/account/list";
    }
}
