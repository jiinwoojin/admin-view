package com.jiin.admin.website.server.controller;

import com.jiin.admin.dto.*;
import com.jiin.admin.mapper.data.*;
import com.jiin.admin.website.model.LayerPageModel;
import com.jiin.admin.website.model.MapPageModel;
import com.jiin.admin.website.view.service.LayerService;
import com.jiin.admin.website.view.service.MapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class RESTTestController {
    @Value("${project.data-path}")
    private String dataPath;

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

    @Resource
    private ProxyLayerMapper proxyLayerMapper;

    @Resource
    private ProxyCacheMapper proxyCacheMapper;

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

    @GetMapping("proxy-layer-list")
    public List<ProxyLayerDTO> getProxyLayerDTOList(){
        return proxyLayerMapper.findAll();
    }

    @GetMapping("proxy-cache-list")
    public List<ProxyCacheDTO> getProxyCacheDTOList(){
        return proxyCacheMapper.findAll();
    }

    @PostMapping("file-upload")
    public boolean postFileUploadTest(@RequestParam("file") MultipartFile file) {
        String filePath = dataPath + "/test/" + file.getOriginalFilename();
        try {
            File savedFile = new File(filePath);
            file.transferTo(savedFile);
            return savedFile.exists() && savedFile.length() == file.getSize();
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
            return false;
        }
    }
}
