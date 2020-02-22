package com.jiin.admin.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class BaseEntity {
    public BaseEntity() { }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 20)
    private String name;


}
