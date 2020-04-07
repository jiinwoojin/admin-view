package com.jiin.admin.website.security;

import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.website.model.AccountModel;
import com.jiin.admin.website.model.AccountModelBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public AccountEntity findByUsername(String username) {
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
    public List<AccountEntity> findAllAccounts() {
        return Arrays.asList(AccountSampleData.ADMIN, AccountSampleData.DEV, AccountSampleData.USER);
    }

    @Override
    public AccountModel createModelWithAuthentication(AccountAuthProvider.AccountAuthentication auth) {
        AccountModelBuilder builder = AccountModelBuilder.builder();
        if(auth == null) return builder.build();
        AccountEntity account = auth.getAccount();
        return account != null ? builder
                                    .setUsername(account.getUsername())
                                    .setPassword1("")
                                    .setPassword2("")
                                    .setName(account.getName())
                                    .setEmail(account.getEmail())
                                    .build() : builder.build();
    }

    @Override
    public Map<String, Long> countWithAccountType() {
        Map<String, Long> map = new HashMap<>();
        findAllAccounts().forEach(a -> map.put(a.getRole(), map.getOrDefault(a.getRole(), 0L) + 1L));
        return map;
    }

    @Override
    public AccountEntity createAccountWithModel(AccountModel accountModel) {
        return null;
    }

    @Override
    public AccountEntity updateAccountWithModel(AccountModel accountModel) {
        return null;
    }

    @Override
    public AccountEntity deleteAccountWithModel(String loginId) {
        return null;
    }
}
