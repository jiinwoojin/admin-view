package com.jiin.admin.website.security;

import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.entity.RoleEntity;
import com.jiin.admin.website.model.AccountModel;
import com.jiin.admin.website.model.RoleModel;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

public interface AccountService {
    UserDetails loadUserByUsername(String username);
    AccountEntity findByUsername(String username);
    List<AccountEntity> findAllAccounts();
    List<RoleEntity> findAllRoles();
    AccountModel createModelWithAuthentication(AccountAuthProvider.AccountAuthentication auth);
    Map<String, Long> countWithAccountType();
    boolean createAccountWithModel(AccountModel accountModel);
    boolean createRoleWithModel(RoleModel roleModel);
    boolean updateAccountWithModel(AccountModel accountModel);
    boolean updateAccountRole(String username, long roleId);
    boolean deleteAccountWithUsername(String loginId);
    boolean deleteRoleById(long id);
}
