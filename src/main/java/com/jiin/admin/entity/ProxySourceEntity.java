package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Entity(name = "_PROXY_SOURCE")
@SequenceGenerator(
        name = "PROXY_SOURCE_SEQ_GEN",
        sequenceName = "PROXY_SOURCE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
@Getter
@Setter
public abstract class ProxySourceEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PROXY_SOURCE_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    /**
     * Source 이름
     */
    @Column(name = "NAME", length = 20, nullable = false, unique = true)
    private String name;

    /**
     * SELECTED BOOL
     */
    @Column(name = "SELECTED")
    private Boolean selected;

    /**
     * Is Default BOOL
     */
    @Column(name = "IS_DEFAULT")
    private Boolean isDefault;

    public ProxySourceEntity(){

    }

    @Override
    public boolean isNew() {
        return false;
    }
}
