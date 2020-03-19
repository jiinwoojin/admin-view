package com.jiin.admin.website.server.service;

import com.jiin.admin.entity.LayerEntity;
import com.jiin.admin.entity.MapEntity;
import com.jiin.admin.website.server.mapper.CheckMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;

@Service
public class CheckService {

    @Resource
    private CheckMapper mapper;

    public boolean checkDuplicate(String type, String name) {
        String tableName = null;
        if(type.equalsIgnoreCase("layer")){
            tableName = LayerEntity.class.getAnnotation(Entity.class).name();
        }else if(type.equalsIgnoreCase("map")){
            tableName = MapEntity.class.getAnnotation(Entity.class).name();
        }
        int count = mapper.countDuplicate(tableName,name);
        return (count != 0);
    }
}
