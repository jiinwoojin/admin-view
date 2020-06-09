package com.jiin.admin.website.model;

import com.jiin.admin.dto.ProxyLayerDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProxyLayerModelV2 {
    private long id;
    private String method;
    private String name;
    private String title;
    private List<String> sources;
    private List<String> caches;

    public ProxyLayerModelV2(){
        this.id = 0L;
        this.method = "INSERT";
        this.sources = new ArrayList<>();
        this.caches = new ArrayList<>();
    }

    public ProxyLayerModelV2(long id, String method, String name, String title, List<String> sources, List<String> caches){
        this.id = id;
        this.method = method;
        this.name = name;
        this.title = title;
        this.sources = sources;
        this.caches = caches;
    }

    // convertDTO 에서는 sources, caches 데이터가 무의미해서 new ArrayList() 로 두었다.
    public static ProxyLayerDTO convertDTO(ProxyLayerModelV2 model){
        if(model == null) return null;
        return new ProxyLayerDTO(model.getId(), model.getName(), model.getTitle(), new ArrayList<>(), new ArrayList<>(), false, false);
    }
}
