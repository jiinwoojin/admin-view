package com.jiin.admin.website.model;

public class AccountModelBuilder {
    private String username;
    private String password1;
    private String password2;
    private String name;
    private String email;

    public AccountModelBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public AccountModelBuilder setPassword1(String password1) {
        this.password1 = password1;
        return this;
    }

    public AccountModelBuilder setPassword2(String password2) {
        this.password2 = password2;
        return this;
    }

    public AccountModelBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public AccountModelBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public AccountModel build() {
        return new AccountModel(this.username, this.password1, this.password2, this.name, this.email);
    }

    public static AccountModelBuilder builder() {
        return new AccountModelBuilder();
    }
}
