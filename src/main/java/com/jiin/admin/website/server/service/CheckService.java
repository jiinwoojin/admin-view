package com.jiin.admin.website.server.service;

import com.jiin.admin.entity.MapLayer;
import com.jiin.admin.entity.MapSource;
import com.jiin.admin.website.server.mapper.CheckMapper;
import com.jiin.admin.website.view.mapper.ManageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;
import java.util.List;
import java.util.Map;

@Service
public class CheckService {

    @Resource
    private CheckMapper mapper;

    public boolean checkDuplicate(String type, String name) {
        String tableName = null;
        if(type.equalsIgnoreCase("layer")){
            tableName = MapLayer.class.getAnnotation(Entity.class).name();
        }else if(type.equalsIgnoreCase("source")){
            tableName = MapSource.class.getAnnotation(Entity.class).name();
        }
        int count = mapper.countDuplicate(tableName,name);
        return (count != 0);
    }
}
