package com.jiin.admin.config;


import com.jiin.admin.entity.LayerEntity;
import com.jiin.admin.entity.MapEntity;
import com.jiin.admin.entity.MapSymbol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Date;


@Service
public class BootingService {

    @Value("${project.data-path}")
    private String dataPath;

    @Value("classpath:data/default-map-proxy.yaml")
    File defaultMapProxy;


    @Resource
    DockerService dockerService;


    @PersistenceContext
    EntityManager entityManager;

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
        Date now = new Date();
        entityManager.createQuery("DELETE FROM " + MapEntity.class.getAnnotation(Entity.class).name() + " WHERE IS_DEFAULT = true").executeUpdate();
        entityManager.createQuery("DELETE FROM " + LayerEntity.class.getAnnotation(Entity.class).name()+ " WHERE IS_DEFAULT = true").executeUpdate();
        /*YAMLFactory fac = new YAMLFactory();
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
            source.setRegistorId("system");
            source.setRegistorName("system");
            source.setRegistTime(now);
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
            //layer.setSource(sourceEntity);
            layer.setRegistorId("system");
            layer.setRegistorName("system");
            layer.setRegistTime(now);
            entityManager.persist(layer);
        }*/
        // mapproxy write
        dockerService.proxyReloadFromDatabase();

    }
}
