package com.jiin.admin.website.view.component;

import com.jiin.admin.dto.*;
import com.jiin.admin.mapper.data.MapMapper;
import com.jiin.admin.mapper.data.ProxyCacheMapper;
import com.jiin.admin.mapper.data.ProxyLayerMapper;
import com.jiin.admin.mapper.data.ProxySourceMapper;
import com.jiin.admin.website.view.service.MapService;
import com.jiin.admin.website.view.service.ProxyCacheService;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class CascadeRelativeComponent {
    @Resource
    private MapMapper mapMapper;

    @Resource
    private ProxySourceMapper proxySourceMapper;

    @Resource
    private ProxyLayerMapper proxyLayerMapper;

    @Resource
    private ProxyCacheMapper proxyCacheMapper;

    @Resource
    private MapService mapService;

    @Resource
    private ProxyCacheService proxyCacheService;

    @Resource
    private ServerCenterInfoService serverCenterInfoService;

    // CASCADE 1 단계
    // LAYER 를 삭제한 이후 MAP 안의 LAYER 가 1 도 없을 때 미리 관계를 끊고 MAP 자체를 삭제할 요소들을 불러온다.
    public List<MapDTO> loadLayerRemoveAfterOrphanMapData(long layerId){
        List<MapDTO> orphans = new ArrayList<>();
        List<Map<String, Object>> data = mapMapper.findMapLayersCountAndLayerContains(layerId);
        for(Map<String, Object> map : data){
            long id = (Long) map.get("id");
            long cnt = (Long) map.get("cnt");
            boolean contains = (Boolean) map.get("contains");

            if(contains && cnt <= 1) {
                orphans.add(mapMapper.findById(id));
            }
        }
        return orphans;
    }

    // CASCADE 2 단계
    // MAP 을 삭제한 이후 PROXY SOURCE 에서 이와 관련된 주소가 형성되어 있을 때 PROXY SOURCE 자체를 삭제할 요소들을 불러온다.
    public List<ProxySourceDTO> loadMapRemoveAfterOrphanProxySources(long mapId){
        MapDTO map = mapMapper.findById(mapId);
        if (map == null) {
            return new ArrayList<>();
        }

        List<ProxySourceWMSDTO> wmsList = proxySourceMapper.findByWMSListRequestMapEndsWith(map.getMapFilePath());
        List<ProxySourceMapServerDTO> msList = proxySourceMapper.findByMapServerListRequestMapEndsWith(map.getMapFilePath());

        List<ProxySourceDTO> list1 = wmsList.stream()
                .map(o -> (ProxySourceDTO) o)
                .collect(Collectors.toList());

        List<ProxySourceDTO> list2 = msList.stream()
                .map(o -> (ProxySourceDTO) o)
                .collect(Collectors.toList());

        return Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList());
    }

    // CASCADE 3 단계
    // PROXY SOURCE 를 삭제하고 난 이후, 고아 된 PROXY CACHE 목록을 불러온다.
    public List<ProxyCacheDTO> loadProxySourceRemoveAfterOrphanProxyCaches(long sourceId){
        List<ProxyCacheDTO> orphans = new ArrayList<>();
        List<Map<String, Object>> data = proxyCacheMapper.findCacheSourcesCountAndSourceContains(sourceId);

        for(Map<String, Object> map : data){
            long id = (Long) map.get("id");
            long cnt = (Long) map.get("cnt");
            boolean contains = (Boolean) map.get("contains");

            if(contains && cnt <= 1) {
                orphans.add(proxyCacheMapper.findById(id));
            }
        }

        return orphans;
    }

    // CASCADE 4 단계 - 1
    // PROXY SOURCE 를 삭제하고 난 이후, 고아 된 PROXY LAYER 목록을 불러온다.
    public List<ProxyLayerDTO> loadProxySourceRemoveAfterOrphanProxyLayers(long sourceId){
        List<ProxyLayerDTO> orphans = new ArrayList<>();
        List<Map<String, Object>> sourceData = proxyLayerMapper.findLayerSourcesCountAndSourceContains(sourceId);
        for(Map<String, Object> map : sourceData){
            long id = (Long) map.get("id");
            long cnt = (Long) map.get("cnt");
            boolean contains = (Boolean) map.get("contains");

            if(contains && cnt <= 1) {
                ProxyLayerDTO layer = proxyLayerMapper.findById(id);
                if (layer.getCaches().size() == 0) {
                    orphans.add(layer);
                }
            }
        }

        return orphans;
    }

    // CASCADE 4 단계 - 2
    // PROXY CACHE 를 삭제하고 난 이후, 고아 된 PROXY LAYER 목록을 불러온다.
    public List<ProxyLayerDTO> loadProxyCacheRemoveAfterOrphanProxyLayers(long cacheId){
        List<ProxyLayerDTO> orphans = new ArrayList<>();
        List<Map<String, Object>> cacheData = proxyLayerMapper.findLayerCachesCountAndCacheContains(cacheId);
        for(Map<String, Object> map : cacheData){
            long id = (Long) map.get("id");
            long cnt = (Long) map.get("cnt");
            boolean contains = (Boolean) map.get("contains");

            if(contains && cnt <= 1) {
                ProxyLayerDTO layer = proxyLayerMapper.findById(id);
                if (layer.getSources().size() == 0) {
                    orphans.add(layer);
                }
            }
        }

        return orphans;
    }

    // LAYER 데이터 삭제 시 고아 데이터는 CASCADE 로 쓸어버린다.
    public void removeLayerFileWithOrphanCheck(long layerId){
        List<MapDTO> maps = loadLayerRemoveAfterOrphanMapData(layerId);
        if (maps.size() > 0) {
            for(MapDTO map : maps) {
                removeMapFileWithOrphanCheck(map.getId());
            }
        }
    }

    // MAP 데이터 삭제 시 고아 데이터는 CASCADE 로 쓸어버린다.
    public void removeMapFileWithOrphanCheck(long mapId) {
        List<ProxySourceDTO> sources = loadMapRemoveAfterOrphanProxySources(mapId);
        if (sources.size() > 0) {
            boolean updated = false;
            for (ProxySourceDTO source : sources) {
                proxyCacheService.removeProxyDataByNameAndType(source.getName(), source.getType().equalsIgnoreCase("wms") ? "SOURCE-WMS" : "SOURCE-MAPSERVER", serverCenterInfoService.loadLocalInfoData());
                if (source.getSelected().equals(true)) {
                    updated = true;
                }
            }

            if (updated) {
                proxyCacheService.saveYAMLFileByEachList(serverCenterInfoService.loadLocalInfoData());
            }
        }

        try {
            mapService.removeData(mapId);
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }
    }

    // PROXY SOURCE 데이터 삭제 시 고아 데이터는 CASCADE 로 쓸어버린다.
    public void removeProxySourceWithOrphanCheck(ProxySourceDTO source) {
        boolean updated = false;

        List<ProxyCacheDTO> caches = loadProxySourceRemoveAfterOrphanProxyCaches(source.getId());
        List<ProxyLayerDTO> layers = loadProxySourceRemoveAfterOrphanProxyLayers(source.getId());

        if (caches.size() > 0) {
            for (ProxyCacheDTO cache : caches) {
                removeProxyCacheWithOrphanCheck(cache.getId());
                proxyCacheService.removeProxyDataByNameAndType(cache.getName(), "CACHE", serverCenterInfoService.loadLocalInfoData());
                if (cache.getSelected().equals(true)) {
                    updated = true;
                }
            }
        }

        if (layers.size() > 0) {
            for (ProxyLayerDTO layer : layers) {
                proxyCacheService.removeProxyDataByNameAndType(layer.getName(), "LAYER", serverCenterInfoService.loadLocalInfoData());
                if (layer.getSelected().equals(true)) {
                    updated = true;
                }
            }
        }

        if (updated) {
            proxyCacheService.saveYAMLFileByEachList(serverCenterInfoService.loadLocalInfoData());
        }
    }

    // PROXY CACHE 데이터 삭제 시 고아 데이터는 CASCADE 로 쓸어버린다.
    public void removeProxyCacheWithOrphanCheck(long cacheId) {
        boolean updated = false;

        List<ProxyLayerDTO> layers = loadProxyCacheRemoveAfterOrphanProxyLayers(cacheId);
        if (layers.size() > 0) {
            for (ProxyLayerDTO layer : layers) {
                proxyCacheService.removeProxyDataByNameAndType(layer.getName(), "LAYER", serverCenterInfoService.loadLocalInfoData());
                if (layer.getSelected().equals(true)) {
                    updated = true;
                }
            }
        }

        if (updated) {
            proxyCacheService.saveYAMLFileByEachList(serverCenterInfoService.loadLocalInfoData());
        }
    }
}
