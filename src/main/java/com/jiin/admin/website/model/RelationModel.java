package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationModel {
    private long id;
    private long mainId;
    private long subId;

    public RelationModel(){

    }

    public RelationModel(long id, long mainId, long subId){
        this.id = id;
        this.mainId = mainId;
        this.subId = subId;
    }
}
