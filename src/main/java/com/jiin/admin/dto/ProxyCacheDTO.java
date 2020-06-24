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
public class ProxyCacheDTO { // MyBatis Based DTO
    private Long id;
    private String name;
    private Integer metaSizeX;
    private Integer metaSizeY;
    private Integer metaBuffer;
    private List<ProxySourceDTO> sources;
    private String cacheType;
    private String cacheDirectory;
    private String format;
    private Boolean selected;
    private Boolean isDefault;

    public ProxyCacheDTO(){
        this.sources = new ArrayList<>();
    }

    public ProxyCacheDTO(Long id, String name, Integer metaSizeX, Integer metaSizeY, Integer metaBuffer, List<ProxySourceDTO> sources, String cacheType, String cacheDirectory, String format, Boolean selected, Boolean isDefault){
        this.id = id;
        this.name = name;
        this.metaSizeX = metaSizeX;
        this.metaSizeY = metaSizeY;
        this.metaBuffer = metaBuffer;
        this.sources = sources;
        this.cacheType = cacheType;
        this.cacheDirectory = cacheDirectory;
        this.format = format;
        this.selected = selected;
        this.isDefault = isDefault;
    }

    public String getSourceKeys() throws JsonProcessingException {
        if(this.sources == null) return "[]";
        else {
            ObjectMapper obj = new ObjectMapper();
            return obj.writeValueAsString(this.sources.stream().map(o -> o.getName()).collect(Collectors.toList()));
        }
    }
}
