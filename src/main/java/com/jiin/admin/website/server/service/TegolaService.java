package com.jiin.admin.website.server.service;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jiin.admin.entity.MapLayer;
import com.jiin.admin.entity.MapSource;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.DumperOptions;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.util.*;


@Service
public class TegolaService {

    @Value("${project.data-path}")
    private String dataPath;

    public Map loadTegolaConfig() {
        File tomlFile = new File(dataPath, "vectortiles/config.toml");
        Map results = new HashMap();
        try {
            Scanner reader = new Scanner(tomlFile);
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if(line.trim().equals("[[maps.layers]]")){
                    String name = null, minzoom = null, maxzoom = null;
                    while (reader.hasNextLine()) {
                        String innerLine = reader.nextLine();
                        if(innerLine.trim().equals("[[maps.layers]]")){
                            break;
                        }else if(innerLine.trim().startsWith("name")){
                            name = innerLine.split("=")[1].trim().replaceAll("\"","");
                        }else if(innerLine.trim().startsWith("min_zoom")){
                            minzoom = innerLine.split("=")[1].trim();
                        }else if(innerLine.trim().startsWith("max_zoom")){
                            maxzoom = innerLine.split("=")[1].trim();
                        }
                    }
                    if(!StringUtils.isEmpty(name)
                            && !StringUtils.isEmpty(minzoom)
                            && !StringUtils.isEmpty(maxzoom)){
                        Map map = new HashMap();
                        map.put("name",name);
                        map.put("minzoom",minzoom);
                        map.put("maxzoom",maxzoom);
                        results.put(name,map);
                    }
                }
            }
            return results;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
