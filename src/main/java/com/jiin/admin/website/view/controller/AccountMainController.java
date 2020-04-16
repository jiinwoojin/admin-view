package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("account")
public class AccountMainController {
    @Autowired
    private AccountService accountService;

    @RequestMapping("list")
    public String accountListViewPage(Model model){
        // 모든 회원 정보 조회 : 서버 사이드 AJAX 미적용. 향후 개발 시, Pagination 기반으로 쿼리 속도 향상 필요 여부 확인.
        model.addAttribute("accounts", accountService.findAllAccounts());
        model.addAttribute("roles", accountService.findAllRoles());
        return "page/account/list";
    }

    @RequestMapping("change-role/{username}/{roleId}")
    public String accountRoleChangeLink(Model model, @PathVariable String username, @PathVariable long roleId){
        if(accountService.updateAccountRole(username, roleId))
            return "redirect:../../list";
        else
            return "redirect:../../list?error";
    }

    @RequestMapping("delete-account/{username}")
    public String authDeleteLink(Model model, @PathVariable String username){
        if(accountService.deleteAccountWithUsername(username)) {
            return "redirect:../list";
        } else return "redirect:../list?error";
    }
}
