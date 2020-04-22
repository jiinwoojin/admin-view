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
import javax.transaction.Transactional;
import java.io.IOException;

@Controller
@RequestMapping("cache")
public class ProxyMainController {
    @Autowired
    private ProxySettingService proxyService;

    @Resource
    private MapProxyYamlComponent mapProxyYamlComponent;

    // Proxy Layer 미리보기 페이지 : DB Layer Reading (설정 값으로 되어 있는 경우만)
    @RequestMapping("preview")
    public String proxyLayerPreviewPage(Model model) throws IOException {
        model.addAttribute("serviceURL", MapProxyUtil.getServiceURL());
        model.addAttribute("proxyLayers", proxyService.getProxyLayerEntitiesIsSelected().get("data"));
        model.addAttribute("proxyYAML", mapProxyYamlComponent.getMapProxyYamlFileContext());
        return "page/cache/preview";
    }

    // Proxy 데이터 설정
    @RequestMapping("setting")
    public String cacheDataSetting(Model model){
        model.addAttribute("layerMap", proxyService.getProxyLayerEntities());
        model.addAttribute("sourceMap", proxyService.getProxySourceEntities());
        model.addAttribute("cacheMap", proxyService.getProxyCacheEntities());

        model.addAttribute("addProxyLayer", proxyService.initializeProxyLayerModel());
        model.addAttribute("addProxySource", proxyService.initializeProxySourceModel());
        model.addAttribute("addProxyCache", proxyService.initializeProxyCacheModel());

        model.addAttribute("proxySources", proxyService.getProxySourceEntitiesIsSelected().get("data"));
        model.addAttribute("proxyCaches", proxyService.getProxyCacheEntitiesIsSelected().get("data"));

        model.addAttribute("proxyCacheDirectory", proxyService.getProxyCacheDirectoryPath());
        model.addAttribute("dataDirectory", proxyService.getDataDirectoryPath());

        return "page/cache/setting";
    }

    // Proxy Layer 데이터 추가
    @RequestMapping(value = "add-proxy-layer", method = RequestMethod.POST)
    public String addProxyLayer(Model model, ProxyLayerModel proxyLayerModel){
        proxyService.createProxyLayerEntityWithModel(proxyLayerModel);
        return "redirect:setting";
    }

    // Proxy Source 데이터 추가
    @RequestMapping(value = "add-proxy-source", method = RequestMethod.POST)
    public String addProxySource(Model model, ProxySourceModel proxySourceModel){
        proxyService.createProxySourceEntityWithModel(proxySourceModel);
        return "redirect:setting";
    }

    // Proxy Cache 데이터 추가
    @RequestMapping(value = "add-proxy-cache", method = RequestMethod.POST)
    public String addProxyCache(Model model, ProxyCacheModel proxyCacheModel){
        proxyService.createProxyCacheEntityWithModel(proxyCacheModel);
        return "redirect:setting";
    }

    // Proxy Layer 데이터 수정
    @Transactional
    @RequestMapping(value = "update-proxy-layer", method = RequestMethod.POST)
    public String updateProxyLayer(Model model, ProxyLayerModel proxyLayerModel) throws IOException {
        proxyService.updateProxyLayerEntityWithModel(proxyLayerModel);
        ProxySelectModel select = proxyService.getCurrentMapProxySettings();
        if(select.getLayers().contains(proxyLayerModel.getProxyLayerName()))
            proxyService.checkProxyDataSettingsWithModel(select);
        return "redirect:setting";
    }

    // Proxy Source 데이터 수정
    @Transactional
    @RequestMapping(value = "update-proxy-source", method = RequestMethod.POST)
    public String updateProxySource(Model model, ProxySourceModel proxySourceModel) throws IOException {
        proxyService.updateProxySourceEntityWithModel(proxySourceModel);
        ProxySelectModel select = proxyService.getCurrentMapProxySettings();
        if(select.getSources().contains(proxySourceModel.getProxySourceName()))
            proxyService.checkProxyDataSettingsWithModel(select);
        return "redirect:setting";
    }

    // Proxy Cache 데이터 수정
    @Transactional
    @RequestMapping(value = "update-proxy-cache", method = RequestMethod.POST)
    public String updateProxyCache(Model model, ProxyCacheModel proxyCacheModel) throws IOException {
        proxyService.updateProxyCacheEntityWithModel(proxyCacheModel);
        ProxySelectModel select = proxyService.getCurrentMapProxySettings();
        if(select.getCaches().contains(proxyCacheModel.getProxyCacheName()))
            proxyService.checkProxyDataSettingsWithModel(select);
        return "redirect:setting";
    }

    // Proxy Layer 데이터 삭제
    @Transactional
    @RequestMapping("delete-proxy-layer/{id}")
    public String deleteProxyLayerByName(Model model, @PathVariable String id) throws IOException {
        proxyService.deleteProxyLayerEntityById(Long.parseLong(id));
        proxyService.checkProxyDataSettingsWithModel(proxyService.getCurrentMapProxySettings());
        return "redirect:../setting";
    }

    // Proxy Source 데이터 삭제
    @RequestMapping("delete-proxy-source/{id}")
    public String deleteProxySourceByName(Model model, @PathVariable String id) throws IOException {
        proxyService.deleteProxySourceEntityById(Long.parseLong(id));
        proxyService.checkProxyDataSettingsWithModel(proxyService.getCurrentMapProxySettings());
        return "redirect:../setting";
    }

    // Proxy Cache 데이터 삭제
    @RequestMapping("delete-proxy-cache/{id}")
    public String deleteProxyCacheByName(Model model, @PathVariable String id) throws IOException {
        proxyService.deleteProxyCacheEntityById(Long.parseLong(id));
        proxyService.checkProxyDataSettingsWithModel(proxyService.getCurrentMapProxySettings());
        return "redirect:../setting";
    }

    // Map Proxy 설정 데이터 저장 (layers, sources, caches 데이터 체크)
    @RequestMapping(value = "checking-save", method = RequestMethod.POST)
    public String checkingProxyDataSettings(Model model, ProxySelectModel proxySelectModel) throws IOException {
        proxyService.checkProxyDataSettingsWithModel(proxySelectModel);
        return "redirect:setting";
    }
}
