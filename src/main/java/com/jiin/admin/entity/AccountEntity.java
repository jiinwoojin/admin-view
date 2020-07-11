package com.jiin.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

@Entity(name = "_ACCOUNT")
@SequenceGenerator(
        name = "ACCOUNT_SEQ_GEN",
        sequenceName = "ACCOUNT_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class AccountEntity implements Serializable, UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ACCOUNT_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    @Column(name = "USERNAME", length = 20, nullable = false, unique = true)
    private String username;

    @Column(name = "NAME", length = 15, nullable = false)
    private String name;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn
    private RoleEntity role;

    @JsonIgnore
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    public AccountEntity() {

    }

    public AccountEntity(Long id, String username, String password, String name, String email, RoleEntity role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(String.format("ROLE_%s", role.getTitle())));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
