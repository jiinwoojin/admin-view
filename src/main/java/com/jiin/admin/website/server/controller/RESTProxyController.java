package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.model.ProxySelectResponseModel;
import com.jiin.admin.website.view.service.ProxyCacheService;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
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

//    @GetMapping
//    public List<ProxyLayerDTO> getProxyLayerList(){
//        return (List<ProxyLayerDTO>) proxyCacheService.loadDataListBySelected("LAYERS", true);
//    }
//
//    // LAYER 추가 동기화 [파기 예정]
//    @PostMapping("sync/layer-save")
//    public Map<String, Object> postSyncGlobalSave(@RequestBody ProxyLayerModel proxyLayerModel) {
//        return new HashMap<String, Object>() {{
//            put("result", false);
//        }};
//    }
//
//    // SOURCE - WMS 동기화 [파기 예정]
//    @PostMapping("sync/source-wms-save")
//    public Map<String, Object> postSyncSourceWMSSave(@RequestBody ProxySourceWMSModel proxySourceWMSModel) {
//        return new HashMap<String, Object>() {{
//            put("result", false);
//        }};
//    }
//
//    // SOURCE - MAPSERVER 동기화 [파기 예정]
//    @PostMapping("sync/source-mapserver-save")
//    public Map<String, Object> postSyncSourceMapServerSave(@RequestBody ProxySourceMapServerModel proxySourceMapServerModel) {
//        return new HashMap<String, Object>() {{
//            put("result", false);
//        }};
//    }
//
//    // CACHE 동기화 [파기 예정]
//    @PostMapping("sync/cache-save")
//    public Map<String, Object> postSyncCacheSave(@RequestBody ProxyCacheModel proxyCacheModel) {
//        return new HashMap<String, Object>() {{
//            put("result", false);
//        }};
//    }
//
//    // 데이터 삭제 동기화 [파기 예정]
//    @PostMapping("sync/{type}-delete")
//    public Map<String, Object> postSyncProxyDataDelete(@PathVariable String type, @RequestBody Map<String, Object> map) {
//        return new HashMap<String, Object>() {{
//            put("result", false);
//        }};
//    }
//
//    // GLOBAL 동기화 [파기 예정]
//    @PostMapping("sync/global-save")
//    public Map<String, Object> postSyncGlobalSave(HttpServletRequest request, @RequestBody List<ProxyGlobalModel> proxyGlobalModels) {
//        return new HashMap<String, Object>() {{
//            put("result", proxyCacheService.saveProxyGlobalByModelList(request, proxyGlobalModels, serverCenterInfoService.loadLocalInfoData(), serverCenterInfoService.loadNeighborList()));
//        }};
//    }

//    // 체크 동기화
//    @PostMapping("sync/checking-save")
//    public Map<String, Object> postSyncCheckingProxyDataSettings(HttpServletRequest request, @RequestBody ProxySelectRequestModel proxySelectModel) {
//        return new HashMap<String, Object>() {{
//            put("result", proxyCacheService.setProxyDataSelectByModel(request, proxySelectModel, serverCenterInfoService.loadLocalInfoData(), serverCenterInfoService.loadNeighborList()));
//        }};
//    }

    // 체크 동기화
    @PostMapping("sync/yaml-save")
    public Map<String, Object> postSyncYAMLFileSettings() {
        return new HashMap<String, Object>() {{
            put("result", proxyCacheService.saveYAMLFileByEachList(serverCenterInfoService.loadLocalInfoData()));
        }};
    }
}
