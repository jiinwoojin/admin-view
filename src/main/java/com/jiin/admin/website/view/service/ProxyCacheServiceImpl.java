package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.*;
import com.jiin.admin.mapper.data.*;
import com.jiin.admin.servlet.AdminServerServlet;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.*;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.MapProxyUtil;
import com.jiin.admin.website.util.RestClientUtil;
import com.jiin.admin.website.view.component.CascadeRelativeComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ProxyCacheServiceImpl implements ProxyCacheService {
    @Value("${server.servlet.context-path}")
    private String CONTEXT_PATH;

    @Value("${project.mapserver.binary}")
    private String mapServerBinary;

    @Value("${project.data-path}")
    private String dataPath;

    @Value("classpath:data/default-map-proxy.yaml")
    private File defaultMapProxy;

    @Value("${project.server-port.mapserver-port}")
    private int MAP_SERVER_PORT;

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

    @Resource
    private CascadeRelativeComponent cascadeRelativeComponent;

    private int sendYAMLSyncRESTForNeighbor(HttpServletRequest request, List<ServerCenterInfo> neighbors) {
        int success = 0;
        for (ServerCenterInfo neighbor : neighbors) {
            Map<String, Object> map = RestClientUtil.postREST(request.isSecure(), neighbor.getIp(), String.format("%s/%s/api/proxy/sync/yaml-save", CONTEXT_PATH, AdminServerServlet.CONTEXT_PATH), new HashMap<>());
            if (map != null) {
                success += (boolean) map.get("result") ? 1 : 0;
            }
        }
        return success;
    }

    /**
     * 각 해당하는 Relation 을 생성한다.
     * @param mainType String, subType String, mainId long, subNames List : String
     */
    @Transactional
    public boolean createRelationWithMainIdAndSubNames(String mainType, String subType, long mainId, List<String> subNames) {
        int cnt = subNames.size();
        if (mainType.equals("LAYER") && subType.equals("SOURCE")) {
            proxyLayerSourceRelationMapper.deleteByLayerId(mainId);
            for (String key : subNames) {
                ProxySourceDTO subData = proxySourceMapper.findByName(key);
                if (subData != null) {
                    cnt -= proxyLayerSourceRelationMapper.insertByRelationModel(new RelationModel(0L, mainId, subData.getId()));
                }
            }
        } else if (mainType.equals("LAYER") && subType.equals("CACHE")) {
            proxyLayerCacheRelationMapper.deleteByLayerId(mainId);
            for (String key : subNames) {
                ProxyCacheDTO subData = proxyCacheMapper.findByName(key);
                if (subData != null) {
                    cnt -= proxyLayerCacheRelationMapper.insertByRelationModel(new RelationModel(0L, mainId, subData.getId()));
                }
            }
        } else if (mainType.equals("CACHE") && subType.equals("SOURCE")) {
            proxyCacheSourceRelationMapper.deleteByCacheId(mainId);
            for (String key : subNames) {
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

    /**
     * YAML 파일 내용을 채취한 이후, 해당 디렉토리에 저장힌다.
     */
    @Override
    public boolean saveYAMLFileByEachList(ServerCenterInfo local) {
        if (local == null) {
            return false;
        }

        List<ProxySourceMapServerDTO> mapServerDTOs = proxySourceMapper.findBySelectedMapServer(true);
        List<ProxySourceWMSDTO> wmsDTOs = proxySourceMapper.findBySelectedWMS(true);
        List<Object> dtos = Stream.of(mapServerDTOs, wmsDTOs).flatMap(o -> o.stream()).collect(Collectors.toList());

        String context = MapProxyUtil.fetchYamlFileContextWithDTO(proxyLayerMapper.findBySelected(true), dtos, proxyCacheMapper.findBySelected(true), proxyGlobalMapper.findAll(), local, MAP_SERVER_PORT);
        FileSystemUtil.createAtFile(dataPath + Constants.PROXY_SETTING_FILE_PATH + "/" + Constants.PROXY_SETTING_FILE_NAME, context);
        return true;
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
        switch(type) {
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
        switch(type) {
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
            proxyLayerMapper.findBySelected(true).stream().map(o -> new HashMap<String, Object>() {{
                put("layer", o.getName());
                put("sources", o.getSources().stream().map(ProxySourceDTO::getName).collect(Collectors.toList()));
                put("caches", o.getCaches().stream().map(ProxyCacheDTO::getName).collect(Collectors.toList()));
            }}).collect(Collectors.toList()),
            proxySourceMapper.findBySelected(true).stream().map(ProxySourceDTO::getName).collect(Collectors.toList()),
            proxyCacheMapper.findBySelected(true).stream().map(o -> new HashMap<String, Object>() {{
                put("cache", o.getName());
                put("sources", o.getSources().stream().map(ProxySourceDTO::getName).collect(Collectors.toList()));
            }}).collect(Collectors.toList())
        );
    }

    @Override
    public Map<String, Object> writeYAMLFileForNeighbors(HttpServletRequest request, boolean result, ServerCenterInfo local, List<ServerCenterInfo> neighbors) {
        if (!result || local == null) {
            return new HashMap<String, Object>() {
                {
                    put("result", false);
                    put("success", 0);
                    put("failure", neighbors.size());
                }
            };
        }

        int success = 0;
        this.saveYAMLFileByEachList(local);
        success = this.sendYAMLSyncRESTForNeighbor(request, neighbors);

        final int cnt = success;
        return new HashMap<String, Object>() {
            {
                put("result", result);
                put("success", cnt);
                put("failure", neighbors.size() - cnt);
            }
        };
    }

    /**
     * MapProxy 기반 Layer 정보를 저장한다.
     * @param proxyLayerModel ProxyLayerModel
     */
    @Override
    @Transactional
    public boolean saveProxyLayerByModel(ProxyLayerModel proxyLayerModel, ServerCenterInfo local) {
        if (local == null) {
            return false;
        }

        ProxyLayerDTO layer = proxyLayerMapper.findByName(proxyLayerModel.getName());
        ProxyLayerDTO dto;
        switch(proxyLayerModel.getMethod()) {
            case "INSERT" :
                if (layer != null) {
                    return true;
                }
                long nextIdx = proxyLayerMapper.findNextSeqVal();
                proxyLayerModel.setId(nextIdx);
                dto = ProxyLayerModel.convertDTO(proxyLayerModel);
                dto.setIsDefault(false);
                dto.setSelected(false);
                return proxyLayerMapper.insert(dto) > 0 && createRelationWithMainIdAndSubNames("LAYER", "SOURCE", nextIdx, proxyLayerModel.getSources()) && createRelationWithMainIdAndSubNames("LAYER", "CACHE", nextIdx, proxyLayerModel.getCaches());

            case "UPDATE" :
                if (layer == null) {
                    return false;
                }
                if (layer.getIsDefault().equals(true)) {
                    return false;
                }
                dto = ProxyLayerModel.convertDTO(proxyLayerModel);
                dto.setIsDefault(layer.getIsDefault());
                dto.setSelected(layer.getSelected());
                return (proxyLayerMapper.updateByName(dto) > 0) && createRelationWithMainIdAndSubNames("LAYER", "SOURCE", layer.getId(), proxyLayerModel.getSources()) && createRelationWithMainIdAndSubNames("LAYER", "CACHE", layer.getId(), proxyLayerModel.getCaches());

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
    public boolean saveProxySourceMapServerByModel(ProxySourceMapServerModel proxySourceMapServerModel, ServerCenterInfo local) {
        if (local == null) {
            return false;
        }

        ProxySourceDTO root = proxySourceMapper.findByName(proxySourceMapServerModel.getName());
        ProxySourceMapServerDTO dto;
        switch(proxySourceMapServerModel.getMethod()) {
            case "INSERT" :
                if (root != null) {
                    return true;
                }
                long nextIdx = proxySourceMapper.findNextSeqVal();
                proxySourceMapServerModel.setId(nextIdx);
                dto = ProxySourceMapServerModel.convertDTO(proxySourceMapServerModel);
                dto.setIsDefault(false);
                dto.setSelected(false);
                return proxySourceMapper.insert(dto) > 0 && proxySourceMapper.insertMapServer(dto) > 0;

            case "UPDATE" :
                if (root == null) {
                    return false;
                }
                if (root.getIsDefault().equals(true)) {
                    return false;
                }
                dto = ProxySourceMapServerModel.convertDTO(proxySourceMapServerModel);
                dto.setId(root.getId());
                dto.setIsDefault(root.getIsDefault());
                dto.setSelected(root.getSelected());
                return proxySourceMapper.update(dto) > 0 && proxySourceMapper.updateMapServer(dto) > 0;

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
    public boolean saveProxySourceWMSByModel(ProxySourceWMSModel proxySourceWMSModel, ServerCenterInfo local) {
        if (local == null) {
            return false;
        }

        ProxySourceDTO root = proxySourceMapper.findByName(proxySourceWMSModel.getName());
        ProxySourceWMSDTO dto;
        switch(proxySourceWMSModel.getMethod()) {
            case "INSERT" :
                if (root != null) {
                    return true;
                }
                long nextIdx = proxySourceMapper.findNextSeqVal();
                proxySourceWMSModel.setId(nextIdx);
                dto = ProxySourceWMSModel.convertDTO(proxySourceWMSModel);
                dto.setRequestURL(String.format(Constants.MAP_SERVER_WMS_URL));

                // TODO : setRequestMap 에서 ${data_dir} 빼야 하는 거 확인 필요할 것임.
                // DB 에는 미포함, 서버에선 YAML 설정 시, ${data_dir} + DB 값 으로 설정해야 할 것임.

                dto.setIsDefault(false);
                dto.setSelected(false);
                return proxySourceMapper.insert(dto) > 0 && proxySourceMapper.insertWMS(dto) > 0;

            case "UPDATE" :
                if (root == null) {
                    return false;
                }
                if (root.getIsDefault().equals(true)) {
                    return false;
                }
                dto = ProxySourceWMSModel.convertDTO(proxySourceWMSModel);
                dto.setId(root.getId());
                dto.setRequestURL(String.format(Constants.MAP_SERVER_WMS_URL));
                dto.setIsDefault(root.getIsDefault());
                dto.setSelected(root.getSelected());

                return proxySourceMapper.update(dto) > 0 && proxySourceMapper.updateWMS(dto) > 0;

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
    public boolean saveProxyCacheByModel(ProxyCacheModel proxyCacheModel, ServerCenterInfo local) {
        if (local == null) {
            return false;
        }

        ProxyCacheDTO cache = proxyCacheMapper.findByName(proxyCacheModel.getName());
        ProxyCacheDTO dto;
        switch(proxyCacheModel.getMethod()) {
            case "INSERT" :
                if (cache != null) {
                    return true;
                }
                long nextIdx = proxyCacheMapper.findNextSeqVal();
                proxyCacheModel.setId(nextIdx);
                dto = ProxyCacheModel.convertDTO(proxyCacheModel);
                dto.setIsDefault(false);
                dto.setSelected(false);
                return proxyCacheMapper.insert(dto) > 0 && createRelationWithMainIdAndSubNames("CACHE", "SOURCE", nextIdx, proxyCacheModel.getSources()) ;

            case "UPDATE" :
                if (cache == null) {
                    return false;
                }
                if (cache.getIsDefault().equals(true)) {
                    return false;
                }
                dto = ProxyCacheModel.convertDTO(proxyCacheModel);
                dto.setIsDefault(cache.getIsDefault());
                dto.setSelected(cache.getSelected());

                return (proxyCacheMapper.updateByName(dto) > 0) && createRelationWithMainIdAndSubNames("CACHE", "SOURCE", cache.getId(), proxyCacheModel.getSources());

            default :
                return false;
        }
    }

    @Override
    @Transactional
    public boolean saveProxyGlobalByModelList(List<ProxyGlobalModel> proxyGlobalModels, ServerCenterInfo local) {
        if (local == null) {
            return false;
        }

        if (proxyGlobalModels.stream().map(ProxyGlobalModel::getKey).distinct().count() != proxyGlobalModels.stream().count()) {
            return false;
        }

        proxyGlobalMapper.deleteAll();
        int count = 0;
        for (ProxyGlobalModel model : proxyGlobalModels) {
            long nextId = proxyGlobalMapper.findNextSeqVal();
            model.setId(nextId);
            count += proxyGlobalMapper.insert(ProxyGlobalModel.convertDTO(model));
        }

        return count == proxyGlobalModels.size() && saveYAMLFileByEachList(local);
    }

    /**
     * Proxy 정보를 Main ID 로 삭제한다.
     * @param id long, type String
     */
    @Override
    @Transactional
    public boolean removeProxyDataByIdAndType(long id, String type, ServerCenterInfo local) {
        if (local == null) {
            return false;
        }

        switch(type.toUpperCase()) {
            case "LAYER" :
                ProxyLayerDTO layer = proxyLayerMapper.findById(id);
                if (layer == null) {
                    return false;
                }
                if (layer.getIsDefault().equals(true)) {
                    return false;
                }

                proxyLayerSourceRelationMapper.deleteByLayerId(id);
                proxyLayerCacheRelationMapper.deleteByLayerId(id);
                proxyLayerMapper.deleteById(id);

                return true;

            case "SOURCE-MAPSERVER" :
                ProxySourceDTO sourceMS = proxySourceMapper.findById(id);
                if (sourceMS == null) {
                    return false;
                }
                if (sourceMS.getIsDefault().equals(true)) {
                    return false;
                }

                List<ProxyCacheDTO> sourceCachesMS = cascadeRelativeComponent.loadProxySourceRemoveAfterOrphanProxyCaches(id);
                List<ProxyLayerDTO> sourceLayerMS = cascadeRelativeComponent.loadProxySourceRemoveAfterOrphanProxyLayers(id);
                if (sourceCachesMS.size() > 0 || sourceLayerMS.size() > 0) {
                    cascadeRelativeComponent.removeProxySourceWithOrphanCheck(sourceMS);
                }

                proxyLayerSourceRelationMapper.deleteBySourceId(id);
                proxyCacheSourceRelationMapper.deleteBySourceId(id);
                proxySourceMapper.deleteByIdMapServer(id);
                proxySourceMapper.deleteById(id);

                return true;

            case "SOURCE-WMS" :
                ProxySourceDTO sourceWMS = proxySourceMapper.findById(id);
                if (sourceWMS == null) {
                    return false;
                }
                if (sourceWMS.getIsDefault().equals(true)) {
                    return false;
                }

                List<ProxyCacheDTO> sourceCachesWMS = cascadeRelativeComponent.loadProxySourceRemoveAfterOrphanProxyCaches(id);
                List<ProxyLayerDTO> sourceLayerWMS = cascadeRelativeComponent.loadProxySourceRemoveAfterOrphanProxyLayers(id);
                if (sourceCachesWMS.size() > 0 || sourceLayerWMS.size() > 0) {
                    cascadeRelativeComponent.removeProxySourceWithOrphanCheck(sourceWMS);
                }

                proxyLayerSourceRelationMapper.deleteBySourceId(id);
                proxyCacheSourceRelationMapper.deleteBySourceId(id);
                proxySourceMapper.deleteByIdWMS(id);
                proxySourceMapper.deleteById(id);

                return true;

            case "CACHE" :
                ProxyCacheDTO cache = proxyCacheMapper.findById(id);
                if (cache == null) {
                    return false;
                }
                if (cache.getIsDefault().equals(true)) {
                    return false;
                }

                List<ProxyLayerDTO> cacheLayers = cascadeRelativeComponent.loadProxyCacheRemoveAfterOrphanProxyLayers(id);
                if (cacheLayers.size() > 0) {
                    cascadeRelativeComponent.removeProxyCacheWithOrphanCheck(id);
                }

                proxyLayerCacheRelationMapper.deleteByCacheId(id);
                proxyCacheSourceRelationMapper.deleteByCacheId(id);
                proxyCacheMapper.deleteById(id);

                return true;

            default :
                return false;
        }
    }

    @Override
    @Transactional
    public boolean removeProxyDataByNameAndType(String name, String type, ServerCenterInfo local) {
        if (local == null) {
            return false;
        }

        switch(type.toUpperCase()) {
            case "LAYER" :
                ProxyLayerDTO layerDTO = proxyLayerMapper.findByName(name);
                if (layerDTO == null) {
                    return false;
                } else {
                    return removeProxyDataByIdAndType(layerDTO.getId(), type, local);
                }

            case "SOURCE-MAPSERVER" :
            case "SOURCE-WMS" :
                ProxySourceDTO sourceDTO = proxySourceMapper.findByName(name);
                if (sourceDTO == null) {
                    return false;
                } else {
                    return removeProxyDataByIdAndType(sourceDTO.getId(), type, local);
                }

            case "CACHE" :
                ProxyCacheDTO cacheDTO = proxyCacheMapper.findByName(name);
                if (cacheDTO == null) {
                    return false;
                } else {
                    return removeProxyDataByIdAndType(cacheDTO.getId(), type, local);
                }

            default :
                return false;
        }
    }

    /**
     * 서버에서 Proxy 설정 데이터를 가져와 YAML 파일에 반영한다.
     * @param proxySelectModel ProxySelectModel
     */
    @Override
    @Transactional
    public boolean setProxyDataSelectByModel(ProxySelectRequestModel proxySelectModel, ServerCenterInfo local) {
        if (local == null) {
            return false;
        }

        proxyLayerMapper.updateSelectedAllDisabled();
        proxySourceMapper.updateSelectedAllDisabled();
        proxyCacheMapper.updateSelectedAllDisabled();

        if (proxySelectModel.getLayers().size() > 0) {
            proxyLayerMapper.updateSelectedByNameIn(proxySelectModel.getLayers(), true);
        }
        if (proxySelectModel.getSources().size() > 0) {
            proxySourceMapper.updateSelectedByNameIn(proxySelectModel.getSources(), true);
        }
        if (proxySelectModel.getCaches().size() > 0) {
            proxyCacheMapper.updateSelectedByNameIn(proxySelectModel.getCaches(), true);
        }

        return saveYAMLFileByEachList(local);
    }
}
