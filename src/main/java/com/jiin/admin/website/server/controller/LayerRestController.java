package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.server.service.TegolaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("layer")
public class LayerRestController {

    @Resource
    private TegolaService tgservice;

    @PostMapping("load-tegola-config")
    public Map loadTegolaConfig(){
        return tgservice.loadTegolaConfig();
    }
}
