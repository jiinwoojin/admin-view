package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.server.service.CheckService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/check")
public class RESTCheckController {
    @Resource
    private CheckService service;

    @PostMapping("duplicate")
    public boolean duplicate(@RequestParam("type") String type, @RequestParam("name") String name) {
        return service.checkDuplicate(type, name);
    }
}
