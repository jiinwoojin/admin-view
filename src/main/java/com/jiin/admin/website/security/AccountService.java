package com.jiin.admin.website.security;

import com.jiin.admin.website.dto.Account;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface AccountService {
    UserDetails loadUserByUsername(String username);
    Account findByUsername(String username);
    List<Account> findAllAccounts();
}
