package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.security.AccountAuthProvider;
import com.jiin.admin.website.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("auth")
public class AuthMainController {
    @Autowired
    private AccountService accountService;

    // 회원 로그인 페이지
    @RequestMapping("login")
    public String authLoginPage(Model model){
        return "page/auth/login";
    }

    // 회원 가입 페이지
    @RequestMapping("sign")
    public String authSignUpPage(Model model){
        return "page/auth/sign";
    }

    // 회원 정보 수정 페이지
    @RequestMapping("edit")
    public String authEditPage(Model model, AccountAuthProvider.AccountAuthentication auth) {
        //System.out.println(accountService.createModelWithAuthentication(auth).getEmail());
        model.addAttribute("account", accountService.createModelWithAuthentication(auth));
        return "page/auth/edit";
    }
}
