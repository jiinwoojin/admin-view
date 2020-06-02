package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.view.service.ProxyCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
