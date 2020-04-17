package com.jiin.admin.website.security;

import com.jiin.admin.dto.AccountDTO;
import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.entity.RoleEntity;
import com.jiin.admin.website.model.AccountModel;
import com.jiin.admin.website.model.AccountModelBuilder;
import com.jiin.admin.website.model.RoleModel;
import com.jiin.admin.website.server.mapper.CheckMapper;
import com.jiin.admin.website.util.EncryptUtil;
import com.jiin.admin.website.view.mapper.AccountMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
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
        return accountMapper.findAllAccounts();
    }

    @Override
    public List<RoleEntity> findAllRoles() {
        return accountMapper.findAllRoles();
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
    public boolean createRoleWithModel(RoleModel roleModel) {
        if(accountMapper.findRoleByTitle(roleModel.getTitle()) != null) return false;
        else {
            accountMapper.insertRole(new RoleEntity(0L, roleModel.getTitle(), roleModel.getLabel(), roleModel.isMapBasic(), roleModel.isMapManage(), roleModel.isLayerManage(), roleModel.isCacheManage(), roleModel.isAccountManage()));
            return true;
        }
    }

    @Override
    public boolean updateAccountWithModel(AccountModel accountModel) {
        String username = accountModel.getUsername();
        String passwd1 = accountModel.getPassword1();
        String passwd2 = accountModel.getPassword2();

        if(passwd1.equals(passwd2) && checkMapper.countDuplicateAccount(username) >= 1) {
            // 여기서는 Role 값을 전혀 변경하지 않는다.
            accountMapper.updateAccount(new AccountDTO(null, username, EncryptUtil.encrypt(passwd1, EncryptUtil.SHA256), accountModel.getName(), accountModel.getEmail(), null));
            return true;
        } else return false;
    }

    @Override
    public boolean updateAccountRole(String username, long roleId) {
        if(checkMapper.countDuplicateAccount(username) >= 1){
            accountMapper.updateAccountRoleWithUsernameAndRoleId(username, roleId);
            return true;
        } else return false;
    }

    @Override
    public boolean updateRoleWithModel(RoleModel roleModel) {
        RoleEntity role = accountMapper.findRoleByTitle(roleModel.getTitle());
        if(role != null){
            accountMapper.updateRole(new RoleEntity(role.getId(), roleModel.getTitle(), roleModel.getLabel(), roleModel.isMapBasic(), roleModel.isMapManage(), roleModel.isLayerManage(), roleModel.isCacheManage(), roleModel.isAccountManage()));
            return true;
        } else return false;
    }

    @Override
    public boolean deleteAccountWithUsername(String loginId) {
        if(checkMapper.countDuplicateAccount(loginId) > 0){
            accountMapper.deleteAccountByUsername(loginId);
            return true;
        } else return false;
    }

    @Override
    @Transactional
    public boolean deleteRoleById(long id) {
        if(accountMapper.findRoleById(id) != null){
            // 권한 삭제 시, 기본 권한인 USER 로 격하 시킴.
            RoleEntity userRole = accountMapper.findRoleByTitle("USER");
            List<AccountEntity> accounts = accountMapper.findAccountByRoleId(id);
            accounts.stream().forEach(o -> accountMapper.updateAccountRoleWithUsernameAndRoleId(o.getUsername(), userRole.getId()));
            accountMapper.deleteRoleById(id);
            return true;
        } else return false;
    }
}
