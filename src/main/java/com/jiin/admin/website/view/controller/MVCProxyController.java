package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.website.model.*;
import com.jiin.admin.website.view.service.ProxyCacheService;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import com.jiin.admin.website.view.service.ServiceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("proxy")
public class MVCProxyController {
    @Value("${project.server-port.mapproxy-port}")
    private int MAP_PROXY_PORT;

    @Autowired
    private ProxyCacheService proxyCacheService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ServerCenterInfoService serverCenterInfoService;

    @Autowired
    private ServiceInfoService serviceInfoService;

    @RequestMapping("setting")
    public String pageProxySetting(Model model){
        model.addAttribute("message", sessionService.message());

        model.addAttribute("layers", proxyCacheService.loadDataList("LAYERS"));
        model.addAttribute("sources", proxyCacheService.loadDataList("SOURCES"));
        model.addAttribute("caches", proxyCacheService.loadDataList("CACHES"));

        model.addAttribute("selectSources", proxyCacheService.loadDataListBySelected("SOURCES", true));
        model.addAttribute("selectCaches", proxyCacheService.loadDataListBySelected("CACHES", true));

        model.addAttribute("proxyCacheDirectory", proxyCacheService.loadProxyCacheMainDir());
        model.addAttribute("dataDirectory", proxyCacheService.loadDataDir());
        model.addAttribute("mapServerBinary", proxyCacheService.loadMapServerBinary());

        return "page/proxy/setting";
    }

    @RequestMapping(value = "layer-save", method = RequestMethod.POST)
    public String postLayerSave(ProxyLayerModel proxyLayerModel){
        boolean result = proxyCacheService.saveProxyLayerByModel(proxyLayerModel);
        sessionService.message(String.format("[%s] LAYER 정보 저장에 %s 했습니다.", proxyLayerModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "source-mapserver-save", method = RequestMethod.POST)
    public String postSourceMapServerSave(ProxySourceMapServerModel proxySourceMapServerModel){
        boolean result = proxyCacheService.saveProxySourceMapServerByModel(proxySourceMapServerModel);
        sessionService.message(String.format("[%s] SOURCE (MapServer) 정보 저장에 %s 했습니다.", proxySourceMapServerModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "source-wms-save", method = RequestMethod.POST)
    public String postSourceWMSSave(ProxySourceWMSModel proxySourceWMSModel){
        boolean result = proxyCacheService.saveProxySourceWMSByModel(proxySourceWMSModel);
        sessionService.message(String.format("[%s] SOURCE (MapServer) 정보 저장에 %s 했습니다.", proxySourceWMSModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "cache-save", method = RequestMethod.POST)
    public String postCacheSave(ProxyCacheModel proxyCacheModel){
        boolean result = proxyCacheService.saveProxyCacheByModel(proxyCacheModel);
        sessionService.message(String.format("[%s] CACHE 정보 저장에 %s 했습니다.", proxyCacheModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping("{type}-delete")
    public String linkProxyDataDelete(@PathVariable String type, @RequestParam long id, @RequestParam String name){
        boolean result = proxyCacheService.removeProxyDataByIdAndType(id, type);
        sessionService.message(String.format("[%s] %s 정보 삭제에 %s 했습니다.", name, type.toUpperCase(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "checking-save", method = RequestMethod.POST)
    public String checkingProxyDataSettings(ProxySelectModel proxySelectModel) {
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
        model.addAttribute("proxyLayers", proxyCacheService.loadDataList("LAYERS"));
        return "page/proxy/seeding";
    }
}
