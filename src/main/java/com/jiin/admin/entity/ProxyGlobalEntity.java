package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "_PROXY_GLOBAL")
@SequenceGenerator(
        name = "PROXY_GLOBAL_SEQ_GEN",
        sequenceName = "PROXY_GLOBAL_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
public class ProxyGlobalEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PROXY_GLOBAL_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    /**
     * GLOBAL Property Key (like Java Path Key -> abc.bcd.cde)
     */
    @Column(name = "KEY", nullable = false, unique = true)
    private String key;

    /**
     * GLOBAL Property Value (JSON Value)
     */
    @Column(name = "VALUE", length = 65535, nullable = false)
    private String value;

}
