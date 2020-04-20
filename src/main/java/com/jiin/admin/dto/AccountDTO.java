package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private Long roleId;

    public AccountDTO(){

    }

    public AccountDTO(Long id, String username, String password, String name, String email, Long roleId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.roleId = roleId;
    }
}