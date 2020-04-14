package com.jiin.admin.website.gis;

import com.jiin.admin.dto.ProxyCacheDTO;
import com.jiin.admin.dto.ProxyLayerDTO;
import com.jiin.admin.dto.ProxySourceDTO;
import com.jiin.admin.entity.ProxyCacheEntity;
import com.jiin.admin.entity.ProxyLayerEntity;
import com.jiin.admin.entity.ProxySourceEntity;
import com.jiin.admin.website.model.ProxyCacheModel;
import com.jiin.admin.website.model.ProxyLayerModel;
import com.jiin.admin.website.model.ProxySelectModel;
import com.jiin.admin.website.model.ProxySourceModel;
import com.jiin.admin.website.server.mapper.CheckMapper;
import com.jiin.admin.website.util.MapProxyUtil;
import com.jiin.admin.website.view.mapper.ProxyMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ProxySettingServiceImpl implements ProxySettingService {
    @Resource
    private ProxyMapper proxyMapper;

    @Resource
    private CheckMapper checkMapper;

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private void insertLayerAndSourceRelation(long layerId, List<String> sources){
        for(String sourceKey : sources){
            Map<String, String> searchSourceMap = new HashMap<String, String>() {{
                put("name", sourceKey);
            }};
            ProxySourceEntity sourceEntity = proxyMapper.findProxySourceEntityWithName(searchSourceMap);
            Map<String, Long> insertMap = new HashMap<String, Long>() {{
                put("layerId", layerId);
                put("sourceId", sourceEntity.getId());
            }};
            proxyMapper.insertProxyLayerSourceRelationWithMap(insertMap);
        }
    }

    private void insertCacheAndSourceRelation(long cacheId, List<String> sources){
        for(String sourceKey : sources){
            Map<String, String> searchSourceMap = new HashMap<String, String>() {{
                put("name", sourceKey);
            }};
            ProxySourceEntity sourceEntity = proxyMapper.findProxySourceEntityWithName(searchSourceMap);
            Map<String, Long> insertMap = new HashMap<String, Long>() {{
                put("cacheId", cacheId);
                put("sourceId", sourceEntity.getId());
            }};
            proxyMapper.insertProxyCacheSourceRelationWithMap(insertMap);
        }
    }

    @Override // TODO : DB 관계 쿼리 최적화 작업 필요
    public Map<String, Object> getProxyLayerEntities() {
        Map<Long, List<ProxySourceDTO>> sourceMap = new HashMap<>();
        List<ProxyLayerDTO> layers = proxyMapper.findAllProxyLayerDTOs();

        for(ProxyLayerDTO layer : layers) {
            long id = layer.getId();
            List<ProxySourceDTO> list = sourceMap.getOrDefault(id, new ArrayList<>());
            for (ProxySourceDTO source : layer.getSources()) {
                list.add(source);
            }
            sourceMap.put(id, list);
        }

        layers = layers.stream()
                .filter(distinctByKey(c -> c.getId()))
                .collect(Collectors.toList());

        layers.stream().forEach(c -> c.setSources(sourceMap.get(c.getId())));

        Map<String, Object> result = new HashMap<>();
        result.put("data", layers);

        return result;
    }

    @Override
    public Map<String, Object> getProxySourceEntities() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", proxyMapper.findAllProxySourceEntities());
        return result;
    }

    @Override // TODO : DB 관계 쿼리 최적화 작업 필요
    public Map<String, Object> getProxyCacheEntities() {
        Map<Long, List<ProxySourceDTO>> sourceMap = new HashMap<>();
        List<ProxyCacheDTO> caches = proxyMapper.findAllProxyCacheDTOs();

        for(ProxyCacheDTO cache : caches) {
            long id = cache.getId();
            List<ProxySourceDTO> list = sourceMap.getOrDefault(id, new ArrayList<>());
            for (ProxySourceDTO source : cache.getSources()) {
                list.add(source);
            }
            sourceMap.put(id, list);
        }

        caches = caches.stream()
                    .filter(distinctByKey(c -> c.getId()))
                    .collect(Collectors.toList());

        caches.stream().forEach(c -> c.setSources(sourceMap.get(c.getId())));

        Map<String, Object> result = new HashMap<>();
        result.put("data", caches);

        return result;
    }

    @Override
    public ProxySelectModel getCurrentMapProxySettings() {
        return new ProxySelectModel(
            proxyMapper.findProxyLayerSelectedEntities().stream().map(o -> o.getName()).collect(Collectors.toList()),
            proxyMapper.findProxySourceSelectedEntities().stream().map(o -> o.getName()).collect(Collectors.toList()),
            proxyMapper.findProxyCacheSelectedEntities().stream().map(o -> o.getName()).collect(Collectors.toList())
        );
    }

    @Override
    public Map<String, Object> getCachedRequestData() {
        Map<String, Object> capability = MapProxyUtil.getCapabilities();
        Map<String, Object> wmsCapability = (Map<String, Object>) capability.get("WMS_Capabilities");
        Map<String, Object> mainRequest = (Map<String, Object>) wmsCapability.get("Capability");

        return (Map<String, Object>) mainRequest.get("Request");
    }

    @Override
    public Map<String, Object> getCachedLayerData() {
        Map<String, Object> capability = MapProxyUtil.getCapabilities();
        Map<String, Object> wmsCapability = (Map<String, Object>) capability.get("WMS_Capabilities");
        Map<String, Object> mainCapability = (Map<String, Object>) wmsCapability.get("Capability");

        return (Map<String, Object>) mainCapability.get("Layer");
    }

    @Override
    public Map<String, Object> getBoundingBoxInfoWithCrs() {
        Map<String, Object> capability = MapProxyUtil.getCapabilities();
        Map<String, Object> wmsCapability = (Map<String, Object>) capability.get("WMS_Capabilities");
        Map<String, Object> mainCapability = (Map<String, Object>) wmsCapability.get("Capability");
        Map<String, Object> layerData = (Map<String, Object>) mainCapability.get("Layer");

        List<Map<String, Object>> baseBoundingBoxList = (ArrayList<Map<String, Object>>) layerData.get("BoundingBox");
        Map<String, Object> boundingBoxMap = new HashMap<>();
        for(Map<String, Object> crs : baseBoundingBoxList){
            if(crs.keySet().contains("CRS"))
                boundingBoxMap.put((String) crs.get("CRS"), crs);
        }
        return boundingBoxMap;
    }

    @Override
    @Transactional
    public boolean createProxyLayerEntityWithModel(ProxyLayerModel proxyLayerModel) {
        int count = checkMapper.countDuplicate(ProxyLayerEntity.class.getAnnotation(Entity.class).name(), proxyLayerModel.getProxyLayerName());
        if(count > 0) {
            return false;
        } else {
            proxyMapper.insertProxyLayerWithModel(proxyLayerModel);
            ProxyLayerEntity entity = proxyMapper.findRecentlyInsertingProxyLayerEntity();
            insertLayerAndSourceRelation(entity.getId(), proxyLayerModel.getProxySources());
            return true;
        }
    }

    @Override
    @Transactional
    public boolean createProxySourceEntityWithModel(ProxySourceModel proxySourceModel) {
        int count = checkMapper.countDuplicate(ProxySourceEntity.class.getAnnotation(Entity.class).name(), proxySourceModel.getProxySourceName());
        if(count > 0) {
            return false;
        } else {
            proxyMapper.insertProxySourceWithModel(proxySourceModel);
            return true;
        }
    }

    @Override
    @Transactional
    public boolean createProxyCacheEntityWithModel(ProxyCacheModel proxyCacheModel) {
        int count = checkMapper.countDuplicate(ProxyCacheEntity.class.getAnnotation(Entity.class).name(), proxyCacheModel.getProxyCacheName());
        if(count > 0) {
            return false;
        } else {
            proxyMapper.insertProxyCacheWithModel(proxyCacheModel);
            ProxyCacheEntity entity = proxyMapper.findRecentlyInsertingProxyCacheEntity();
            insertCacheAndSourceRelation(entity.getId(), proxyCacheModel.getProxySourcesWithCaches());
            return true;
        }
    }

    @Override
    @Transactional
    public boolean updateProxyLayerEntityWithModel(ProxyLayerModel proxyLayerModel) {
        int count = checkMapper.countDuplicate(ProxyLayerEntity.class.getAnnotation(Entity.class).name(), proxyLayerModel.getProxyLayerName());
        if(count > 0){
            Map<String, Long> deleteMap = new HashMap<String, Long>() {{
                put("id", proxyLayerModel.getId());
            }};
            proxyMapper.deleteProxyLayerSourceRelationByLayerId(deleteMap);
            proxyMapper.updateProxyLayerWithModel(proxyLayerModel);
            insertLayerAndSourceRelation(proxyLayerModel.getId(), proxyLayerModel.getProxySources());
            return true;
        } else return false;
    }

    @Override
    public boolean updateProxySourceEntityWithModel(ProxySourceModel proxySourceModel) {
        int count = checkMapper.countDuplicate(ProxySourceEntity.class.getAnnotation(Entity.class).name(), proxySourceModel.getProxySourceName());
        if(count > 0){
            proxyMapper.updateProxySourceWithModel(proxySourceModel);
            return true;
        } else return false;
    }

    @Override
    @Transactional
    public boolean updateProxyCacheEntityWithModel(ProxyCacheModel proxyCacheModel) {
        int count = checkMapper.countDuplicate(ProxyCacheEntity.class.getAnnotation(Entity.class).name(), proxyCacheModel.getProxyCacheName());
        if(count > 0){
            Map<String, Long> deleteMap = new HashMap<String, Long>() {{
                put("id", proxyCacheModel.getId());
            }};
            proxyMapper.deleteProxyCacheSourceRelationByCacheId(deleteMap);
            proxyMapper.updateProxyCacheWithModel(proxyCacheModel);
            insertCacheAndSourceRelation(proxyCacheModel.getId(), proxyCacheModel.getProxySourcesWithCaches());
            return true;
        } else return false;
    }

    @Override
    @Transactional
    public void deleteProxyLayerEntityById(long id) {
        Map<String, Long> searchMap = new HashMap<String, Long>() {{
            put("id", id);
        }};
        proxyMapper.deleteProxyLayerSourceRelationByLayerId(searchMap);
        proxyMapper.deleteProxyLayerById(searchMap);
    }

    @Override
    @Transactional
    public void deleteProxySourceEntityById(long id) {
        Map<String, Long> searchMap = new HashMap<String, Long>() {{
            put("id", id);
        }};
        proxyMapper.deleteProxyLayerSourceRelationBySourceId(searchMap);
        proxyMapper.deleteProxyCacheSourceRelationBySourceId(searchMap);
        proxyMapper.deleteProxySourceById(searchMap);
    }

    @Override
    @Transactional
    public void deleteProxyCacheEntityById(long id) {
        Map<String, Long> searchMap = new HashMap<String, Long>() {{
            put("id", id);
        }};
        proxyMapper.deleteProxyCacheSourceRelationByCacheId(searchMap);
        proxyMapper.deleteProxyCacheById(searchMap);
    }

    @Override
    @Transactional
    public void checkProxyDataSettingsWithModel(ProxySelectModel proxySelectModel) {
        List<String> tables = Arrays.asList(
            ProxyLayerEntity.class.getAnnotation(Entity.class).name(),
            ProxySourceEntity.class.getAnnotation(Entity.class).name(),
            ProxyCacheEntity.class.getAnnotation(Entity.class).name()
        );

        proxyMapper.updateProxyEntitySelected(tables.get(0), false);
        proxyMapper.updateProxyEntitySelected(tables.get(1), false);
        proxyMapper.updateProxyEntitySelected(tables.get(2), false);

        for(String layer : proxySelectModel.getLayers()){
            proxyMapper.updateProxyEntitySelectedByName(tables.get(0), true, layer);
        }

        for(String source : proxySelectModel.getSources()){
            proxyMapper.updateProxyEntitySelectedByName(tables.get(1), true, source);
        }

        for(String cache : proxySelectModel.getCaches()){
            proxyMapper.updateProxyEntitySelectedByName(tables.get(2), true, cache);
        }
    }
}