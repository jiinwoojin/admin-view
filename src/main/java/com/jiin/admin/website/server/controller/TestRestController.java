package com.jiin.admin.website.server.controller;

import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.dto.MapDTO;
import com.jiin.admin.dto.SymbolPositionDTO;
import com.jiin.admin.mapper.data.LayerMapper;
import com.jiin.admin.mapper.data.MapMapper;
import com.jiin.admin.mapper.data.SymbolPositionMapper;
import com.jiin.admin.website.model.LayerPageModel;
import com.jiin.admin.website.model.MapPageModel;
import com.jiin.admin.website.view.service.LayerService;
import com.jiin.admin.website.view.service.MapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestRestController {
    @Resource
    private MapMapper mapMapper;

    @Resource
    private LayerMapper layerMapper;

    @Resource
    private SymbolPositionMapper symbolPositionMapper;

    @Resource
    private MapService mapService;

    @Resource
    private LayerService layerService;

    @GetMapping("map-list")
    public List<MapDTO> getMapDTOList(){
        return mapMapper.findAll();
    }

    @GetMapping("map-list-page-model")
    public Map<String, Object> getMapDTOListByPageModel(MapPageModel mapPageModel){
        return mapService.loadDataListAndCountByPaginationModel(mapPageModel);
    }

    @GetMapping("layer-list-page-model")
    public Map<String, Object> getLayerDTOListByPageModel(LayerPageModel layerPageModel){
        return layerService.loadDataListAndCountByPaginationModel(layerPageModel);
    }

    @GetMapping("layer-list")
    public List<LayerDTO> getLayerDTOList(){
        return layerMapper.findAll();
    }

    @GetMapping("symbol-list")
    public List<SymbolPositionDTO> getSymbolPositionDTOList(){
        return symbolPositionMapper.findAll();
    }


}
