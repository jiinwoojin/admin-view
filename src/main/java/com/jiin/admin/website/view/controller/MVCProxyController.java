package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.view.service.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("proxy")
public class MVCProxyController {
    @Autowired
    private ProxyService proxyService;

    @RequestMapping("setting")
    public String pageProxySetting(Model model){
        model.addAttribute("layerMap", proxyService.loadDataList("LAYERS"));
        model.addAttribute("sourceMap", proxyService.loadDataList("SOURCES"));
        model.addAttribute("cacheMap", proxyService.loadDataList("CACHES"));

        model.addAttribute("addProxyLayer", proxyService.loadDataModel("LAYERS"));
        model.addAttribute("addProxySource", proxyService.loadDataModel("SOURCES"));
        model.addAttribute("addProxyCache", proxyService.loadDataModel("CACHES"));

        model.addAttribute("proxySources", proxyService.loadDataListBySelected("SOURCES", true));
        model.addAttribute("proxyCaches", proxyService.loadDataListBySelected("CACHES", true));

        model.addAttribute("proxyCacheDirectory", proxyService.loadProxyMainDir());
        model.addAttribute("dataDirectory", proxyService.loadDataDir());

        return "page/proxy/setting";
    }
}
