package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.ProxyCacheDTO;
import com.jiin.admin.dto.ProxySourceDTO;
import com.jiin.admin.mapper.data.*;
import com.jiin.admin.website.model.*;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.MapProxyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProxyCacheServiceImpl implements ProxyCacheService {
    @Value("${project.mapserver.binary}")
    private String mapServerBinary;

    @Value("${project.data-path}")
    private String dataPath;

    @Resource
    private ProxyLayerMapper proxyLayerMapper;

    @Resource
    private ProxySourceMapper proxySourceMapper;

    @Resource
    private ProxyCacheMapper proxyCacheMapper;

    @Resource
    private ProxyLayerSourceRelationMapper proxyLayerSourceRelationMapper;

    @Resource
    private ProxyLayerCacheRelationMapper proxyLayerCacheRelationMapper;

    @Resource
    private ProxyCacheSourceRelationMapper proxyCacheSourceRelationMapper;

    /**
     * YAML 파일 내용을 채취한 이후, 해당 디렉토리에 저장힌다.
     * @param
     */
    private boolean saveYAMLFileByEachList(){
        String context;
        try {
            context = MapProxyUtil.fetchYamlFileContextWithDTO(
                proxyLayerMapper.findAll(), proxySourceMapper.findAll(), proxyCacheMapper.findAll(), dataPath
            );
        } catch (IOException e) {
            log.error("Map Proxy YAML 파일을 생성할 수 없습니다. : DB 내용 오류");
            return false;
        }

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
                return proxySourceMapper.findAll();
            case "CACHES" :
                return proxyCacheMapper.findAll();
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
     * 각 캐시 정보의 Form 을 초기화 한다.
     * @param type String
     */
    @Override
    public Object loadDataModel(String type) {
        switch(type){
            case "LAYERS" :
                return new ProxyLayerModel(0L, "", "", new ArrayList<>(), new ArrayList<>());
            case "SOURCES" :
                return new ProxySourceModel(0L, "", "mapserver", "[none]", "[none]", mapServerBinary, dataPath + "/tmp");
            case "CACHES" :
                return new ProxyCacheModel(0L, "", "file", "", 0, 0, 0, new ArrayList<>());
            default :
                return null;
        }
    }

    /**
     * 현재 MapProxy 의 yaml 설정 파일에 선택된 데이터 목록을 호출한다.
     * @param
     */
    @Override
    public ProxySelectModel loadProxySetting() {
        return new ProxySelectModel(
              proxyLayerMapper.findBySelected(true).stream().map(o -> o.getName()).collect(Collectors.toList()),
              proxySourceMapper.findBySelected(true).stream().map(o -> o.getName()).collect(Collectors.toList()),
              proxyCacheMapper.findBySelected(true).stream().map(o -> o.getName()).collect(Collectors.toList())
        );
    }

    /**
     * MapProxy 기반 Layer 정보를 저장한다.
     * @param proxyLayerModelV2 ProxyLayerModelV2
     */
    @Override
    @Transactional
    public boolean saveProxyLayerByModel(ProxyLayerModelV2 proxyLayerModelV2) {
        switch(proxyLayerModelV2.getMethod()){
            case "INSERT" :
                if(proxyLayerMapper.findByName(proxyLayerModelV2.getName()) != null) return false;
                long nextIdx = proxyLayerMapper.findNextSeqVal();
                proxyLayerModelV2.setId(nextIdx);
                return proxyLayerMapper.insert(ProxyLayerModelV2.convertDTO(proxyLayerModelV2)) > 0 && createRelationWithMainIdAndSubNames("LAYER", "SOURCE", nextIdx, proxyLayerModelV2.getSources()) && createRelationWithMainIdAndSubNames("LAYER", "CACHE", nextIdx, proxyLayerModelV2.getCaches()) && saveYAMLFileByEachList();
            case "UPDATE" :
                if(proxyLayerMapper.findByName(proxyLayerModelV2.getName()) == null) return false;
                return proxyLayerMapper.update(ProxyLayerModelV2.convertDTO(proxyLayerModelV2)) > 0 && createRelationWithMainIdAndSubNames("LAYER", "SOURCE", proxyLayerModelV2.getId(), proxyLayerModelV2.getSources()) && createRelationWithMainIdAndSubNames("LAYER", "CACHE", proxyLayerModelV2.getId(), proxyLayerModelV2.getCaches()) && saveYAMLFileByEachList();
            default :
                return false;
        }
    }

    /**
     * MapProxy 기반 Source 정보를 저장한다.
     * @param proxySourceModelV2 ProxySourceModelV2
     */
    @Override
    @Transactional
    public boolean saveProxySourceByModel(ProxySourceModelV2 proxySourceModelV2) {
        switch(proxySourceModelV2.getMethod()){
            case "INSERT" :
                if(proxySourceMapper.findByName(proxySourceModelV2.getName()) != null) return false;
                long nextIdx = proxySourceMapper.findNextSeqVal();
                proxySourceModelV2.setId(nextIdx);
                return proxySourceMapper.insert(ProxySourceModelV2.convertDTO(proxySourceModelV2)) > 0 && saveYAMLFileByEachList();
            case "UPDATE" :
                if(proxySourceMapper.findByName(proxySourceModelV2.getName()) == null) return false;
                return proxySourceMapper.update(ProxySourceModelV2.convertDTO(proxySourceModelV2)) > 0 && saveYAMLFileByEachList();
            default :
                return false;
        }
    }

    /**
     * MapProxy 기반 Cache 정보를 저장한다.
     * @param proxyCacheModelV2 ProxyCacheModelV2
     */
    @Override
    @Transactional
    public boolean saveProxyCacheByModel(ProxyCacheModelV2 proxyCacheModelV2) {
        switch(proxyCacheModelV2.getMethod()){
            case "INSERT" :
                if(proxyCacheMapper.findByName(proxyCacheModelV2.getName()) != null) return false;
                long nextIdx = proxyCacheMapper.findNextSeqVal();
                proxyCacheModelV2.setId(nextIdx);
                return proxyCacheMapper.insert(ProxyCacheModelV2.convertDTO(proxyCacheModelV2)) > 0 && createRelationWithMainIdAndSubNames("CACHE", "SOURCE", nextIdx, proxyCacheModelV2.getSources()) && saveYAMLFileByEachList();
            case "UPDATE" :
                if(proxyCacheMapper.findByName(proxyCacheModelV2.getName()) == null) return false;
                return proxyCacheMapper.insert(ProxyCacheModelV2.convertDTO(proxyCacheModelV2)) > 0 && createRelationWithMainIdAndSubNames("CACHE", "SOURCE", proxyCacheModelV2.getId(), proxyCacheModelV2.getSources()) && saveYAMLFileByEachList();
            default :
                return false;
        }
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
            case "SOURCE" :
                proxyLayerSourceRelationMapper.deleteBySourceId(id);
                proxyCacheSourceRelationMapper.deleteBySourceId(id);
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
    public boolean setProxyDataSelectByModel(ProxySelectModel proxySelectModel) {
        proxyLayerMapper.updateSelectedAllDisabled();
        proxySourceMapper.updateSelectedAllDisabled();
        proxyCacheMapper.updateSelectedAllDisabled();

        proxyLayerMapper.updateSelectedByNameIn(proxySelectModel.getLayers(), true);
        proxySourceMapper.updateSelectedByNameIn(proxySelectModel.getSources(), true);
        proxyCacheMapper.updateSelectedByNameIn(proxySelectModel.getCaches(), true);

        return true;
    }
}
