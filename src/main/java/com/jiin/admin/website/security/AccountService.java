package com.jiin.admin.website.security;

import com.jiin.admin.website.dto.Account;
import com.jiin.admin.website.model.AccountModel;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

public interface AccountService {
    UserDetails loadUserByUsername(String username);
    Account findByUsername(String username);
    List<Account> findAllAccounts();
    AccountModel createModelWithAuthentication(AccountAuthProvider.AccountAuthentication auth);
    Map<String, Long> countWithAccountType();
}
