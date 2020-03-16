package com.jiin.admin.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.jiin.admin.entity.MapLayer;
import com.jiin.admin.entity.MapSource;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class DockerService {

    @Value("classpath:data/default-map-proxy.yaml")
    File defaultMapProxy;

    @Value("${project.data-path}")
    private String dataPath;

    @Value("${project.mapserver.binary}")
    private String mapserverBinary;

    @PersistenceContext
    EntityManager entityManager;

    public boolean restart() {
        // docker restart
        try{
            System.out.println(">> run > " + "docker restart gis-server1");
            Process process = Runtime.getRuntime().exec("docker restart gis-server1");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(">> read > " + line);
            }
            return true;
        }catch (Exception e){
            System.out.println(">> error > " + e.getMessage());
            return false;
        }
    }

    public void proxyReloadFromDatabase() throws IOException {
        // default load
        YAMLFactory fac = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(fac);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        Map mapproxy = mapper.readValue(defaultMapProxy, Map.class);
        // load db data
        List<Map> layers = (List<Map>) mapproxy.get("layers");
        Map caches = (Map) mapproxy.get("caches");
        Map sources = (Map) mapproxy.get("sources");
        List<MapLayer> dbLayers = entityManager
                .createQuery("SELECT T FROM " + MapLayer.class.getAnnotation(Entity.class).name() + " T WHERE IS_DEFAULT = false", MapLayer.class)
                .getResultList();
        mapproxy.get("layers");
        for(MapLayer o:dbLayers){
            Map map = new LinkedHashMap();
            map.put("name",o.getName());
            map.put("title",o.getTitle());
            //List<String> sourceNames= o.getSource().stream().map(s -> (s.getCacheName() == null ? s.getName() : s.getCacheName())).collect(toList());
            //map.put("source",sourceNames);
            layers.add(map);
        }
        List<MapSource> dbSources = entityManager
                .createQuery("SELECT T FROM " + MapSource.class.getAnnotation(Entity.class).name() + " T WHERE IS_DEFAULT = false", MapSource.class)
                .getResultList();
        for(MapSource o:dbSources){
            Map sourceMap = new LinkedHashMap();
            Map sourceReqMap = new LinkedHashMap();
            Map sourceTypeMap = new LinkedHashMap();
            sourceMap.put("type",o.getType());
            sourceReqMap.put("mapPath",o.getMapPath());
            sourceReqMap.put("layers","world"); // TODO : 추출작업 필요
            sourceMap.put("req",sourceReqMap);
            if(o.getType().equals("mapserver")){
                sourceTypeMap.put("binary",mapserverBinary);
                sourceTypeMap.put("working_dir",dataPath + "/tmp");
                sourceMap.put(o.getType(),sourceTypeMap);
            }
            sources.put(o.getName(),sourceMap);
            // TODO : 옵션 설정작업 필요
            Map cacheMap = new LinkedHashMap();
            Map cacheTypeMap = new LinkedHashMap();
            cacheMap.put("grids",new String[]{"GLOBAL_GEODETIC"});
            cacheMap.put("meta_size",new Integer[]{1,1});
            cacheMap.put("meta_buffer",0);
            cacheMap.put("sources",new String[]{o.getName()}); // TODO : sources 배열 변경시 별도작업 필요
            cacheTypeMap.put("type","file");
            cacheTypeMap.put("directory",dataPath + "/cache/" + o.getName());
            cacheMap.put("cache",cacheTypeMap);
            caches.put((o.getCacheName() == null ? o.getName() : o.getCacheName()),cacheMap);
        }
        // write
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
        StringWriter yamlStr = new StringWriter();
        org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml (options);
        yaml.dump(mapproxy, yamlStr);
        FileUtils.write(new File(dataPath,"proxy/mapproxy.yaml"), yamlStr.toString(),"utf-8");
        // docker restart
        restart();
    }
}
