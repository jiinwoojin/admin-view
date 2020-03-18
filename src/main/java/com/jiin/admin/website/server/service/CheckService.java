package com.jiin.admin.website.server.service;

import com.jiin.admin.entity.Layer;
import com.jiin.admin.entity.Map;
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
            tableName = Layer.class.getAnnotation(Entity.class).name();
        }else if(type.equalsIgnoreCase("map")){
            tableName = Map.class.getAnnotation(Entity.class).name();
        }
        int count = mapper.countDuplicate(tableName,name);
        return (count != 0);
    }
}
