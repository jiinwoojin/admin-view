package com.jiin.admin.website.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("auth")
public class AuthMainController {
    // 회원 로그인 페이지
    @RequestMapping("login")
    public String authLoginPage(Model model){
        return "page/auth/login";
    }

    // 회원 가입 페이지
    @RequestMapping("sign")
    public String authSignUpPage(Model model){
        return "";
    }


}
