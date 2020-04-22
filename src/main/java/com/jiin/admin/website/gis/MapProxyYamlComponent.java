package com.jiin.admin.website.gis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.jiin.admin.entity.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MapProxyYamlComponent {
    @Value("${project.data-path}")
    private String dataPath;

    private static final String workDir = "/proxy";
    private static final String fileName = "/gis-server-1.yaml";

    private Map<String, Object> initializeServiceValues(){
        Map<String, Object> serviceMap = new LinkedHashMap<>();
        serviceMap.put("demo", null);

        Map<String, Object> wmsServiceMap = new LinkedHashMap<>();
        wmsServiceMap.put("versions", new String[] { "1.1.1", "1.3.0" });
        wmsServiceMap.put("srs", new String[] { "EPSG:4326", "EPSG:900913", "EPSG:3857"});
        wmsServiceMap.put("bbox_srs", new String[] { "EPSG:4326", "EPSG:3857" });
        wmsServiceMap.put("image_formats", new String[] { "image/jpeg", "image/png" });

        serviceMap.put("wms", wmsServiceMap);

        return serviceMap;
    }

    // yaml 파일 저장 기능 구현 : gis-service-1.yaml 처럼 나올 수 있도록 정규식, LinkedHashMap 등을 사용.
   public void writeProxyYamlFileWithSettingData(List<ProxyLayerEntity> layers, List<ProxySourceEntity> sources, List<ProxyCacheEntity> caches) throws IOException {
        Map<String, Object> yamlModel = new LinkedHashMap<>();

        List<Map<String, Object>> layers_y = new ArrayList<>();
        Map<String, Object> caches_y = new LinkedHashMap<>();
        Map<String, Object> sources_y = new LinkedHashMap<>();

        layers.forEach(layer -> {
            Map<String, Object> l = new LinkedHashMap<>();
            l.put("name", layer.getName());
            l.put("title", layer.getTitle());

            List<String> sourceNames = new ArrayList<>();
            List<ProxyLayerSourceRelationEntity> layerSourceRelations = layer.getSources();
            layerSourceRelations.stream().filter(r -> r.getSource() != null).forEach(r -> sourceNames.add(r.getSource().getName()));
            List<ProxyLayerCacheRelationEntity> layerCacheRelations = layer.getCaches();
            layerCacheRelations.stream().filter(r -> r.getCache() != null).forEach(r -> sourceNames.add(r.getCache().getName()));
            l.put("sources", sourceNames.stream().toArray(String[]::new));

            layers_y.add(l);
        });

       caches.forEach(cache -> {
           Map<String, Object> depth_main = new LinkedHashMap<>();
           Map<String, Object> depth_cache = new LinkedHashMap<>();

           depth_main.put("grids", new String[] { "GLOBAL_GEODETIC" });
           depth_main.put("meta_size", new Integer[] { cache.getMetaSizeX(), cache.getMetaSizeY() });
           depth_main.put("meta_buffer", cache.getMetaBuffer());

           List<String> sourceNames = new ArrayList<>();
           List<ProxyCacheSourceRelationEntity> cacheSourceRelations = cache.getSources();
           cacheSourceRelations.stream().filter(r -> r.getSource() != null).forEach(r -> sourceNames.add(r.getSource().getName()));
           depth_main.put("sources", sourceNames);

           depth_cache.put("type", cache.getCacheType());
           depth_cache.put("directory", cache.getCacheDirectory());

           depth_main.put("cache", depth_cache);

           caches_y.put(cache.getName(), depth_main);
       });

        sources.forEach(source -> {
            Map<String, Object> depth_main = new LinkedHashMap<>();
            Map<String, Object> depth_req = new LinkedHashMap<>();
            Map<String, Object> depth_mapserver = new LinkedHashMap<>();

            depth_main.put("type", source.getType());

            depth_req.put("map", source.getRequestMap());
            depth_req.put("layers", source.getRequestLayers());

            depth_mapserver.put("binary", source.getMapServerBinary());
            depth_mapserver.put("working_dir", source.getMapServerWorkDir());

            depth_main.put("req", depth_req);
            depth_main.put("mapserver", depth_mapserver);

            sources_y.put(source.getName(), depth_main);
        });

        yamlModel.put("services", initializeServiceValues());
        yamlModel.put("layers", layers_y);
        yamlModel.put("caches", caches_y);
        yamlModel.put("sources", sources_y);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        );
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO); // 이를 BLOCK 으로 설정하면, 배열 field 가 (- a) 같이 나오고, AUTO 로 설정하면 배열 field 가 배열로 나오지만 object 가 SET 문자열로 나옴.

        File directory = new File(dataPath + workDir);
        if(!directory.exists()) directory.mkdirs();

        File file = new File(dataPath + workDir + fileName);
        if(!file.exists()) file.createNewFile();

        Yaml yaml = new Yaml(options);

        StringBuffer sb = new StringBuffer();
        for(String key : Arrays.asList("services", "layers", "caches", "sources")){
            StringWriter yamlStr = new StringWriter();
            Map<String, Object> serviceMap = new HashMap<String, Object>() {{
                put(key, yamlModel.get(key));
            }};
            yaml.dump(serviceMap, yamlStr);

            String context = yamlStr.toString();

            switch(key) {
                case "services" :
                    context = context.replace("demo: null", "demo:");
                    context = context.replace("grids: [GLOBAL_GEODETIC]", "grids: [\'GLOBAL_GEODETIC\']");
                    context = context.replace("versions: [1.1.1, 1.3.0]", "versions: [\'1.1.1\', \'1.3.0\']");
                    context = context.replace("image_formats: [image/jpeg, image/png]", "image_formats: [\'image/jpeg\', \'image/png\']");
                    break;

                case "sources" :
                case "caches" :
                    Pattern mapPattern = Pattern.compile("(\\s\\{).*?(,\\s).*?(\\})");
                    Matcher m = mapPattern.matcher(context);
                    while(m.find()){
                        String mapText = m.group(0);
                        mapText = mapText.replace(m.group(1), "\n\t\t\t");
                        mapText = mapText.replace(m.group(2), "\n\t\t\t");
                        mapText = mapText.replace(m.group(3), "");
                        context = context.replace(m.group(0), mapText);
                    }

                    if(key.equals("sources")){
                        context = context.replaceAll("\\'", "");
                    } else {
                        context = context.replace("grids: [GLOBAL_GEODETIC]", "grids: [\'GLOBAL_GEODETIC\']");
                    }
                    break;
            }

            if(!key.equals("sources")) sb.append(context + "\n");
            else sb.append(context);
        }

        FileUtils.write(new File(dataPath,workDir + fileName), sb.toString(), "utf-8");
    }

    public String getMapProxyYamlFileContext() throws IOException {
        File directory = new File(dataPath + workDir);
        if(!directory.exists()) directory.mkdirs();

        File file = new File(dataPath + workDir + fileName);
        if(!file.exists()) file.createNewFile();

        StringBuffer sb = new StringBuffer();
        FileReader reader = new FileReader(dataPath + workDir + fileName);
        BufferedReader bufReader = new BufferedReader(reader);
        String line = "";
        while((line = bufReader.readLine()) != null){
            sb.append(line + "\n");
        }

        return sb.length() == 0 ? "[ NO CONTEXT ]" : sb.toString();
    }

    public Map<String, Object> getMapProxyYamlFileMapObject() throws IOException {
        File directory = new File(dataPath + workDir);
        if(!directory.exists()) directory.mkdirs();

        File file = new File(dataPath + workDir + fileName);
        if(!file.exists()) {
            file.createNewFile();
            return null;
        }

        YAMLFactory fac = new YAMLFactory();
        ObjectMapper mapper = new ObjectMapper(fac);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return mapper.readValue(file, Map.class);
    }
}
