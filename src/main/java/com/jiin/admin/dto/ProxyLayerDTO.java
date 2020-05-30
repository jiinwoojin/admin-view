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
    private List<ProxySourceDTO> sources;
    private List<ProxyCacheDTO> caches;
    private Boolean selected;
    private Boolean isDefault;

    public ProxyLayerDTO(){
        this.sources = new ArrayList<>();
    }

    public ProxyLayerDTO(Long id, String name, String title, List<ProxySourceDTO> sources, List<ProxyCacheDTO> caches, Boolean selected, Boolean isDefault){
        this.id = id;
        this.name = name;
        this.title = title;
        this.sources = sources;
        this.caches = caches;
        this.selected = selected;
        this.isDefault = isDefault;
    }
}
