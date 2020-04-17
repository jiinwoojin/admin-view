package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.gis.MapProxyYamlComponent;
import com.jiin.admin.website.gis.ProxySettingService;
import com.jiin.admin.website.model.ProxyCacheModel;
import com.jiin.admin.website.model.ProxyLayerModel;
import com.jiin.admin.website.model.ProxySelectModel;
import com.jiin.admin.website.model.ProxySourceModel;
import com.jiin.admin.website.util.MapProxyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping("cache")
public class ProxyMainController {
    @Autowired
    private ProxySettingService proxyService;

    @Resource
    private MapProxyYamlComponent mapProxyYamlComponent;

    @RequestMapping("setting")
    public String cacheDataSetting(Model model){
        model.addAttribute("layerMap", proxyService.getProxyLayerEntities());
        model.addAttribute("sourceMap", proxyService.getProxySourceEntities());
        model.addAttribute("cacheMap", proxyService.getProxyCacheEntities());

        model.addAttribute("addProxyLayer", proxyService.initializeProxyLayerModel());
        model.addAttribute("addProxySource", proxyService.initializeProxySourceModel());
        model.addAttribute("addProxyCache", proxyService.initializeProxyCacheModel());

        model.addAttribute("proxySources", proxyService.getProxySourceEntities().get("data"));
        model.addAttribute("proxyCaches", proxyService.getProxyCacheEntities().get("data"));

        return "page/cache/setting";
    }

    @RequestMapping(value = "add-proxy-layer", method = RequestMethod.POST)
    public String addProxyLayer(Model model, ProxyLayerModel proxyLayerModel){
        proxyService.createProxyLayerEntityWithModel(proxyLayerModel);
        return "redirect:setting";
    }

    @RequestMapping(value = "add-proxy-source", method = RequestMethod.POST)
    public String addProxySource(Model model, ProxySourceModel proxySourceModel){
        proxyService.createProxySourceEntityWithModel(proxySourceModel);
        return "redirect:setting";
    }

    @RequestMapping(value = "add-proxy-cache", method = RequestMethod.POST)
    public String addProxyCache(Model model, ProxyCacheModel proxyCacheModel){
        proxyService.createProxyCacheEntityWithModel(proxyCacheModel);
        return "redirect:setting";
    }

    @RequestMapping(value = "update-proxy-layer", method = RequestMethod.POST)
    public String updateProxyLayer(Model model, ProxyLayerModel proxyLayerModel){
        proxyService.updateProxyLayerEntityWithModel(proxyLayerModel);
        return "redirect:setting";
    }

    @RequestMapping(value = "update-proxy-source", method = RequestMethod.POST)
    public String updateProxySource(Model model, ProxySourceModel proxySourceModel){
        proxyService.updateProxySourceEntityWithModel(proxySourceModel);
        return "redirect:setting";
    }

    @RequestMapping(value = "update-proxy-cache", method = RequestMethod.POST)
    public String updateProxyCache(Model model, ProxyCacheModel proxyCacheModel){
        proxyService.updateProxyCacheEntityWithModel(proxyCacheModel);
        return "redirect:setting";
    }

    @RequestMapping(value = "checking-save", method = RequestMethod.POST)
    public String checkingProxyDataSettings(Model model, ProxySelectModel proxySelectModel) throws IOException {
        proxyService.checkProxyDataSettingsWithModel(proxySelectModel);
        return "redirect:setting";
    }

    @RequestMapping("delete-proxy-layer/{id}")
    public String deleteProxyLayerByName(Model model, @PathVariable String id){
        proxyService.deleteProxyLayerEntityById(Long.parseLong(id));
        return "redirect:../setting";
    }

    @RequestMapping("delete-proxy-source/{id}")
    public String deleteProxySourceByName(Model model, @PathVariable String id){
        proxyService.deleteProxySourceEntityById(Long.parseLong(id));
        return "redirect:../setting";
    }

    @RequestMapping("delete-proxy-cache/{id}")
    public String deleteProxyCacheByName(Model model, @PathVariable String id){
        proxyService.deleteProxyCacheEntityById(Long.parseLong(id));
        return "redirect:../setting";
    }

    @RequestMapping("preview")
    public String proxyLayerPreviewPage(Model model) throws IOException {
        model.addAttribute("serviceURL", MapProxyUtil.getServiceURL());
        model.addAttribute("proxyLayers", proxyService.getProxyLayerEntities().get("data"));
        model.addAttribute("proxyYAML", mapProxyYamlComponent.getMapProxyYamlFileContext());
        return "page/cache/preview";
    }
}
