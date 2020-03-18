package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "_MAP_LAYER_RELATION")
@Getter
@Setter
public class MapLayerRelation implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn
    private Map map;

    @Id
    @ManyToOne
    @JoinColumn
    private Layer layer;

    /**
     * LAYER 순서
     */
    @Column(name = "LAYER_ORDER", nullable = false)
    private Integer layerOrder;
}