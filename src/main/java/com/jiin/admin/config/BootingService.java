package com.jiin.admin.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jiin.admin.entity.MapLayer;
import com.jiin.admin.entity.MapSource;
import com.jiin.admin.entity.MapSymbol;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


@Service
public class BootingService {

    @Value("${project.data-path}")
    private String dataPath;

    @Value("classpath:data/default-map-proxy.yaml")
    File defaultMapProxy;

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    DockerService dockerService;

    @Transactional
    public void initializeSymbol() {
        System.out.println(">>> initializeSymbol Start");
        entityManager.createQuery("DELETE FROM " + MapSymbol.class.getAnnotation(Entity.class).name()).executeUpdate();
        int i = 0;
        while(i++ < 10){
            MapSymbol symbol = new MapSymbol();
            symbol.setName("테스트");
            symbol.setCode("SYMBOL" + i);
            symbol.setType("POLYGON");
            entityManager.persist(symbol);
        }
    }

    @Transactional
    public void initializeLayer() throws IOException {
        // 기본 레이어 및 소스 초기화
        System.out.println(">>> initializeLayer Start");
        entityManager.createQuery("DELETE FROM " + MapLayer.class.getAnnotation(Entity.class).name() + " WHERE IS_DEFAULT = true").executeUpdate();
        entityManager.createQuery("DELETE FROM " + MapSource.class.getAnnotation(Entity.class).name()+ " WHERE IS_DEFAULT = true").executeUpdate();
        YAMLFactory fac = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(fac);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        Map mapproxy = mapper.readValue(defaultMapProxy, Map.class);
        List<Map> layers = (List<Map>) mapproxy.get("layers");
        Map caches = (Map) mapproxy.get("caches");
        Map sources = (Map) mapproxy.get("sources");
        Map<String,MapSource> entity = new HashMap();
        for(Object key : sources.keySet()){
            Map sourceMap = (Map) sources.get(key);
            Map sourceReqMap = (Map) sourceMap.get("req");
            MapSource source = new MapSource();
            source.setDefault(true); // default
            source.setName((String) key);
            source.setType((String) sourceMap.get("type"));
            if(sourceReqMap != null){
                source.setMapPath((String) sourceReqMap.get("map"));
                source.setLayers("world"); // TODO : 추출작업 필요
            }
            for(Object cacheKey : caches.keySet()){
                Map cacheMap = (Map) caches.get(cacheKey);
                List<String> cacheSources = (List<String>) cacheMap.get("sources");
                if(cacheSources.contains(key)){
                    source.setUseCache(true);
                    source.setCacheName((String) cacheKey);
                    break;
                }
            }
            entityManager.persist(source);
            entity.put(source.isUseCache() ? source.getCacheName() : source.getName(),source);
        }
        for(Map o : layers){
            MapLayer layer = new MapLayer();
            layer.setDefault(true); // default
            layer.setName((String) o.get("name"));
            layer.setTitle((String) o.get("title"));
            List<String> sourceStrs = (List) o.get("sources");
            List<MapSource> sourceEntity = new ArrayList<>();
            for(String sourceStr : sourceStrs){
                sourceEntity.add(entity.get(sourceStr));
            }
            layer.setSource(sourceEntity);
            entityManager.persist(layer);
        }
        // mapproxy write
        dockerService.proxyReloadFromDatabase();

    }
}
