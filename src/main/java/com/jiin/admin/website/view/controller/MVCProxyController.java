package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.website.model.ProxyCacheModelV2;
import com.jiin.admin.website.model.ProxyLayerModelV2;
import com.jiin.admin.website.model.ProxySourceModelV2;
import com.jiin.admin.website.view.service.ProxyCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("proxy")
public class MVCProxyController {
    @Autowired
    private ProxyCacheService proxyCacheService;

    @Autowired
    private SessionService sessionService;

    @RequestMapping("setting")
    public String pageProxySetting(Model model){
        model.addAttribute("layers", proxyCacheService.loadDataList("LAYERS"));
        model.addAttribute("sources", proxyCacheService.loadDataList("SOURCES"));
        model.addAttribute("caches", proxyCacheService.loadDataList("CACHES"));

        model.addAttribute("addProxyLayer", proxyCacheService.loadDataModel("LAYERS"));
        model.addAttribute("addProxySource", proxyCacheService.loadDataModel("SOURCES"));
        model.addAttribute("addProxyCache", proxyCacheService.loadDataModel("CACHES"));

        model.addAttribute("selectSources", proxyCacheService.loadDataListBySelected("SOURCES", true));
        model.addAttribute("selectCaches", proxyCacheService.loadDataListBySelected("CACHES", true));

        model.addAttribute("proxyCacheDirectory", proxyCacheService.loadProxyMainDir());
        model.addAttribute("dataDirectory", proxyCacheService.loadDataDir());
        model.addAttribute("mapServerBinary", proxyCacheService.loadMapServerBinary());

        return "page/proxy/setting";
    }

    @RequestMapping(value = "layer-save", method = RequestMethod.POST)
    public String postLayerSave(ProxyLayerModelV2 proxyLayerModelV2){
        return "redirect:setting";
    }

    @RequestMapping(value = "source-save", method = RequestMethod.POST)
    public String postSourceSave(ProxySourceModelV2 proxySourceModelV2){
        return "redirect:setting";
    }

    @RequestMapping(value = "cache-save", method = RequestMethod.POST)
    public String postCacheSave(ProxyCacheModelV2 proxyCacheModelV2){
        return "redirect:setting";
    }

    @RequestMapping("${type}-delete")
    public String linkProxyDataDelete(@PathVariable String type, @RequestParam long id){
        return "redirect:setting";
    }
}
