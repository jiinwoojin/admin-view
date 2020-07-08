package com.jiin.admin.website.server.controller;

import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.website.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/account")
public class AccountRestController {
    @Autowired
    private AccountService accountService;

    @GetMapping("list")
    public List<AccountEntity> accountListJSON() {
        return accountService.findAllAccounts();
    }
}
