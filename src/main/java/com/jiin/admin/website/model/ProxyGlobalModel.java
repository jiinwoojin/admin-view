package com.jiin.admin.website.model;

import com.jiin.admin.dto.ProxyGlobalDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxyGlobalModel {
    private Long id;
    private String key;
    private String value;

    public ProxyGlobalModel(){

    }

    public ProxyGlobalModel(Long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    public static ProxyGlobalDTO convertDTO(ProxyGlobalModel model){
        if(model == null) return null;
        else {
            return new ProxyGlobalDTO(model.getId(), model.getKey(), model.getValue());
        }
    }
}
