package com.jiin.admin.website.gis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.jiin.admin.entity.*;
import com.jiin.admin.website.model.yaml.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MapProxyYamlComponent {
    @Value("${project.data-path}")
    private String dataPath;

    private static final String workDir = "/proxy";
    private static final String fileName = "/gis-server-1.yaml";

    private MapProxyYamlModel initializeYamlWrite(){
        MapProxyYamlModel model = new MapProxyYamlModel();
        Map<String, Object> serviceMap = model.getServices();
        serviceMap.put("demo", null);
        serviceMap.put("wms", new ProxyServiceYamlModel(
            new String[] { "1.1.1", "1.3.0" },
            new String[] { "EPSG:4326", "EPSG:900913", "EPSG:3857"},
            new String[] { "EPSG:4326", "EPSG:3857" },
            new String[] { "image/jpeg", "image/png" }
        ));
        model.setServices(serviceMap);
        return model;
    }

    // yaml 파일 저장 기능 구현 : 하지만 gis-server-1.yaml 처럼 나오지 않고, mapproxy.yaml 처럼 나옴... + 따옴표 문제 있음.
   public void writeProxyYamlFileWithSettingData(List<ProxyLayerEntity> layers, List<ProxySourceEntity> sources, List<ProxyCacheEntity> caches) throws IOException {
        MapProxyYamlModel model = initializeYamlWrite();

        List<ProxyLayerYamlModel> layerModels = model.getLayers();
        layers.forEach(layer -> {
            ProxyLayerYamlModel l = new ProxyLayerYamlModel();
            l.setName(layer.getName());
            l.setTitle(layer.getTitle());

            List<String> sourceNames = new ArrayList<>();
            List<ProxyLayerSourceRelationEntity> layerSourceRelations = layer.getSources();
            layerSourceRelations.stream().filter(r -> r.getSource() != null).forEach(r -> sourceNames.add(r.getSource().getName()));
            List<ProxyLayerCacheRelationEntity> layerCacheRelations = layer.getCaches();
            layerCacheRelations.stream().filter(r -> r.getCache() != null).forEach(r -> sourceNames.add(r.getCache().getName()));

            l.setSources(sourceNames.stream().toArray(String[]::new));

            layerModels.add(l);
        });
        model.setLayers(layerModels);

        Map<String, ProxySourceYamlModel> sourceModels = model.getSources();
        sources.forEach(source -> {
            sourceModels.put(source.getName(), new ProxySourceYamlModel(source.getType(), source.getRequestMap(), source.getRequestLayers(), source.getMapServerBinary(), source.getMapServerWorkDir()));
        });
        model.setSources(sourceModels);

        Map<String, ProxyCacheYamlModel> cacheModels = model.getCaches();
        caches.forEach(cache -> {
            List<String> sourceNames = new ArrayList<>();
            List<ProxyCacheSourceRelationEntity> cacheSourceRelations = cache.getSources();
            cacheSourceRelations.stream().filter(r -> r.getSource() != null).forEach(r -> sourceNames.add(r.getSource().getName()));
            cacheModels.put(cache.getName(), new ProxyCacheYamlModel(cache.getCacheType(), cache.getCacheDirectory(), cache.getMetaSizeX(), cache.getMetaSizeY(), cache.getMetaBuffer(), sourceNames.stream().toArray(String[]::new)));
        });
        model.setCaches(cacheModels);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
        );
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Representer representer = new Representer();
        representer.addClassTag(MapProxyYamlModel.class, Tag.MAP);
        representer.addClassTag(ProxyServiceYamlModel.class, Tag.MAP);
        representer.addClassTag(ProxyLayerYamlModel.class, Tag.MAP);
        representer.addClassTag(ProxySourceYamlModel.class, Tag.MAP);
        representer.addClassTag(ProxyCacheYamlModel.class, Tag.MAP);
        representer.addClassTag(CacheYamlModel.class, Tag.MAP);
        representer.addClassTag(RequestYamlModel.class, Tag.MAP);
        representer.addClassTag(MapServerYamlModel.class, Tag.MAP);

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.AUTO); // 이를 BLOCK 으로 설정하면, 배열 field 가 (- a) 같이 나오고, AUTO 로 설정하면 배열 field 가 배열로 나오지만 object 가 SET 문자열로 나옴.

        File directory = new File(dataPath + workDir);
        if(!directory.exists()) directory.mkdirs();

        File file = new File(dataPath + workDir + fileName);
        if(!file.exists()) file.createNewFile();

        Yaml yaml = new Yaml(representer, options);
        StringWriter yamlStr = new StringWriter();
        yaml.dump(model, yamlStr);
        FileUtils.write(new File(dataPath,workDir + fileName), yamlStr.toString(),"utf-8");
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
