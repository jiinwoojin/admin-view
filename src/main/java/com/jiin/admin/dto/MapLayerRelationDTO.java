package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapLayerRelationDTO {
    private Long id;
    private Long mapId;
    private Long layerId;
    private Integer layerOrder;

    public MapLayerRelationDTO() {

    }

    public MapLayerRelationDTO(Long id, Long mapId, Long layerId, Integer layerOrder) {
        this.id = id;
        this.mapId = mapId;
        this.layerId = layerId;
        this.layerOrder = layerOrder;
    }
}
