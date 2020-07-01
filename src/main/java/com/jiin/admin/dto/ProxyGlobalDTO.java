package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxyGlobalDTO {
    private Long id;
    private String key;
    private String value;

    public ProxyGlobalDTO(){

    }

    public ProxyGlobalDTO(Long id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }
}
