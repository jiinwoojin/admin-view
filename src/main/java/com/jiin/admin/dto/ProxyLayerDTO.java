package com.jiin.admin.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public ProxyLayerDTO() {
        this.sources = new ArrayList<>();
    }

    public ProxyLayerDTO(Long id, String name, String title, List<ProxySourceDTO> sources, List<ProxyCacheDTO> caches, Boolean selected, Boolean isDefault) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.sources = sources;
        this.caches = caches;
        this.selected = selected;
        this.isDefault = isDefault;
    }

    public String getSourceKeys() throws JsonProcessingException {
        if (this.sources == null) return "[]";
        else {
            ObjectMapper obj = new ObjectMapper();
            return obj.writeValueAsString(this.sources.stream().map(o -> o.getName()).collect(Collectors.toList()));
        }
    }

    public String getCacheKeys() throws JsonProcessingException {
        if (this.caches == null) return "[]";
        else {
            ObjectMapper obj = new ObjectMapper();
            return obj.writeValueAsString(this.caches.stream().map(o -> o.getName()).collect(Collectors.toList()));
        }
    }
}
