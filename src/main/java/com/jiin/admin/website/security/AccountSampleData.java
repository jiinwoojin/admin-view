package com.jiin.admin.website.security;

import com.jiin.admin.entity.AccountEntity;

public interface AccountSampleData {
    AccountEntity USER = new AccountEntity("user", "user",  "USER", "user@ji-in.com", "USER");
    AccountEntity DEV = new AccountEntity("dev", "dev", "DEVELOPER", "dev@ji-in.com", "USER");
    AccountEntity ADMIN = new AccountEntity("admin", "admin", "ADMINISTRATOR", "admin@ji-in.com", "ADMIN");
}
