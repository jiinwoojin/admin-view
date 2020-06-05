package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.model.ProxyCacheModel;
import com.jiin.admin.website.model.ProxyLayerModel;
import com.jiin.admin.website.model.ProxySourceModel;
import com.jiin.admin.website.view.service.ProxyCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("proxy")
public class MVCProxyController {
    @Autowired
    private ProxyCacheService proxyCacheService;

    @RequestMapping("setting")
    public String pageProxySetting(Model model){
        model.addAttribute("layerMap", proxyCacheService.loadDataList("LAYERS"));
        model.addAttribute("sourceMap", proxyCacheService.loadDataList("SOURCES"));
        model.addAttribute("cacheMap", proxyCacheService.loadDataList("CACHES"));

        model.addAttribute("addProxyLayer", proxyCacheService.loadDataModel("LAYERS"));
        model.addAttribute("addProxySource", proxyCacheService.loadDataModel("SOURCES"));
        model.addAttribute("addProxyCache", proxyCacheService.loadDataModel("CACHES"));

        model.addAttribute("proxySources", proxyCacheService.loadDataListBySelected("SOURCES", true));
        model.addAttribute("proxyCaches", proxyCacheService.loadDataListBySelected("CACHES", true));

        model.addAttribute("proxyCacheDirectory", proxyCacheService.loadProxyMainDir());
        model.addAttribute("dataDirectory", proxyCacheService.loadDataDir());

        return "page/proxy/setting";
    }

    @RequestMapping(value = "layer-save", method = RequestMethod.POST)
    public String postLayerSave(ProxyLayerModel proxyLayerModel, @RequestParam String method){
        return null;
    }

    @RequestMapping(value = "source-save", method = RequestMethod.POST)
    public String postSourceSave(ProxySourceModel proxySourceModel, @RequestParam String method){
        return null;
    }

    @RequestMapping(value = "cache-save", method = RequestMethod.POST)
    public String postCacheSave(ProxyCacheModel proxyCacheModel, @RequestParam String method){
        return null;
    }
}
