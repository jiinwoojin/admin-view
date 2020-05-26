package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerRelationDTO {
    private Long id;
    private Long mainSvrId;
    private Long subSvrId;

    public ServerRelationDTO(){

    }

    public ServerRelationDTO(Long id, Long mainSvrId, Long subSvrId) {
        this.id = id;
        this.mainSvrId = mainSvrId;
        this.subSvrId = subSvrId;
    }
}
