package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.website.model.ProxyCacheModelV2;
import com.jiin.admin.website.model.ProxyLayerModelV2;
import com.jiin.admin.website.model.ProxySelectModel;
import com.jiin.admin.website.model.ProxySourceModelV2;
import com.jiin.admin.website.view.service.ProxyCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequestMapping("proxy")
public class MVCProxyController {
    @Autowired
    private ProxyCacheService proxyCacheService;

    @Autowired
    private SessionService sessionService;

    @RequestMapping("setting")
    public String pageProxySetting(Model model){
        model.addAttribute("message", sessionService.message());

        model.addAttribute("layers", proxyCacheService.loadDataList("LAYERS"));
        model.addAttribute("sources", proxyCacheService.loadDataList("SOURCES"));
        model.addAttribute("caches", proxyCacheService.loadDataList("CACHES"));

        model.addAttribute("addProxyLayer", proxyCacheService.loadDataModel("LAYERS"));
        model.addAttribute("addProxySource", proxyCacheService.loadDataModel("SOURCES"));
        model.addAttribute("addProxyCache", proxyCacheService.loadDataModel("CACHES"));

        model.addAttribute("selectSources", proxyCacheService.loadDataListBySelected("SOURCES", true));
        model.addAttribute("selectCaches", proxyCacheService.loadDataListBySelected("CACHES", true));

        model.addAttribute("proxyCacheDirectory", proxyCacheService.loadProxyCacheMainDir());
        model.addAttribute("dataDirectory", proxyCacheService.loadDataDir());
        model.addAttribute("mapServerBinary", proxyCacheService.loadMapServerBinary());

        return "page/proxy/setting";
    }

    @RequestMapping(value = "layer-save", method = RequestMethod.POST)
    public String postLayerSave(ProxyLayerModelV2 proxyLayerModelV2){
        boolean result = proxyCacheService.saveProxyLayerByModel(proxyLayerModelV2);
        sessionService.message(String.format("[%s] LAYER 정보 저장에 %s 했습니다.", proxyLayerModelV2.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "source-save", method = RequestMethod.POST)
    public String postSourceSave(ProxySourceModelV2 proxySourceModelV2){
        boolean result = proxyCacheService.saveProxySourceByModel(proxySourceModelV2);
        sessionService.message(String.format("[%s] SOURCE 정보 저장에 %s 했습니다.", proxySourceModelV2.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "cache-save", method = RequestMethod.POST)
    public String postCacheSave(ProxyCacheModelV2 proxyCacheModelV2){
        boolean result = proxyCacheService.saveProxyCacheByModel(proxyCacheModelV2);
        sessionService.message(String.format("[%s] CACHE 정보 저장에 %s 했습니다.", proxyCacheModelV2.getName(), result ? "성공" : "실패"));
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
}
