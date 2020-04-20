package com.jiin.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "_PROXY_LAYER_SOURCE_RELATION")
@SequenceGenerator(
        name = "PROXY_LAYER_SOURCE_SEQ_GEN",
        sequenceName = "PROXY_LAYER_SOURCE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = { "LAYER_ID", "SOURCE_ID" })
})
@Getter
@Setter
public class ProxyLayerSourceRelationEntity implements Serializable {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PROXY_LAYER_SOURCE_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn
    private ProxyLayerEntity layer;

    @ManyToOne
    @JoinColumn
    private ProxySourceEntity source;

    public ProxyLayerSourceRelationEntity(){

    }

    public ProxyLayerSourceRelationEntity(long id, ProxyLayerEntity layer, ProxySourceEntity source) {
        this.id = id;
        this.layer = layer;
        this.source = source;
    }
}
