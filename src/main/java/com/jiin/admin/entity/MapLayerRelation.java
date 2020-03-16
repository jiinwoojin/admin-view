package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "_MAP_LAYER_RELATION")
@IdClass(MapLayerRelationId.class)
@Getter
@Setter
public class MapLayerRelation {
    @Id
    @OneToOne
    @JoinColumn(name = "MAP_ID", nullable = false)
    private Map map;

    @Id
    @OneToOne
    @JoinColumn(name = "LAYER_ID", nullable = false)
    private Layer layer;

    /**
     * LAYER 순서
     */
    @Column(name = "LAYER_ORDER", nullable = false)
    private Integer layerOrder;
}

@Getter
@Setter
class MapLayerRelationId implements Serializable {
    private Long map;

    private Long layer;
}