package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.model.ServicePortModel;
import com.jiin.admin.website.view.service.ServerInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/server")
public class ServerRestController {
    @Resource
    private ServerInfoService serverInfoService;

    @GetMapping("port/{id}")
    public ServicePortModel getInitializeProxySelectModel(@PathVariable long id) {
        return serverInfoService.getServicePortInfoWithId(id);
    }
}
