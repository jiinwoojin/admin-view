package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.model.*;
import com.jiin.admin.website.view.service.ProxyCacheService;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/proxy")
public class RESTProxyController {
    @Resource
    private ProxyCacheService proxyCacheService;

    @Resource
    private ServerCenterInfoService serverCenterInfoService;

    @GetMapping("form")
    public ProxySelectResponseModel getInitializeProxySelectModel() {
        return proxyCacheService.loadProxySetting();
    }

    // LAYER 추가 동기화
    @PostMapping("sync/layer-save")
    public Map<String, Object> postSyncGlobalSave(@RequestBody ProxyLayerModel proxyLayerModel) {
        return new HashMap<String, Object>() {{
            put("result", proxyCacheService.saveProxyLayerByModel(proxyLayerModel, true));
        }};
    }

    // SOURCE - WMS 동기화
    @PostMapping("sync/source-wms-save")
    public Map<String, Object> postSyncSourceWMSSave(@RequestBody ProxySourceWMSModel proxySourceWMSModel) {
        return new HashMap<String, Object>() {{
            put("result", proxyCacheService.saveProxySourceWMSByModel(proxySourceWMSModel, serverCenterInfoService.loadLocalInfoData(), true));
        }};
    }

    // SOURCE - MAPSERVER 동기화
    @PostMapping("sync/source-mapserver-save")
    public Map<String, Object> postSyncSourceMapServerSave(@RequestBody ProxySourceMapServerModel proxySourceMapServerModel) {
        return new HashMap<String, Object>() {{
            put("result", proxyCacheService.saveProxySourceMapServerByModel(proxySourceMapServerModel, true));
        }};
    }

    // CACHE 동기화
    @PostMapping("sync/cache-save")
    public Map<String, Object> postSyncCacheSave(@RequestBody ProxyCacheModel proxyCacheModel) {
        return new HashMap<String, Object>() {{
            put("result", proxyCacheService.saveProxyCacheByModel(proxyCacheModel, true));
        }};
    }

    // GLOBAL 동기화
    @PostMapping("sync/global-save")
    public Map<String, Object> postSyncGlobalSave(@RequestBody List<ProxyGlobalModel> proxyGlobalModels) {
        return new HashMap<String, Object>() {{
            put("result", proxyCacheService.saveProxyGlobalByModelList(proxyGlobalModels, serverCenterInfoService.loadLocalInfoData()));
        }};
    }

    // 데이터 삭제 동기화
    @PostMapping("sync/{type}-delete")
    public Map<String, Object> deleteSyncProxyDataDelete(@PathVariable String type, @RequestBody Map<String, Object> map) {
        return new HashMap<String, Object>() {{
            put("result", proxyCacheService.removeProxyDataByNameAndType((String) map.get("name"), type));
        }};
    }

    // 체크 동기화
    @PostMapping("sync/checking-save")
    public Map<String, Object> putSyncCheckingProxyDataSettings(@RequestBody ProxySelectRequestModel proxySelectModel) {
        return new HashMap<String, Object>() {{
            put("result", proxyCacheService.setProxyDataSelectByModel(proxySelectModel, serverCenterInfoService.loadLocalInfoData()));
        }};
    }
}
