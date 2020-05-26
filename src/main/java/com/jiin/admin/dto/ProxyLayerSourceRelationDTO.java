package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxyLayerSourceRelationDTO {
    private Long id;
    private Long layerId;
    private Long sourceId;

    public ProxyLayerSourceRelationDTO(){

    }

    public ProxyLayerSourceRelationDTO(Long id, Long layerId, Long sourceId) {
        this.id = id;
        this.layerId = layerId;
        this.sourceId = sourceId;
    }
}
