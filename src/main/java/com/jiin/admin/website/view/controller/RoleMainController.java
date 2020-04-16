package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("role")
public class RoleMainController {
    @Autowired
    private AccountService accountService;

    @RequestMapping("list")
    public String roleListView(Model model){
        return "page/role/list";
    }
}
