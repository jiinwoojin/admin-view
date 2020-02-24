package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("account")
public class AccountMainController {
    @Autowired
    private AccountService accountService;

    @RequestMapping("list")
    public String accountListViewPage(Model model){
        // 1차적인 모든 회원 정보 조회 : 서버 사이드 AJAX 미적용. 향후 적용 예정.
        model.addAttribute("accounts", accountService.findAllAccounts());
        return "page/account/list";
    }
}
