package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxySourceDTO {
    private Long id;
    private String name;
    private String type;
    private Boolean selected;
    private Boolean isDefault;

    public ProxySourceDTO() {

    }

    public ProxySourceDTO(Long id, String name, String type, Boolean selected, Boolean isDefault) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.selected = selected;
        this.isDefault = isDefault;
    }
}
