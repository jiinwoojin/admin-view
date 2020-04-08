package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.gis.ProxySettingService;
import com.jiin.admin.website.model.ProxySelectModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/proxy")
public class ProxyRestController {
    @Resource
    private ProxySettingService proxySettingService;

    @GetMapping("form")
    public ProxySelectModel getInitializeProxySelectModel(){
        return proxySettingService.getCurrentMapProxySettings();
    }
}
