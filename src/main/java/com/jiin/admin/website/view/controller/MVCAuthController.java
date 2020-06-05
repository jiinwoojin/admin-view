package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.model.AccountModel;
import com.jiin.admin.website.security.AccountAuthProvider;
import com.jiin.admin.website.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("auth")
public class MVCAuthController {
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
        model.addAttribute("account", new AccountModel());
        return "page/auth/sign";
    }

    // 회원 가입 실행 Request
    @RequestMapping(value = "sign", method = RequestMethod.POST)
    public String authSignUpRedirect(Model model, AccountModel accountModel){
        if(accountService.createAccountWithModel(accountModel)) {
            return "redirect:login";
        } else {
            return "redirect:sign?error";
        }
    }

    // 회원 정보 수정 페이지
    @RequestMapping("edit")
    public String authEditPage(Model model, AccountAuthProvider.AccountAuthentication auth) {
        model.addAttribute("account", accountService.createModelWithAuthentication(auth));
        return "page/auth/edit";
    }

    // 회원 정보 수정 Request
    @RequestMapping(value = "edit", method = RequestMethod.POST)
    public String authEditRedirect(Model model, AccountModel accountModel){
        if(accountService.updateAccountWithModel(accountModel)) {
            return "redirect:../home/user";
        } else {
            return "redirect:edit?error";
        }
    }
}
