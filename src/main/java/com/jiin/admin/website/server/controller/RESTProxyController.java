package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.model.ProxySelectModel;
import com.jiin.admin.website.view.service.ProxyCacheService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/proxy")
public class RESTProxyController {
    @Resource
    private ProxyCacheService proxyCacheService;

    @GetMapping("form")
    public ProxySelectModel getInitializeProxySelectModel(){
        return proxyCacheService.loadProxySetting();
    }
}
