package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerRelationModel {
    private Long id;
    private Long mainSvrId;
    private Long subSvrId;

    public ServerRelationModel(){

    }

    public ServerRelationModel(Long id, Long mainSvrId, Long subSvrId) {
        this.id = id;
        this.mainSvrId = mainSvrId;
        this.subSvrId = subSvrId;
    }
}
