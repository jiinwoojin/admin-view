package com.jiin.admin.website.server.controller;

import com.jiin.admin.entity.MapEntity;
import com.jiin.admin.website.view.service.ManageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/map")
public class MapRestController {
    @Resource
    private ManageService manageService;

    @RequestMapping("list")
    public List<MapEntity> findAllMapEntities(){
        return manageService.getSourceList();
    }
}
