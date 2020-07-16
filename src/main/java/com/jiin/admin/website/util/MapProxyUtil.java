package com.jiin.admin.website.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.jiin.admin.dto.*;
import com.jiin.admin.vo.ServerCenterInfo;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import pl.jalokim.propertiestojson.util.PropertiesToJsonConverter;

import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// MapProxy 기반 파일 작성 메소드 모음
@Slf4j
public class MapProxyUtil {
    /**
     * MapProxy YAML 파일 중 services 에 해당하는 초기 설정 값을 가져온다.
     * @param
     * @note JPA Entity 대응 필요, wms_srs (복수), wms_title 등 메타 정보 주입 대응 필요
     */
    private static Map<String, Object> loadInitServiceProperties() {
        Map<String, Object> serviceMap = new LinkedHashMap<>();
        serviceMap.put("demo", null);

        Map<String, Object> tmsServiceMap = new LinkedHashMap<>();
        tmsServiceMap.put("origin", "nw");
        serviceMap.put("tms", tmsServiceMap);

        Map<String, Object> wmsServiceMap = new LinkedHashMap<>();
        wmsServiceMap.put("versions", new String[] { "1.1.1", "1.3.0" });
        wmsServiceMap.put("srs", new String[] { "EPSG:4326", "EPSG:900913", "EPSG:3857"});
        wmsServiceMap.put("bbox_srs", new String[] { "EPSG:4326", "EPSG:3857" });
        wmsServiceMap.put("image_formats", new String[] { "image/jpeg", "image/png" });
        serviceMap.put("wms", wmsServiceMap);

        Map<String, Object> wmtsServiceMap = new LinkedHashMap<>();
        wmtsServiceMap.put("restful", true);
        wmtsServiceMap.put("restful_template", "/{Layer}/{TileMatrixSet}/{TileMatrix}/{TileCol}/{TileRow}.{Format}");
        wmtsServiceMap.put("kvp", true);
        serviceMap.put("wmts", wmtsServiceMap);

        return serviceMap;
    }

    /**
     * MapProxy YAML 파일 중 services 에 해당하는 초기 설정 값을 가져온다.
     * @param
     * @note JPA Entity 대응 필요, wms_srs (복수), wms_title 등 메타 정보 주입 대응 필요
     */
    public static String fetchYamlFileContextWithDTO(List<ProxyLayerDTO> layers, List<Object> sources, List<ProxyCacheDTO> caches, List<ProxyGlobalDTO> globals, ServerCenterInfo local, Integer mapServerPort) {
        Map<String, Object> yamlModel = new LinkedHashMap<>();

        List<Map<String, Object>> layers_y = new ArrayList<>();
        Map<String, Object> caches_y = new LinkedHashMap<>();
        Map<String, Object> sources_y = new LinkedHashMap<>();

        layers.forEach(layer -> {
            Map<String, Object> l = new LinkedHashMap<>();
            l.put("name", layer.getName());
            l.put("title", layer.getTitle());

            List<String> sourceNames = new ArrayList<>();
            if (layer.getSources() != null)
                layer.getSources().stream().forEach(s -> sourceNames.add(s.getName()));
            if (layer.getCaches() != null)
                layer.getCaches().stream().forEach(c -> sourceNames.add(c.getName()));
            l.put("sources", sourceNames.stream().toArray(String[]::new));

            layers_y.add(l);
        });

        caches.forEach(cache -> {
            Map<String, Object> depth_main = new LinkedHashMap<>();

            depth_main.put("grids", new String[] { cache.getGrids() });
            depth_main.put("format", cache.getFormat());

            List<String> sourceNames = new ArrayList<>();
            if (cache.getSources() != null)
                cache.getSources().stream().forEach(s -> sourceNames.add(s.getName()));
            depth_main.put("sources", sourceNames);

            // 우선 Meta 값들을 입력 하게 되면 나오게끔은 설정.
            // 그러나 안 입력하면 굳이 안 나오게끔도 설정 했음.
            if (cache.getMetaSizeX() != null && cache.getMetaSizeY() != null)
                depth_main.put("meta_size", new Integer[] { cache.getMetaSizeX(), cache.getMetaSizeY() });

            if (cache.getMetaBuffer() != null)
                depth_main.put("meta_buffer", cache.getMetaBuffer());

            // Cache 값은 우선 배제할 것. (DB 에서는 Not Null 제약 조건만 빼 둘 계획)
            // Map<String, Object> depth_cache = new LinkedHashMap<>();
            // depth_cache.put("type", cache.getCacheType());
            // depth_cache.put("directory", cache.getCacheDirectory());

            // depth_main.put("cache", depth_cache);

            caches_y.put(cache.getName(), depth_main);
        });

        sources.forEach(source -> {
            Map<String, Object> depth_main = new LinkedHashMap<>();
            Map<String, Object> depth_req = new LinkedHashMap<>();
            Map<String, Object> depth_mapserver = new LinkedHashMap<>();

            if (source instanceof ProxySourceMapServerDTO) {
                ProxySourceMapServerDTO dto = (ProxySourceMapServerDTO) source;
                depth_main.put("type", dto.getType());

                depth_req.put("map", dto.getRequestMap());
                depth_req.put("layers", dto.getRequestLayers());

                depth_mapserver.put("binary", dto.getMapServerBinary());
                depth_mapserver.put("working_dir", dto.getMapServerWorkDir());

                depth_main.put("req", depth_req);
                depth_main.put("mapserver", depth_mapserver);

                sources_y.put(dto.getName(), depth_main);
            }

            if (source instanceof ProxySourceWMSDTO) {
                ProxySourceWMSDTO dto = (ProxySourceWMSDTO) source;
                depth_main.put("type", dto.getType());

                depth_main.put("concurrent_requests", dto.getConcurrentRequests());
                depth_main.put("wms_opts", new LinkedHashMap<String, Object>() {{ put("version", dto.getWmsOptsVersion()); }});
                depth_main.put("http", new LinkedHashMap<String, Object>() {{ put("client_timeout", dto.getHttpClientTimeout()); }});

                depth_req.put("url", String.format("http://%s:%d/%s", local.getIp(), mapServerPort, dto.getRequestURL()));
                depth_req.put("layers", dto.getRequestLayers());
                depth_req.put("map", dto.getRequestMap());
                depth_req.put("transparent", dto.getRequestTransparent());

                depth_main.put("req", depth_req);
                depth_main.put("supported_srs", String.format("[\\\'%s\\\']", dto.getSupportedSRS().replace(",", "\\\', \\\'")));

                sources_y.put(dto.getName(), depth_main);
            }
        });

        Properties properties = new Properties();
        for (ProxyGlobalDTO global : globals) {
            properties.put(global.getKey(), global.getValue());
        }

        String json = new PropertiesToJsonConverter().convertToJson(properties);
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> globals_y = null;
        try {
            globals_y = mapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            log.error("ERROR - " + e.getMessage());
        }

        yamlModel.put("services", loadInitServiceProperties());
        yamlModel.put("layers", layers_y);
        yamlModel.put("caches", caches_y);
        yamlModel.put("sources", sources_y);
        yamlModel.put("grids", new LinkedHashMap<String, Object>() {{
            put("osm_grid", new LinkedHashMap<String, Object>() {{
                put("srs", "EPSG:3857");
                put("origin", "nw");
            }});
        }});
        yamlModel.put("globals", globals_y);

        mapper = new ObjectMapper(new YAMLFactory()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        );
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO); // 이를 BLOCK 으로 설정하면, 배열 field 가 (- a) 같이 나오고, AUTO 로 설정하면 배열 field 가 배열로 나오지만 object 가 SET 문자열로 나옴.

        DumperOptions options2 = new DumperOptions();
        options2.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);
        Yaml yaml2 = new Yaml(options2);

        StringBuffer sb = new StringBuffer();
        for (String key : Arrays.asList("services", "layers", "caches", "sources", "grids", "globals")) {
            StringWriter yamlStr = new StringWriter();
            Map<String, Object> serviceMap = new HashMap<String, Object>() {{
                put(key, yamlModel.get(key));
            }};

            if (key.equals("globals") || key.equals("sources")) {
                yaml2.dump(serviceMap, yamlStr);
            } else {
                yaml.dump(serviceMap, yamlStr);
            }

            String temp = null, context = yamlStr.toString();
            Pattern p;
            Matcher m;

            switch(key) {
                case "services" :
                    context = context.replace("demo: null", "demo:");
                    context = context.replace("grids: [GLOBAL_GEODETIC]", "grids: [\'GLOBAL_GEODETIC\']");
                    context = context.replace("versions: [1.1.1, 1.3.0]", "versions: [\'1.1.1\', \'1.3.0\']");
                    context = context.replace("image_formats: [image/jpeg, image/png]", "image_formats: [\'image/jpeg\', \'image/png\']");

                    p = Pattern.compile("(\\s\\{).*?(,\\s).*?(,\\s+).*?(\\})");
                    m = p.matcher(context);

                    while (m.find()) {
                        temp = m.group(0);
                        temp = temp.replace(m.group(1), "\n    ");
                        temp = temp.replace(m.group(2), "\n    ");
                        temp = temp.replace(m.group(3), "\n    ");
                        temp = temp.replaceFirst("(?s)(.*)" + m.group(4), "$1");
                        context = context.replace(m.group(0), temp);
                    }

                    p = Pattern.compile("(\\s\\{).*?:.*?(\\})");
                    m = p.matcher(context);
                    while (m.find()) {
                        temp = m.group(0);
                        temp = temp.replace(m.group(1), "\n    ");
                        temp = temp.replace(m.group(2), "");
                        context = context.replace(m.group(0), temp);
                    }

                    context = context.replace("origin: nw", "origin: \'nw\'");
                    break;

                // 로직 최소화 작업 진행 : SOURCES 를 BLOCK. 단, CACHE 는 배열 처리 때문에 AUTO 로 진행할 것.
                case "sources" :
                case "caches" :
                    Map<String, Object> keyMap = (Map<String, Object>) yamlModel.get(key);
                    if (!keyMap.isEmpty()) {
                        if (key.equals("sources")) {
                            context = context.replaceAll("\\'", "");
                            context = context.replaceAll("\\\\", "\'");
                        }
                    } else {
                        context = String.format("%s : {}\n", key);
                    }
                    break;

                case "layers" :
                    List<Map<String, Object>> keyList = (List<Map<String, Object>>) yamlModel.get(key);
                    if (!keyList.isEmpty()) {
                        String[] sp = context.split("\\n");
                        StringBuffer tmp = new StringBuffer();
                        for (String s : sp) {
                            tmp.append(String.format("%s%s\n", s.equals("layers:") ? "" : "  ", s));
                        }
                        context = tmp.toString();
                    } else {
                        context = String.format("%s : []\n", key);
                    }
                    break;

                case "grids" :
                    p = Pattern.compile("(\\s\\{).*?(,\\s).*?(\\})");
                    m = p.matcher(context);
                    while (m.find()) {
                        temp = m.group(0);
                        temp = temp.replace(m.group(1), "\n    ");
                        temp = temp.replace(m.group(2), "\n    ");
                        temp = temp.replace(m.group(3), "");
                        context = context.replace(m.group(0), temp);
                    }

                    if (key.equals("grids")) {
                        context = context.replace("\'EPSG:3857\'", "EPSG:3857");
                    }
                    break;
            }

            if (!key.equals("globals")) sb.append(context + "\n");
            else sb.append(context);
        }

        return sb.toString();
    }
}
