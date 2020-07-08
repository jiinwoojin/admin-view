package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountModel {
    private String username;
    private String password1;
    private String password2;
    private String name;
    private String email;

    public AccountModel() {

    }

    public AccountModel(String username, String password1, String password2, String name, String email) {
        this.username = username;
        this.password1 = password1;
        this.password2 = password2;
        this.name = name;
        this.email = email;
    }
}
