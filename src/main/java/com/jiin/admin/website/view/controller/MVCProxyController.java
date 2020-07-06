package com.jiin.admin.website.view.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiin.admin.config.SessionService;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.*;
import com.jiin.admin.website.util.DockerUtil;
import com.jiin.admin.website.view.service.ProxyCacheService;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("proxy")
public class MVCProxyController {
    @Value("${project.server-port.mapproxy-port}")
    private int MAP_PROXY_PORT;

    @Value("${project.server-port.mapserver-port}")
    private int MAP_SERVER_PORT;

    @Autowired
    private ProxyCacheService proxyCacheService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ServerCenterInfoService serverCenterInfoService;

    @RequestMapping("setting")
    public String pageProxySetting(Model model){
        ServerCenterInfo info = serverCenterInfoService.loadLocalInfoData();
        model.addAttribute("message", sessionService.message());
        model.addAttribute("neighbors", serverCenterInfoService.loadNeighborList());

        model.addAttribute("layers", proxyCacheService.loadDataList("LAYERS"));
        model.addAttribute("sources", proxyCacheService.loadDataList("SOURCES"));
        model.addAttribute("caches", proxyCacheService.loadDataList("CACHES"));
        model.addAttribute("globals", proxyCacheService.loadDataList("GLOBALS"));

        model.addAttribute("mapServerPort", MAP_SERVER_PORT);
        model.addAttribute("mapServerAddress", String.format("http://%s:%d/mapserver/cgi-bin/mapserv?", (info != null) ? info.getIp() : "127.0.0.1", MAP_SERVER_PORT));
        model.addAttribute("selectSources", proxyCacheService.loadDataListBySelected("SOURCES", true));
        model.addAttribute("selectCaches", proxyCacheService.loadDataListBySelected("CACHES", true));

        model.addAttribute("proxyCacheDirectory", proxyCacheService.loadProxyCacheMainDir());
        model.addAttribute("dataDirectory", proxyCacheService.loadDataDir());
        model.addAttribute("mapServerBinary", proxyCacheService.loadMapServerBinary());

        return "page/proxy/setting";
    }

    @RequestMapping(value = "layer-save", method = RequestMethod.POST)
    public String postLayerSave(ProxyLayerModel proxyLayerModel){
        boolean result = proxyCacheService.saveProxyLayerByModel(proxyLayerModel, false);
        sessionService.message(String.format("[%s] LAYER 정보 저장에 %s 했습니다.", proxyLayerModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "source-wms-save", method = RequestMethod.POST)
    public String postSourceWMSSave(ProxySourceWMSModel proxySourceWMSModel){
        boolean result = proxyCacheService.saveProxySourceWMSByModel(proxySourceWMSModel, false);
        sessionService.message(String.format("[%s] SOURCE (MapServer) 정보 저장에 %s 했습니다.", proxySourceWMSModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "source-mapserver-save", method = RequestMethod.POST)
    public String postSourceMapServerSave(ProxySourceMapServerModel proxySourceMapServerModel){
        boolean result = proxyCacheService.saveProxySourceMapServerByModel(proxySourceMapServerModel, false);
        sessionService.message(String.format("[%s] SOURCE (MapServer) 정보 저장에 %s 했습니다.", proxySourceMapServerModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "cache-save", method = RequestMethod.POST)
    public String postCacheSave(ProxyCacheModel proxyCacheModel){
        boolean result = proxyCacheService.saveProxyCacheByModel(proxyCacheModel, false);
        sessionService.message(String.format("[%s] CACHE 정보 저장에 %s 했습니다.", proxyCacheModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "global-save", method = RequestMethod.POST)
    public String postGlobalSave(@RequestParam String json){
        ObjectMapper mapper = new ObjectMapper();
        List<ProxyGlobalModel> list = new ArrayList<>();
        try {
            list = mapper.readValue(json, new TypeReference<List<ProxyGlobalModel>>() {});
        } catch (JsonProcessingException e) {
            log.error("ERROR - " + e.getMessage());
            sessionService.message("GLOBAL 변수들을 수정하는 도중 오류가 발생했습니다.");
            return "redirect:setting";
        }

        sessionService.message(String.format("GLOBAL 정보 저장에 %s 했습니다.", proxyCacheService.saveProxyGlobalByModelList(list) ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping("{type}-delete")
    public String linkProxyDataDelete(@PathVariable String type, @RequestParam long id, @RequestParam String name){
        boolean result = proxyCacheService.removeProxyDataByIdAndType(id, type);
        sessionService.message(String.format("[%s] %s 정보 삭제에 %s 했습니다.", name, type.toUpperCase(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "checking-save", method = RequestMethod.POST)
    public String checkingProxyDataSettings(ProxySelectRequestModel proxySelectModel) {
        boolean result = proxyCacheService.setProxyDataSelectByModel(proxySelectModel);
        sessionService.message(result ? "모든 PROXY 정보들이 수정 되었습니다." : "모든 PROXY 정보들을 설정하는 도중 오류가 발생했습니다.");
        return "redirect:setting";
    }

    @RequestMapping("preview")
    public String proxyLayerPreview(Model model){
        model.addAttribute("local", serverCenterInfoService.loadLocalInfoData());
        model.addAttribute("port", MAP_PROXY_PORT);
        model.addAttribute("proxyLayers", proxyCacheService.loadDataListBySelected("LAYERS", true));
        model.addAttribute("proxyYAML", proxyCacheService.loadProxyYamlSetting());
        return "page/proxy/preview";
    }

    @RequestMapping("seeding")
    public String proxySeedingSetting(Model model){
        model.addAttribute("dockerCacheContainers", DockerUtil.dockerContainers("jimap"));
        return "page/proxy/seeding";
    }
}
