package com.jiin.admin.website.security;

import com.jiin.admin.website.dto.Account;
import com.jiin.admin.website.model.AccountModel;
import com.jiin.admin.website.model.AccountModelBuilder;
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
        // 임시 로직만 작성. 내용 변동 가능성 有.
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

    @Override
    public AccountModel createModelWithAuthentication(AccountAuthProvider.AccountAuthentication auth) {
        AccountModelBuilder builder = AccountModelBuilder.builder();
        if(auth == null) return builder.build();
        Account account = auth.getAccount();
        return account != null ? builder
                                    .setUsername(account.getUsername())
                                    .setPassword1("")
                                    .setPassword2("")
                                    .setName(account.getName())
                                    .setEmail(account.getEmail())
                                    .build() : builder.build();
    }
}
