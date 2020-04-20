package com.jiin.admin.website.gis;

import com.jiin.admin.entity.ProxyCacheEntity;
import com.jiin.admin.entity.ProxyLayerEntity;
import com.jiin.admin.entity.ProxySourceEntity;
import com.jiin.admin.website.model.ProxyCacheModel;
import com.jiin.admin.website.model.ProxyLayerModel;
import com.jiin.admin.website.model.ProxySelectModel;
import com.jiin.admin.website.model.ProxySourceModel;
import com.jiin.admin.website.server.mapper.CheckMapper;
import com.jiin.admin.website.view.mapper.ProxyMapper;
import com.jiin.admin.website.view.repository.ProxyCacheEntityRepository;
import com.jiin.admin.website.view.repository.ProxyLayerEntityRepository;
import com.jiin.admin.website.view.repository.ProxySourceEntityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProxySettingServiceImpl implements ProxySettingService {
    @Value("${project.mapserver.binary}")
    private String mapServerBinary;

    @Value("${project.data-path}")
    private String dataPath;

    @Resource
    private ProxyMapper proxyMapper;

    @Resource
    private CheckMapper checkMapper;

    @Resource
    private ProxyLayerEntityRepository proxyLayerEntityRepository;

    @Resource
    private ProxySourceEntityRepository proxySourceEntityRepository;

    @Resource
    private ProxyCacheEntityRepository proxyCacheEntityRepository;

    @Resource
    private MapProxyYamlComponent mapProxyYamlComponent;

//    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
//        Map<Object, Boolean> map = new ConcurrentHashMap<>();
//        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
//    }

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

    private void insertLayerAndCacheRelation(long layerId, List<String> caches){
        for(String cacheKey : caches){
            Map<String, String> searchCacheMap = new HashMap<String, String>() {{
                put("name", cacheKey);
            }};
            ProxyCacheEntity cacheEntity = proxyMapper.findProxyCacheEntityWithName(searchCacheMap);
            Map<String, Long> insertMap = new HashMap<String, Long>() {{
                put("layerId", layerId);
                put("cacheId", cacheEntity.getId());
            }};
            proxyMapper.insertProxyLayerCacheRelationWithMap(insertMap);
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

    @Override // TODO : JPA vs MyBatis Persistence
    public Map<String, Object> getProxyLayerEntities() {
//        Map<Long, List<ProxySourceDTO>> sourceMap = new HashMap<>();
//        List<ProxyLayerDTO> layers = proxyMapper.findAllProxyLayerDTOs();
//
//        for(ProxyLayerDTO layer : layers) {
//            long id = layer.getId();
//            List<ProxySourceDTO> list = sourceMap.getOrDefault(id, new ArrayList<>());
//            for (ProxySourceDTO source : layer.getSources()) {
//                list.add(source);
//            }
//            sourceMap.put(id, list);
//        }
//
//        layers = layers.stream()
//                .filter(distinctByKey(c -> c.getId()))
//                .collect(Collectors.toList());
//
//        layers.stream().forEach(c -> c.setSources(sourceMap.get(c.getId())));

        Map<String, Object> result = new HashMap<>();
        result.put("data", proxyLayerEntityRepository.findAll());

        return result;
    }

    @Override
    public Map<String, Object> getProxySourceEntities() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", proxyMapper.findAllProxySourceEntities());
        return result;
    }

    @Override // TODO : JPA vs MyBatis Persistence
    public Map<String, Object> getProxyCacheEntities() {
//        Map<Long, List<ProxySourceDTO>> sourceMap = new HashMap<>();
//        List<ProxyCacheDTO> caches = proxyMapper.findAllProxyCacheDTOs();
//
//        for(ProxyCacheDTO cache : caches) {
//            long id = cache.getId();
//            List<ProxySourceDTO> list = sourceMap.getOrDefault(id, new ArrayList<>());
//            for (ProxySourceDTO source : cache.getSources()) {
//                list.add(source);
//            }
//            sourceMap.put(id, list);
//        }
//
//        caches = caches.stream()
//                    .filter(distinctByKey(c -> c.getId()))
//                    .collect(Collectors.toList());
//
//        caches.stream().forEach(c -> c.setSources(sourceMap.get(c.getId())));

        Map<String, Object> result = new HashMap<>();
        result.put("data", proxyCacheEntityRepository.findAll());

        return result;
    }

    @Override
    public Map<String, Object> getProxyLayerEntitiesIsSelected() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", proxyLayerEntityRepository.findBySelectedIsTrue());

        return result;
    }

    @Override
    public Map<String, Object> getProxySourceEntitiesIsSelected() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", proxySourceEntityRepository.findBySelectedIsTrue());

        return result;
    }

    @Override
    public Map<String, Object> getProxyCacheEntitiesIsSelected() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", proxyCacheEntityRepository.findBySelectedIsTrue());

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
    public ProxyLayerModel initializeProxyLayerModel() {
        return new ProxyLayerModel(0L, "", "", new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public ProxySourceModel initializeProxySourceModel() {
        return new ProxySourceModel(0L, "", "mapserver", "[none]", "[none]", mapServerBinary, dataPath);
    }

    @Override
    public ProxyCacheModel initializeProxyCacheModel() {
        return new ProxyCacheModel(0L, "", "file", dataPath, 0, 0, 0, new ArrayList<>());
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
            insertLayerAndCacheRelation(entity.getId(), proxyLayerModel.getProxyCaches());
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
            proxyMapper.deleteProxyLayerSourceRelationByLayerId(proxyLayerModel.getId());
            proxyMapper.deleteProxyLayerCacheRelationByLayerId(proxyLayerModel.getId());
            proxyMapper.updateProxyLayerWithModel(proxyLayerModel);
            insertLayerAndSourceRelation(proxyLayerModel.getId(), proxyLayerModel.getProxySources());
            insertLayerAndCacheRelation(proxyLayerModel.getId(), proxyLayerModel.getProxyCaches());
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
            proxyMapper.deleteProxyCacheSourceRelationByCacheId(proxyCacheModel.getId());
            proxyMapper.updateProxyCacheWithModel(proxyCacheModel);
            insertCacheAndSourceRelation(proxyCacheModel.getId(), proxyCacheModel.getProxySourcesWithCaches());
            return true;
        } else return false;
    }

    @Override
    @Transactional
    public void deleteProxyLayerEntityById(long id) {
        proxyMapper.deleteProxyLayerSourceRelationByLayerId(id);
        proxyMapper.deleteProxyLayerCacheRelationByLayerId(id);
        proxyMapper.deleteProxyLayerById(id);
    }

    @Override
    @Transactional
    public void deleteProxySourceEntityById(long id) {
        proxyMapper.deleteProxyLayerSourceRelationBySourceId(id);
        proxyMapper.deleteProxyCacheSourceRelationBySourceId(id);
        proxyMapper.deleteProxySourceById(id);
    }

    @Override
    @Transactional
    public void deleteProxyCacheEntityById(long id) {
        proxyMapper.deleteProxyCacheSourceRelationByCacheId(id);
        proxyMapper.deleteProxyLayerCacheRelationByCacheId(id);
        proxyMapper.deleteProxyCacheById(id);
    }

    @Override
    @Transactional
    public void checkProxyDataSettingsWithModel(ProxySelectModel proxySelectModel) throws IOException {
        List<String> tables = Arrays.asList(
            ProxyLayerEntity.class.getAnnotation(Entity.class).name(),
            ProxySourceEntity.class.getAnnotation(Entity.class).name(),
            ProxyCacheEntity.class.getAnnotation(Entity.class).name()
        );

        proxyMapper.updateProxyEntitySelected(tables.get(0), false);
        proxyMapper.updateProxyEntitySelected(tables.get(1), false);
        proxyMapper.updateProxyEntitySelected(tables.get(2), false);

        // TODO : 수정 및 삭제 진행 시, 관계 수정 및 삭제 기능은 조금 고려해 볼 것...
        // WANTED : Entity 에서 가진 관계 : 선택 데이터에 따라 유무 판단 필요. JPA Persistance Ref.
        for(String layer : proxySelectModel.getLayers()){
            proxyMapper.updateProxyEntitySelectedByName(tables.get(0), true, layer);

//            ProxyLayerEntity layerEntity = proxyLayerEntityRepository.findByName(layer).orElse(null);
//            if(layerEntity != null) {
//                layerEntity.getSources().stream()
//                        .filter(o -> !proxySelectModel.getSources().contains(o))
//                        .map(o -> o.getSource())
//                        .forEach(source -> proxyMapper.deleteProxyLayerSourceRelationWithBothIds(layerEntity.getId(), source.getId()));
//
//                layerEntity.getCaches().stream()
//                        .filter(o -> !proxySelectModel.getCaches().contains(o))
//                        .map(o -> o.getCache())
//                        .forEach(cache -> proxyMapper.deleteProxyLayerCacheRelationWithBothIds(layerEntity.getId(), cache.getId()));
//            }
        }

        for(String source : proxySelectModel.getSources()){
            proxyMapper.updateProxyEntitySelectedByName(tables.get(1), true, source);
        }

        for(String cache : proxySelectModel.getCaches()){
            proxyMapper.updateProxyEntitySelectedByName(tables.get(2), true, cache);

//            ProxyCacheEntity cacheEntity = proxyCacheEntityRepository.findByName(cache).orElse(null);
//            if(cacheEntity != null){
//                cacheEntity.getSources().stream()
//                        .filter(o -> !proxySelectModel.getSources().contains(o))
//                        .map(o -> o.getSource())
//                        .forEach(source -> proxyMapper.deleteProxyCacheSourceRelationWithBothIds(cacheEntity.getId(), source.getId()));
//            }
        }

        mapProxyYamlComponent.writeProxyYamlFileWithSettingData(
            proxyLayerEntityRepository.findByNameIn(proxySelectModel.getLayers()),
            proxySourceEntityRepository.findByNameIn(proxySelectModel.getSources()),
            proxyCacheEntityRepository.findByNameIn(proxySelectModel.getCaches())
        );

//        // 모든 설정 선택 데이터들을 false 처리.
//        proxyMapper.updateProxyEntitySelected(tables.get(0), false);
//        proxyMapper.updateProxyEntitySelected(tables.get(1), false);
//        proxyMapper.updateProxyEntitySelected(tables.get(2), false);
//
//        // 선택된 layers, sources, caches 들을 true 처리.
//        for(String layer : proxySelectModel.getLayers()){
//            // Layer 관계 삭제
//            proxyMapper.updateProxyEntitySelectedByName(tables.get(0), true, layer);
//            ProxyLayerEntity layerEntity = proxyLayerEntityRepository.findByName(layer).orElse(null);
//            if(layerEntity != null){
//                // 새로운 있는 관계 추가 : LAYER - SOURCE
//                layerEntity.getSources().stream()
//                    .filter(o -> o.getSource() != null)
//                    .map(o -> o.getSource())
//                    .filter(o -> !proxySelectModel.getSources().contains(o.getName()))
//                    .collect(Collectors.toList()).forEach(source -> proxyMapper.insertProxyLayerSourceRelationWithMap(new HashMap<String, Long>(){{
//                        put("layerId", layerEntity.getId());
//                        put("sourceId", source.getId());
//                    }}));
//
//                // 해당 없는 관계 제거 : LAYER - SOURCE
//                layerEntity.getSources().stream()
//                        .filter(o -> !proxySelectModel.getSources().contains(o))
//                        .map(o -> o.getSource())
//                        .forEach(source -> proxyMapper.deleteProxyLayerSourceRelationWithBothIds(layerEntity.getId(), source.getId()));
//
//                // 새로운 있는 관계 추가 : LAYER - CACHE
//                layerEntity.getCaches().stream()
//                        .filter(o -> o.getCache() != null)
//                        .map(o -> o.getCache())
//                        .filter(o -> !proxySelectModel.getCaches().contains(o.getName()))
//                        .collect(Collectors.toList()).forEach(cache -> proxyMapper.insertProxyLayerCacheRelationWithMap(new HashMap<String, Long>(){{
//                            put("layerId", layerEntity.getId());
//                            put("cacheId", cache.getId());
//                        }}));
//
//                // 해당 없는 관계 제거 : LAYER - CACHE
//                layerEntity.getCaches().stream()
//                        .filter(o -> !proxySelectModel.getCaches().contains(o))
//                        .map(o -> o.getCache())
//                        .forEach(cache -> proxyMapper.deleteProxyLayerCacheRelationWithBothIds(layerEntity.getId(), cache.getId()));
//            }
//        }
//
//        for(String source : proxySelectModel.getSources()){
//            proxyMapper.updateProxyEntitySelectedByName(tables.get(1), true, source);
//        }
//
//        for(String cache : proxySelectModel.getCaches()){
//            proxyMapper.updateProxyEntitySelectedByName(tables.get(2), true, cache);
//            ProxyCacheEntity cacheEntity = proxyCacheEntityRepository.findByName(cache).orElse(null);
//            if(cacheEntity != null){
//                // 새로운 있는 관계 추가 : LAYER - SOURCE
//                cacheEntity.getSources().stream()
//                        .filter(o -> o.getSource() != null)
//                        .map(o -> o.getSource())
//                        .filter(o -> !proxySelectModel.getSources().contains(o.getName()))
//                        .collect(Collectors.toList()).forEach(source -> proxyMapper.insertProxyCacheSourceRelationWithMap(new HashMap<String, Long>(){{
//                            put("cacheId", cacheEntity.getId());
//                            put("sourceId", source.getId());
//                        }}));
//
//                // 해당 없는 관계 제거 : LAYER - SOURCE
//                cacheEntity.getSources().stream()
//                        .filter(o -> !proxySelectModel.getSources().contains(o))
//                        .map(o -> o.getSource())
//                        .forEach(source -> proxyMapper.deleteProxyCacheSourceRelationWithBothIds(cacheEntity.getId(), source.getId()));
//            }
//        }
//
//        mapProxyYamlComponent.writeProxyYamlFileWithSettingData(
//            proxyLayerEntityRepository.findByNameIn(proxySelectModel.getLayers()),
//            proxySourceEntityRepository.findByNameIn(proxySelectModel.getSources()),
//            proxyCacheEntityRepository.findByNameIn(proxySelectModel.getCaches())
//        );
    }
}
