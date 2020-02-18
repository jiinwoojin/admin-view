package com.jiin.admin.website.view.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Account {
    private String username;
    private String password;
    private String name;
    private String email;
    private String role;

    public Account(){

    }

    public Account(String username, String password, String name, String email, String role){
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}
