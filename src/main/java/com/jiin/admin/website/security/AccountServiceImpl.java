package com.jiin.admin.website.security;

import com.jiin.admin.dto.AccountDTO;
import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.entity.RoleEntity;
import com.jiin.admin.website.model.AccountModel;
import com.jiin.admin.website.model.AccountModelBuilder;
import com.jiin.admin.website.server.mapper.CheckMapper;
import com.jiin.admin.website.util.EncryptUtil;
import com.jiin.admin.website.view.mapper.AccountMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountServiceImpl implements UserDetailsService, AccountService {
    @Resource
    private CheckMapper checkMapper;

    @Resource
    private AccountMapper accountMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountMapper.findAccountByUsername(username);
    }

    @Override
    public AccountEntity findByUsername(String username) {
        return accountMapper.findAccountByUsername(username);
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
        findAllAccounts().forEach(a -> map.put(a.getRole().getTitle(), map.getOrDefault(a.getRole().getTitle(), 0L) + 1L));
        return map;
    }

    @Override
    public boolean createAccountWithModel(AccountModel accountModel) {
        String username = accountModel.getUsername();
        String passwd1 = accountModel.getPassword1();
        String passwd2 = accountModel.getPassword2();
        if(passwd1.equals(passwd2) && checkMapper.countDuplicateAccount(username) < 1){
            RoleEntity role = accountMapper.findRoleByTitle("USER");
            accountMapper.insertAccount(new AccountDTO(null, username, EncryptUtil.encrypt(passwd1, EncryptUtil.SHA256), accountModel.getName(), accountModel.getEmail(), role.getId()));
            return true;
        } else return false;
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
