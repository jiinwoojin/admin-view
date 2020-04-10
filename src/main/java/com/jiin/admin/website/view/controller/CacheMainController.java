package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.gis.ProxySettingService;
import com.jiin.admin.website.model.ProxyCacheModel;
import com.jiin.admin.website.model.ProxyLayerModel;
import com.jiin.admin.website.model.ProxySourceModel;
import com.jiin.admin.website.util.MapProxyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("cache")
public class CacheMainController {
    @Autowired
    private ProxySettingService cacheService;

    @RequestMapping("preview")
    public String cacheDataPreview(Model model){
        model.addAttribute("layer", cacheService.getCachedLayerData());
        model.addAttribute("request", cacheService.getCachedRequestData());
        model.addAttribute("boundingBox", cacheService.getBoundingBoxInfoWithCrs());
        model.addAttribute("serviceURL", MapProxyUtil.getServiceURL());
        return "page/cache/preview";
    }

    @RequestMapping("setting")
    public String cacheDataSetting(Model model){
        model.addAttribute("layerMap", cacheService.getProxyLayerEntities());
        model.addAttribute("sourceMap", cacheService.getProxySourceEntities());
        model.addAttribute("cacheMap", cacheService.getProxyCacheEntities());

        model.addAttribute("addProxyLayer", new ProxyLayerModel());
        model.addAttribute("addProxySource", new ProxySourceModel());
        model.addAttribute("addProxyCache", new ProxyCacheModel());

        model.addAttribute("proxySources", cacheService.getProxySourceEntities().get("data"));

        return "page/cache/setting";
    }

    @RequestMapping(value = "add-proxy-layer", method = RequestMethod.POST)
    public String addProxyLayer(Model model, ProxyLayerModel proxyLayerModel){
        cacheService.createProxyLayerEntityWithModel(proxyLayerModel);
        return "redirect:setting";
    }

    @RequestMapping(value = "add-proxy-source", method = RequestMethod.POST)
    public String addProxySource(Model model, ProxySourceModel proxySourceModel){
        cacheService.createProxySourceEntityWithModel(proxySourceModel);
        return "redirect:setting";
    }

    @RequestMapping(value = "add-proxy-cache", method = RequestMethod.POST)
    public String addProxyCache(Model model, ProxyCacheModel proxyCacheModel){
        cacheService.createProxyCacheEntityWithModel(proxyCacheModel);
        return "redirect:setting";
    }

    @RequestMapping("layers")
    public String cacheSourcesPage(Model model){
        model.addAttribute("layer", cacheService.getCachedLayerData());
        model.addAttribute("request", cacheService.getCachedRequestData());
        model.addAttribute("boundingBox", cacheService.getBoundingBoxInfoWithCrs());
        model.addAttribute("serviceURL", MapProxyUtil.getServiceURL());
        return "page/cache/layers";
    }

    @RequestMapping("grids")
    public String cacheGridsPage(Model model){
        model.addAttribute("boundingBox", cacheService.getBoundingBoxInfoWithCrs());
        return "page/cache/grids";
    }

    @RequestMapping("seeds")
    public String cacheConfigPage(Model model){
        return "page/cache/seeds";
    }
}
