package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "_MAP_LAYER_RELATION")
@SequenceGenerator(
        name = "MAP_LAYER_SEQ_GEN",
        sequenceName = "MAP_LAYER_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(uniqueConstraints={
        @UniqueConstraint(columnNames = { "MAP_ID", "LAYER_ID" })
})
@Getter
@Setter
public class MapLayerRelationEntity implements Serializable{
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "MAP_LAYER_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn
    private MapEntity map;

    @ManyToOne
    @JoinColumn
    private LayerEntity layer;

    /**
     * LAYER 순서
     */
    @Column(name = "LAYER_ORDER", nullable = false)
    private Integer layerOrder;
}