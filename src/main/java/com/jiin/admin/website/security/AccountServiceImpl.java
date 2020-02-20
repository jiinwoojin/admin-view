package com.jiin.admin.website.security;

import com.jiin.admin.website.dto.Account;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AccountServiceImpl implements UserDetailsService, AccountService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        switch(username){
            case "admin" :
                return AccountSampleData.ADMIN;
            case "dev" :
                return AccountSampleData.DEV;
            case "user" :
                return AccountSampleData.USER;
            default :
                return null;
        }
    }

    @Override
    public Account findByUsername(String username) {
        switch(username){
            case "admin" :
                return AccountSampleData.ADMIN;
            case "dev" :
                return AccountSampleData.DEV;
            case "user" :
                return AccountSampleData.USER;
            default :
                return null;
        }
    }

    @Override
    public List<Account> findAllAccounts() {
        return Arrays.asList(AccountSampleData.ADMIN, AccountSampleData.DEV, AccountSampleData.USER);
    }
}
