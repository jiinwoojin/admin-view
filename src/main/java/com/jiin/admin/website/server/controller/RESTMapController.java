package com.jiin.admin.website.server.controller;

import com.jiin.admin.dto.MapDTO;
import com.jiin.admin.website.view.service.MapService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("api/map")
public class RESTMapController {
    @Resource
    private MapService mapService;

    @RequestMapping("list")
    public List<MapDTO> findAllMapEntities(){
        return mapService.loadMapDataList();
    }
}
