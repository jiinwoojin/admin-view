package com.jiin.admin.website.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class YAMLFileUtil {
    /**
     * 설정 기반 YAML 파일을 MAP 으로 변환한다.
     * @param yamlFile File
     */
    public static Map<String, Object> fetchMapByYAMLFile(File yamlFile) throws IOException {
        YAMLFactory fac = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(fac);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return mapper.readValue(yamlFile, Map.class);
    }

    /**
     * 설정 기반 MAP 데이터를 YAML 파일 내용으로 변환한다.
     * @Param map Map
     */
    public static String fetchYAMLStringByMap(Map<String, Object> map, String type) {
        return fetchYAMLStringByMap(map,type,false);
    }

    public static String fetchYAMLStringByMap(Map map, String type, boolean prettyFlow) {
        DumperOptions options = new DumperOptions();
        switch(type) {
            case "AUTO" :
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO);
                break;
            case "BLOCK" :
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                break;
            case "FLOW" :
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
                break;
        }
        options.setPrettyFlow(prettyFlow);
        Yaml yaml = new Yaml(options);
        return yaml.dump(map);
    }
}
