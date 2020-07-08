package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "_PROXY_LAYER")
@SequenceGenerator(
        name = "PROXY_LAYER_SEQ_GEN",
        sequenceName = "PROXY_LAYER_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
public class ProxyLayerEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PROXY_LAYER_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    /**
     * Layer 이름
     */
    @Column(name = "NAME", length = 20, nullable = false, unique = true)
    private String name;

    /**
     * Layer 제목
     */
    @Column(name = "TITLE", nullable = false)
    private String title;

    @OneToMany(mappedBy = "layer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProxyLayerSourceRelationEntity> sources;

    @OneToMany(mappedBy = "layer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProxyLayerCacheRelationEntity> caches;

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

    public ProxyLayerEntity() {
        this.sources = new ArrayList<>();
    }

    @Override
    public boolean isNew() {
        return false;
    }
}
