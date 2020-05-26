package com.jiin.admin.website.server.controller;

import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.dto.MapDTO;
import com.jiin.admin.dto.SymbolPositionDTO;
import com.jiin.admin.mapper.data.LayerMapper;
import com.jiin.admin.mapper.data.MapMapper;
import com.jiin.admin.mapper.data.SymbolPositionMapper;
import com.jiin.admin.website.model.MapPageModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestRestController {
    @Resource
    private MapMapper mapMapper;

    @Resource
    private LayerMapper layerMapper;

    @Resource
    private SymbolPositionMapper symbolPositionMapper;

    @GetMapping("map-list")
    public List<MapDTO> getMapDTOList(){
        return mapMapper.findAll();
    }

    @GetMapping("map-list-page-model")
    public List<MapDTO> getMapDTOListByPageModel(MapPageModel mapPageModel){
        return mapMapper.findByPageModel(mapPageModel);
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
