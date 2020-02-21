package com.jiin.admin.website.security;

import com.jiin.admin.website.dto.Account;

public interface AccountSampleData {
    Account USER = new Account("user", "user",  "USER", "user@ji-in.com", "USER");
    Account DEV = new Account("dev", "dev", "DEVELOPER", "dev@ji-in.com", "USER");
    Account ADMIN = new Account("admin", "admin", "ADMINISTRATOR", "admin@ji-in.com", "ADMIN");
}
