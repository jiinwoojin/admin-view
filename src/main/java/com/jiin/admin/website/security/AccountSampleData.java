package com.jiin.admin.website.security;

import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.entity.RoleEntity;

public interface AccountSampleData {
    AccountEntity USER = new AccountEntity(1L, "user", "user",  "USER", "user@ji-in.com", new RoleEntity(0L, "USER", "사용자", false, false, false, false, false));
    AccountEntity DEV = new AccountEntity(2L, "dev", "dev", "DEVELOPER", "dev@ji-in.com", new RoleEntity(0L, "USER", "사용자", false, false, false, false, false));
    AccountEntity ADMIN = new AccountEntity(3L, "admin", "admin", "ADMINISTRATOR", "admin@ji-in.com", new RoleEntity(0L, "ADMIN", "관리자", true, true, true, true, true));
}
