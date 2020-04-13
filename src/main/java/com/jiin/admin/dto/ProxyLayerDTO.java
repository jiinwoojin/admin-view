package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProxyLayerDTO { // MyBatis Based DTO
    private Long id;
    private String name;
    private String title;
    private Boolean selected;
    private List<ProxySourceDTO> sources;

    public ProxyLayerDTO(){
        this.sources = new ArrayList<>();
    }

    public ProxyLayerDTO(Long id, String name, String title, Boolean selected, List<ProxySourceDTO> sources){
        this.id = id;
        this.name = name;
        this.title = title;
        this.selected = selected;
        this.sources = sources;
    }
}
