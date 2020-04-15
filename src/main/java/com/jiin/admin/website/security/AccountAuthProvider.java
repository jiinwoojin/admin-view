package com.jiin.admin.website.security;

import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.website.util.EncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AccountAuthProvider implements AuthenticationProvider {
    @Autowired
    private AccountService accountService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        return authenticate(username, password);
    }

    public Authentication authenticate(String username, String password){
        AccountEntity account = accountService.findByUsername(username);
        String passwd = EncryptUtil.encrypt(password, EncryptUtil.SHA256);
        if(account == null) return null;
        if(!account.getPassword().equals(passwd)) return null;
        return new AccountAuthentication(username, passwd, account.getAuthorities(), account);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public class AccountAuthentication extends UsernamePasswordAuthenticationToken {
        private static final long serialVersionUID = 1L;
        private AccountEntity account;

        public AccountAuthentication(String username, String password, Collection<? extends GrantedAuthority> collection, AccountEntity account){
            super(username, password, collection);
            this.account = account;
        }

        public AccountEntity getAccount(){
            return this.account;
        }

        public void setAccount(AccountEntity account){
            this.account = account;
        }
    }
}
