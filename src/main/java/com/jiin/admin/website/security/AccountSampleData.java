package com.jiin.admin.website.security;

import com.jiin.admin.website.dto.Account;

public interface AccountSampleData {
    Account USER = new Account("user", "user",  "user", "user@ji-in.com", "USER");
    Account DEV = new Account("dev", "dev", "dev", "dev@ji-in.com", "USER");
    Account ADMIN = new Account("admin", "admin", "admin", "admin@ji-in.com", "ADMIN");
}
