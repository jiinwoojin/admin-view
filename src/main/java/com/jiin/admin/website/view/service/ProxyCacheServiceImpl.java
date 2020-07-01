package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.*;
import com.jiin.admin.mapper.data.*;
import com.jiin.admin.website.model.*;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.MapProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ProxyCacheServiceImpl implements ProxyCacheService {
    @Value("${project.mapserver.binary}")
    private String mapServerBinary;

    @Value("${project.data-path}")
    private String dataPath;

    @Value("classpath:data/default-map-proxy.yaml")
    private File defaultMapProxy;

    @Resource
    private ProxyLayerMapper proxyLayerMapper;

    @Resource
    private ProxySourceMapper proxySourceMapper;

    @Resource
    private ProxyCacheMapper proxyCacheMapper;

    @Resource
    private ProxyGlobalMapper proxyGlobalMapper;

    @Resource
    private ProxyLayerSourceRelationMapper proxyLayerSourceRelationMapper;

    @Resource
    private ProxyLayerCacheRelationMapper proxyLayerCacheRelationMapper;

    @Resource
    private ProxyCacheSourceRelationMapper proxyCacheSourceRelationMapper;

    /**
     * YAML 파일 내용을 채취한 이후, 해당 디렉토리에 저장힌다.
     */
    private boolean saveYAMLFileByEachList(){
        List<ProxySourceMapServerDTO> mapServerDTOs = proxySourceMapper.findBySelectedMapServer(true);
        List<ProxySourceWMSDTO> wmsDTOs = proxySourceMapper.findBySelectedWMS(true);
        List<Object> dtos = Stream.of(mapServerDTOs, wmsDTOs).flatMap(o -> o.stream()).collect(Collectors.toList());

        String context = MapProxyUtil.fetchYamlFileContextWithDTO(proxyLayerMapper.findBySelected(true), dtos, proxyCacheMapper.findBySelected(true));
        FileSystemUtil.createAtFile(dataPath + Constants.PROXY_SETTING_FILE_PATH + "/" + Constants.PROXY_SETTING_FILE_NAME, context);
        return true;
    }

    /**
     * 각 해당하는 Relation 을 생성한다.
     * @param mainType String, subType String, mainId long, subNames List : String
     */
    private boolean createRelationWithMainIdAndSubNames(String mainType, String subType, long mainId, List<String> subNames){
        int cnt = subNames.size();
        if(mainType.equals("LAYER") && subType.equals("SOURCE")){
            proxyLayerSourceRelationMapper.deleteByLayerId(mainId);
            for(String key : subNames) {
                ProxySourceDTO subData = proxySourceMapper.findByName(key);
                if (subData != null) {
                    cnt -= proxyLayerSourceRelationMapper.insertByRelationModel(new RelationModel(0L, mainId, subData.getId()));
                }
            }
        } else if(mainType.equals("LAYER") && subType.equals("CACHE")){
            proxyLayerCacheRelationMapper.deleteByLayerId(mainId);
            for(String key : subNames) {
                ProxyCacheDTO subData = proxyCacheMapper.findByName(key);
                if (subData != null) {
                    cnt -= proxyLayerCacheRelationMapper.insertByRelationModel(new RelationModel(0L, mainId, subData.getId()));
                }
            }
        } else if(mainType.equals("CACHE") && subType.equals("SOURCE")){
            proxyCacheSourceRelationMapper.deleteByCacheId(mainId);
            for(String key : subNames) {
                ProxySourceDTO subData = proxySourceMapper.findByName(key);
                if (subData != null) {
                    cnt -= proxyCacheSourceRelationMapper.insertByRelationModel(new RelationModel(0L, mainId, subData.getId()));
                }
            }
        } else {
            return false;
        }

        return cnt == 0;
    }

    @Override
    public String loadDataDir() {
        return dataPath;
    }

    @Override
    public String loadProxyCacheMainDir() {
        return dataPath + Constants.PROXY_CACHE_DIRECTORY + "/";
    }

    @Override
    public String loadMapServerBinary() {
        return mapServerBinary;
    }

    @Override
    public String loadProxyYamlSetting() {
        try {
            return FileSystemUtil.fetchFileContext(dataPath + Constants.PROXY_SETTING_FILE_PATH + "/" + Constants.PROXY_SETTING_FILE_NAME);
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
            return "text";
        }
    }

    /**
     * 각 캐시 정보 목록들을 불러온다.
     * @param type String
     */
    @Override
    public Object loadDataList(String type) {
        switch(type){
            case "LAYERS" :
                return proxyLayerMapper.findAll();
            case "SOURCES" :
                List<ProxySourceMapServerDTO> mapServerDTOs = proxySourceMapper.findAllMapServer();
                List<ProxySourceWMSDTO> wmsDTOs = proxySourceMapper.findAllWMS();
                return Stream.of(mapServerDTOs, wmsDTOs).flatMap(o -> o.stream()).collect(Collectors.toList());
            case "SOURCES_MAPSERVER" :
                return proxySourceMapper.findAllMapServer();
            case "SOURCES_WMS" :
                return proxySourceMapper.findAllWMS();
            case "CACHES" :
                return proxyCacheMapper.findAll();
            case "GLOBALS" :
                return proxyGlobalMapper.findAll();
            default :
                return new ArrayList<>();
        }
    }

    /**
     * 각 캐시 정보 목록 중 설정에 선택 or 미선택 된 목록들을 불러온다.
     * @param type String
     */
    @Override
    public Object loadDataListBySelected(String type, Boolean selected) {
        switch(type){
            case "LAYERS" :
                return proxyLayerMapper.findBySelected(selected);
            case "SOURCES" :
                return proxySourceMapper.findBySelected(selected);
            case "CACHES" :
                return proxyCacheMapper.findBySelected(selected);
            default :
                return new ArrayList<>();
        }
    }

    /**
     * 현재 MapProxy 의 yaml 설정 파일에 선택된 데이터 목록을 호출한다.
     */
    @Override
    public ProxySelectResponseModel loadProxySetting() {
        return new ProxySelectResponseModel(
            proxyLayerMapper.findBySelected(true).stream().map(o -> new HashMap<String, Object>(){{
                put("layer", o.getName());
                put("sources", o.getSources().stream().map(ProxySourceDTO::getName).collect(Collectors.toList()));
                put("caches", o.getCaches().stream().map(ProxyCacheDTO::getName).collect(Collectors.toList()));
            }}).collect(Collectors.toList()),
            proxySourceMapper.findBySelected(true).stream().map(ProxySourceDTO::getName).collect(Collectors.toList()),
            proxyCacheMapper.findBySelected(true).stream().map(o -> new HashMap<String, Object>(){{
                put("cache", o.getName());
                put("sources", o.getSources().stream().map(ProxySourceDTO::getName).collect(Collectors.toList()));
            }}).collect(Collectors.toList())
        );
    }

    /**
     * MapProxy 기반 Layer 정보를 저장한다.
     * @param proxyLayerModel ProxyLayerModel
     */
    @Override
    @Transactional
    public boolean saveProxyLayerByModel(ProxyLayerModel proxyLayerModel) {
        ProxyLayerDTO layer = proxyLayerMapper.findByName(proxyLayerModel.getName());
        ProxyLayerDTO dto;
        switch(proxyLayerModel.getMethod()){
            case "INSERT" :
                if(layer != null) return false;
                long nextIdx = proxyLayerMapper.findNextSeqVal();
                proxyLayerModel.setId(nextIdx);
                dto = ProxyLayerModel.convertDTO(proxyLayerModel);
                dto.setIsDefault(false);
                dto.setSelected(false);
                return proxyLayerMapper.insert(dto) > 0 && createRelationWithMainIdAndSubNames("LAYER", "SOURCE", nextIdx, proxyLayerModel.getSources()) && createRelationWithMainIdAndSubNames("LAYER", "CACHE", nextIdx, proxyLayerModel.getCaches()) && saveYAMLFileByEachList();
            case "UPDATE" :
                if(layer == null) return false;
                dto = ProxyLayerModel.convertDTO(proxyLayerModel);
                dto.setIsDefault(layer.getIsDefault());
                dto.setSelected(layer.getSelected());
                return proxyLayerMapper.update(dto) > 0 && createRelationWithMainIdAndSubNames("LAYER", "SOURCE", proxyLayerModel.getId(), proxyLayerModel.getSources()) && createRelationWithMainIdAndSubNames("LAYER", "CACHE", proxyLayerModel.getId(), proxyLayerModel.getCaches()) && saveYAMLFileByEachList();
            default :
                return false;
        }
    }

    /**
     * MapProxy 기반 Source (MapServer based) 정보를 저장한다.
     * @param proxySourceMapServerModel ProxySourceMapServerModel
     */
    @Override
    @Transactional
    public boolean saveProxySourceMapServerByModel(ProxySourceMapServerModel proxySourceMapServerModel) {
        ProxySourceDTO root = proxySourceMapper.findByName(proxySourceMapServerModel.getName());
        ProxySourceMapServerDTO dto;
        switch(proxySourceMapServerModel.getMethod()){
            case "INSERT" :
                if(root != null) return false;
                long nextIdx = proxySourceMapper.findNextSeqVal();
                proxySourceMapServerModel.setId(nextIdx);
                dto = ProxySourceMapServerModel.convertDTO(proxySourceMapServerModel);
                dto.setIsDefault(false);
                dto.setSelected(false);
                return proxySourceMapper.insert(dto) > 0 && proxySourceMapper.insertMapServer(dto) > 0 && saveYAMLFileByEachList();
            case "UPDATE" :
                if(root == null) return false;
                dto = ProxySourceMapServerModel.convertDTO(proxySourceMapServerModel);
                dto.setIsDefault(root.getIsDefault());
                dto.setSelected(root.getSelected());
                return proxySourceMapper.update(dto) > 0 && proxySourceMapper.updateMapServer(dto) > 0 && saveYAMLFileByEachList();
            default :
                return false;
        }
    }

    /**
     * MapProxy 기반 Source (WMS based) 정보를 저장한다.
     * @param proxySourceWMSModel ProxySourceWMSModel
     */
    @Override
    @Transactional
    public boolean saveProxySourceWMSByModel(ProxySourceWMSModel proxySourceWMSModel) {
        ProxySourceDTO root = proxySourceMapper.findByName(proxySourceWMSModel.getName());
        ProxySourceWMSDTO dto;
        switch(proxySourceWMSModel.getMethod()) {
            case "INSERT" :
                if (root != null) return false;
                long nextIdx = proxySourceMapper.findNextSeqVal();
                proxySourceWMSModel.setId(nextIdx);
                dto = ProxySourceWMSModel.convertDTO(proxySourceWMSModel);
                dto.setIsDefault(false);
                dto.setSelected(false);
                return proxySourceMapper.insert(dto) > 0 && proxySourceMapper.insertWMS(dto) > 0 && saveYAMLFileByEachList();
            case "UPDATE" :
                if (root == null) return false;
                dto = ProxySourceWMSModel.convertDTO(proxySourceWMSModel);
                dto.setIsDefault(root.getIsDefault());
                dto.setSelected(root.getSelected());
                return proxySourceMapper.update(dto) > 0 && proxySourceMapper.updateWMS(dto) > 0 && saveYAMLFileByEachList();
            default :
                return false;
        }
    }

    /**
     * MapProxy 기반 Cache 정보를 저장한다.
     * @param proxyCacheModel ProxyCacheModel
     */
    @Override
    @Transactional
    public boolean saveProxyCacheByModel(ProxyCacheModel proxyCacheModel) {
        ProxyCacheDTO cache = proxyCacheMapper.findByName(proxyCacheModel.getName());
        ProxyCacheDTO dto;
        switch(proxyCacheModel.getMethod()){
            case "INSERT" :
                if(cache != null) return false;
                long nextIdx = proxyCacheMapper.findNextSeqVal();
                proxyCacheModel.setId(nextIdx);
                dto = ProxyCacheModel.convertDTO(proxyCacheModel);
                dto.setIsDefault(false);
                dto.setSelected(false);
                return proxyCacheMapper.insert(dto) > 0 && createRelationWithMainIdAndSubNames("CACHE", "SOURCE", nextIdx, proxyCacheModel.getSources()) && saveYAMLFileByEachList();
            case "UPDATE" :
                if(cache == null) return false;
                dto = ProxyCacheModel.convertDTO(proxyCacheModel);
                dto.setIsDefault(cache.getIsDefault());
                dto.setSelected(cache.getSelected());
                return proxyCacheMapper.update(dto) > 0 && createRelationWithMainIdAndSubNames("CACHE", "SOURCE", proxyCacheModel.getId(), proxyCacheModel.getSources()) && saveYAMLFileByEachList();
            default :
                return false;
        }
    }

    @Override
    @Transactional
    public boolean saveProxyGlobalByModelList(List<ProxyGlobalModel> proxyGlobalModels) {
        int count = 0;
        Set<Long> retainIds = new HashSet<>(); // 이전 목록에서 남길 ID 를 모아둔다.
        for(ProxyGlobalModel model : proxyGlobalModels){
            // Inserting
            if(model.getId().equals(0L)){
                long nextId = proxyGlobalMapper.findNextSeqVal();
                model.setId(nextId);
                retainIds.add(nextId);
                count += proxyGlobalMapper.insert(ProxyGlobalModel.convertDTO(model));
            } else { // Updating
                retainIds.add(model.getId());
                count += proxyGlobalMapper.update(ProxyGlobalModel.convertDTO(model));
            }
        }

        if(retainIds.size() > 0) {
            proxyGlobalMapper.deleteByIdListNotIn(retainIds);
        }

        return count == proxyGlobalModels.size();
    }

    /**
     * Proxy 정보를 Main ID 로 삭제한다.
     * @param id long, type String
     */
    @Override
    @Transactional
    public boolean removeProxyDataByIdAndType(long id, String type) {
        switch(type.toUpperCase()){
            case "LAYER" :
                proxyLayerSourceRelationMapper.deleteByLayerId(id);
                proxyLayerCacheRelationMapper.deleteByLayerId(id);
                proxyLayerMapper.deleteById(id);
                return saveYAMLFileByEachList();

            case "SOURCE-MAPSERVER" :
                proxyLayerSourceRelationMapper.deleteBySourceId(id);
                proxyCacheSourceRelationMapper.deleteBySourceId(id);
                proxySourceMapper.deleteByIdMapServer(id);
                proxySourceMapper.deleteById(id);
                return saveYAMLFileByEachList();

            case "SOURCE-WMS" :
                proxyLayerSourceRelationMapper.deleteBySourceId(id);
                proxyCacheSourceRelationMapper.deleteBySourceId(id);
                proxySourceMapper.deleteByIdWMS(id);
                proxySourceMapper.deleteById(id);
                return saveYAMLFileByEachList();

            case "CACHE" :
                proxyLayerCacheRelationMapper.deleteByCacheId(id);
                proxyCacheSourceRelationMapper.deleteByCacheId(id);
                proxyCacheMapper.deleteById(id);
                return saveYAMLFileByEachList();

            default :
                return false;
        }
    }

    /**
     * 서버에서 Proxy 설정 데이터를 가져와 YAML 파일에 반영한다.
     * @param proxySelectModel ProxySelectModel
     */
    @Override
    public boolean setProxyDataSelectByModel(ProxySelectRequestModel proxySelectModel) {
        proxyLayerMapper.updateSelectedAllDisabled();
        proxySourceMapper.updateSelectedAllDisabled();
        proxyCacheMapper.updateSelectedAllDisabled();

        if(proxySelectModel.getLayers().size() > 0) {
            proxyLayerMapper.updateSelectedByNameIn(proxySelectModel.getLayers(), true);
        }
        if(proxySelectModel.getSources().size() > 0) {
            proxySourceMapper.updateSelectedByNameIn(proxySelectModel.getSources(), true);
        }
        if(proxySelectModel.getCaches().size() > 0) {
            proxyCacheMapper.updateSelectedByNameIn(proxySelectModel.getCaches(), true);
        }

        return saveYAMLFileByEachList();
    }
}
