package com.jiin.admin.website.util;

import com.jiin.admin.dto.ProxyCacheDTO;
import com.jiin.admin.dto.ProxyLayerDTO;
import com.jiin.admin.dto.ProxySourceDTO;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// MapProxy 기반 파일 작성 메소드 모음
public class MapProxyUtil {
    /**
     * MapProxy YAML 파일 중 services 에 해당하는 초기 설정 값을 가져온다.
     * @param
     * @note JPA Entity 대응 필요, wms_srs (복수), wms_title 등 메타 정보 주입 대응 필요
     */
    private static Map<String, Object> loadInitServiceProperties(){
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

    /**
     * MapProxy YAML 파일 중 services 에 해당하는 초기 설정 값을 가져온다.
     * @param
     * @note JPA Entity 대응 필요, wms_srs (복수), wms_title 등 메타 정보 주입 대응 필요
     */
    public String fetchYamlFileContextWithDTO(List<ProxyLayerDTO> layers, List<ProxySourceDTO> sources, List<ProxyCacheDTO> caches, String dataPath) throws IOException {
//        Map<String, Object> yamlModel = new LinkedHashMap<>();
//
//        List<Map<String, Object>> layers_y = new ArrayList<>();
//        Map<String, Object> caches_y = new LinkedHashMap<>();
//        Map<String, Object> sources_y = new LinkedHashMap<>();
//
//        layers.forEach(layer -> {
//            Map<String, Object> l = new LinkedHashMap<>();
//            l.put("name", layer.getName());
//            l.put("title", layer.getTitle());
//
//            List<String> sourceNames = new ArrayList<>();
//            List<ProxyLayerSourceRelationEntity> layerSourceRelations = layer.getSources();
//            layerSourceRelations.stream().filter(r -> r.getSource() != null).forEach(r -> sourceNames.add(r.getSource().getName()));
//            List<ProxyLayerCacheRelationEntity> layerCacheRelations = layer.getCaches();
//            layerCacheRelations.stream().filter(r -> r.getCache() != null).forEach(r -> sourceNames.add(r.getCache().getName()));
//            l.put("sources", sourceNames.stream().toArray(String[]::new));
//
//            layers_y.add(l);
//        });
//
//        caches.forEach(cache -> {
//            Map<String, Object> depth_main = new LinkedHashMap<>();
//            Map<String, Object> depth_cache = new LinkedHashMap<>();
//
//            depth_main.put("grids", new String[] { "GLOBAL_GEODETIC" });
//            depth_main.put("meta_size", new Integer[] { cache.getMetaSizeX(), cache.getMetaSizeY() });
//            depth_main.put("meta_buffer", cache.getMetaBuffer());
//
//            List<String> sourceNames = new ArrayList<>();
//            List<ProxyCacheSourceRelationEntity> cacheSourceRelations = cache.getSources();
//            cacheSourceRelations.stream().filter(r -> r.getSource() != null).forEach(r -> sourceNames.add(r.getSource().getName()));
//            depth_main.put("sources", sourceNames);
//
//            depth_cache.put("type", cache.getCacheType());
//            depth_cache.put("directory", cache.getCacheDirectory());
//
//            depth_main.put("cache", depth_cache);
//
//            caches_y.put(cache.getName(), depth_main);
//        });
//
//        sources.forEach(source -> {
//            Map<String, Object> depth_main = new LinkedHashMap<>();
//            Map<String, Object> depth_req = new LinkedHashMap<>();
//            Map<String, Object> depth_mapserver = new LinkedHashMap<>();
//
//            depth_main.put("type", source.getType());
//
//            depth_req.put("map", source.getRequestMap());
//            depth_req.put("layers", source.getRequestLayers());
//
//            depth_mapserver.put("binary", source.getMapServerBinary());
//            depth_mapserver.put("working_dir", source.getMapServerWorkDir());
//
//            depth_main.put("req", depth_req);
//            depth_main.put("mapserver", depth_mapserver);
//
//            sources_y.put(source.getName(), depth_main);
//        });
//
//        yamlModel.put("services", loadInitServiceProperties());
//        yamlModel.put("layers", layers_y);
//        yamlModel.put("caches", caches_y);
//        yamlModel.put("sources", sources_y);
//
//        ObjectMapper mapper = new ObjectMapper(new YAMLFactory()
//                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
//                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
//        );
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        DumperOptions options = new DumperOptions();
//        options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO); // 이를 BLOCK 으로 설정하면, 배열 field 가 (- a) 같이 나오고, AUTO 로 설정하면 배열 field 가 배열로 나오지만 object 가 SET 문자열로 나옴.
//
//        File directory = new File(dataPath + Constants.);
//        if(!directory.exists()) directory.mkdirs();
//
//        File file = new File(dataPath + workDir + fileName);
//        if(!file.exists()) file.createNewFile();
//
//        Yaml yaml = new Yaml(options);
//
//        StringBuffer sb = new StringBuffer();
//        for(String key : Arrays.asList("services", "layers", "caches", "sources")){
//            StringWriter yamlStr = new StringWriter();
//            Map<String, Object> serviceMap = new HashMap<String, Object>() {{
//                put(key, yamlModel.get(key));
//            }};
//            yaml.dump(serviceMap, yamlStr);
//
//            String temp = null, context = yamlStr.toString();
//            Pattern p;
//            Matcher m;
//            switch(key) {
//                case "services" :
//                    context = context.replace("demo: null", "demo:");
//                    context = context.replace("grids: [GLOBAL_GEODETIC]", "grids: [\'GLOBAL_GEODETIC\']");
//                    context = context.replace("versions: [1.1.1, 1.3.0]", "versions: [\'1.1.1\', \'1.3.0\']");
//                    context = context.replace("image_formats: [image/jpeg, image/png]", "image_formats: [\'image/jpeg\', \'image/png\']");
//                    break;
//
//                case "sources" :
//                case "caches" :
//                    p = Pattern.compile("(\\s\\{).*?(,\\s).*?(\\})");
//                    m = p.matcher(context);
//                    while(m.find()){
//                        temp = m.group(0);
//                        temp = temp.replace(m.group(1), "\n      ");
//                        temp = temp.replace(m.group(2), "\n      ");
//                        temp = temp.replace(m.group(3), "");
//                        context = context.replace(m.group(0), temp);
//                    }
//
//                    if(key.equals("sources")){
//                        context = context.replaceAll("\\'", "");
//                    } else {
//                        context = context.replace("grids: [GLOBAL_GEODETIC]", "grids: [\'GLOBAL_GEODETIC\']");
//                    }
//                    break;
//
//                case "layers" :
//                    String[] sp = context.split("\\n");
//                    StringBuffer tmp = new StringBuffer();
//                    for(String s : sp){
//                        tmp.append(String.format("%s%s\n", s.equals("layers:") ? "" : "  ", s));
//                    }
//                    context = tmp.toString();
//                    break;
//            }
//
//            if(!key.equals("sources")) sb.append(context + "\n");
//            else sb.append(context);
//        }
//
//        return sb.toString();
        return "어머니 미안해 리펙토링인가";
    }
}
