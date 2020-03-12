package com.jiin.admin.website.server.controller;

import com.jiin.admin.config.DockerService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/docker")
public class DockerRestController {

    @Resource
    private DockerService service;

    @PostMapping("reload")
    public boolean reload(){
        return service.restart();
    }
}
