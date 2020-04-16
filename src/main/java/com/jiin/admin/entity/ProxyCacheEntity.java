package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "_PROXY_CACHE")
@SequenceGenerator(
        name = "PROXY_CACHE_SEQ_GEN",
        sequenceName = "PROXY_CACHE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
public class ProxyCacheEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PROXY_CACHE_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    /**
     * Cache 이름
     */
    @Column(name = "NAME", length = 20, nullable = false, unique = true)
    private String name;

    /**
     * META SIZE X Side
     */
    @Column(name = "META_SIZE_X")
    private Integer metaSizeX;

    /**
     * META SIZE Y Side
     */
    @Column(name = "META_SIZE_Y")
    private Integer metaSizeY;

    /**
     * META BUFFER
     */
    @Column(name = "META_BUFFER")
    private Integer metaBuffer;

    @OneToMany(mappedBy = "cache", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProxyCacheSourceRelationEntity> sources;

    /**
     * CACHE TYPE
     */
    @Column(name = "CACHE_TYPE", length = 20, nullable = false)
    private String cacheType;

    /**
     * CACHE DIRECTORY
     */
    @Column(name = "CACHE_DIRECTORY", length = 20, nullable = false)
    private String cacheDirectory;

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

    public ProxyCacheEntity(){
        this.sources = new ArrayList<>();
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
