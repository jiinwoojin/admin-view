package com.jiin.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "_PROXY_LAYER_CACHE_RELATION")
@SequenceGenerator(
        name = "PROXY_LAYER_CACHE_SEQ_GEN",
        sequenceName = "PROXY_LAYER_CACHE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = { "LAYER_ID", "CACHE_ID" })
})
@Getter
@Setter
public class ProxyLayerCacheRelationEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PROXY_LAYER_CACHE_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    private ProxyLayerEntity layer;

    @ManyToOne
    @JoinColumn
    private ProxyCacheEntity cache;

    public ProxyLayerCacheRelationEntity() {

    }

    public ProxyLayerCacheRelationEntity(long id, ProxyLayerEntity layer, ProxyCacheEntity cache) {
        this.id = id;
        this.layer = layer;
        this.cache = cache;
    }
}
