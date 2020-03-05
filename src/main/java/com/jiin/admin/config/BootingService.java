package com.jiin.admin.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.jiin.admin.entity.MapSymbol;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static org.yaml.snakeyaml.DumperOptions.ScalarStyle.SINGLE_QUOTED;


@Service
public class BootingService {

    @Value("${project.data-path}")
    private String dataPath;

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

    public void initializeLayer() throws IOException {
        System.out.println(">>> initializeLayer Start");
        YAMLFactory fac = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(fac);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        Map mapproxy = mapper.readValue(new File(dataPath,"proxy/mapproxy__.yaml"), Map.class);
        File mapserver = new File(dataPath,"mapserver");
        for( File dir : mapserver.listFiles()){
            System.out.println( dir.getName() );
        }
        // write
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
        StringWriter yamlStr = new StringWriter();
        org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml (options);
        yaml.dump(mapproxy, yamlStr);
        FileUtils.write(new File(dataPath,"proxy/mapproxy__.yaml"), yamlStr.toString(),"utf-8");
        // docker restart
        try{
            System.out.println("\t\t >> run > " + "docker restart gis-server1");
            Process process = Runtime.getRuntime().exec("docker restart gis-server1");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("\t\t >> " + line);
            }
        }catch (Exception e){
            System.out.println("\t\t >> error > " + e.getMessage());
        }
    }
}
