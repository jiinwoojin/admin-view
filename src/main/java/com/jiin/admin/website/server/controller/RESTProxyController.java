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

    // 체크 이중화 REST API
    @PostMapping("sync/yaml-save")
    public Map<String, Object> postSyncYAMLFileSettings() {
        return new HashMap<String, Object>() {{
            put("result", proxyCacheService.saveYAMLFileByEachList(serverCenterInfoService.loadLocalInfoData()));
        }};
    }
}
