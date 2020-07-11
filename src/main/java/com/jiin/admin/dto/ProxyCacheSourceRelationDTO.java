package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxyCacheSourceRelationDTO {
    private Long id;
    private Long cacheId;
    private Long sourceId;

    public ProxyCacheSourceRelationDTO() {

    }

    public ProxyCacheSourceRelationDTO(Long id, Long cacheId, Long sourceId) {
        this.id = id;
        this.cacheId = cacheId;
        this.sourceId = sourceId;
    }
}
