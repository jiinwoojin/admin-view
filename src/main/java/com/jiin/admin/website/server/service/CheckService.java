package com.jiin.admin.website.server.service;

import com.jiin.admin.entity.*;
import com.jiin.admin.website.server.mapper.CheckMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;

@Service
public class CheckService {

    @Resource
    private CheckMapper mapper;

    public boolean checkDuplicate(String type, String name) {
        String tableName;

        if(type.equalsIgnoreCase("layer")){
            tableName = LayerEntity.class.getAnnotation(Entity.class).name();
        } else if(type.equalsIgnoreCase("map")){
            tableName = MapEntity.class.getAnnotation(Entity.class).name();
        } else if(type.equalsIgnoreCase("proxylayer")){
            tableName = ProxyLayerEntity.class.getAnnotation(Entity.class).name();
        } else if(type.equalsIgnoreCase("proxysource")){
            tableName = ProxySourceEntity.class.getAnnotation(Entity.class).name();
        } else if(type.equalsIgnoreCase("proxycache")){
            tableName = ProxyCacheEntity.class.getAnnotation(Entity.class).name();
        } else {
            tableName = null;
        }

        if(tableName != null) {
            int count = mapper.countDuplicate(tableName, name);
            return (count != 0);
        } else return false;
    }
}
