package com.jiin.admin.website.security;

import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.website.util.EncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AccountAuthProvider implements AuthenticationProvider {
    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private AccountService accountService;

    private List<GrantedAuthority> makeAuthority(String role) {
        return Arrays.asList(new SimpleGrantedAuthority(String.format("%s%s", ROLE_PREFIX, role)));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        return authenticate(username, password);
    }

    public Authentication authenticate(String username, String password) {
        AccountEntity account = accountService.findByUsername(username);
        String passwd = EncryptUtil.encrypt(password, EncryptUtil.SHA256);
        if (account == null) return null;
        if (!account.getPassword().equals(passwd)) return null;
        return new UsernamePasswordAuthenticationToken(account, null, makeAuthority(account.getRole() != null ? account.getRole().getTitle() : "USER"));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
