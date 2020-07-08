package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxyLayerCacheRelationDTO {
    private Long id;
    private Long layerId;
    private Long cacheId;

    public ProxyLayerCacheRelationDTO() {

    }

    public ProxyLayerCacheRelationDTO(Long id, Long layerId, Long cacheId) {
        this.id = id;
        this.layerId = layerId;
        this.cacheId = cacheId;
    }
}
