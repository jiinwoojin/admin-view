package com.jiin.admin.website.gis;

import com.jiin.admin.website.model.ProxySelectModel;
import com.jiin.admin.website.util.MapProxyUtil;
import com.jiin.admin.website.view.mapper.ProxyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProxySettingServiceImpl implements ProxySettingService {
    @Resource
    private ProxyMapper proxyMapper;

    @Override
    public Map<String, Object> getProxyLayerEntities() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", proxyMapper.findAllProxyLayerEntities());
        return result;
    }

    @Override
    public Map<String, Object> getProxySourceEntities() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", proxyMapper.findAllProxySourceEntities());
        return result;
    }

    @Override
    public Map<String, Object> getProxyCacheEntities() {
        Map<String, Object> result = new HashMap<>();
        result.put("data", proxyMapper.findAllProxyCacheEntities());
        return result;
    }

    @Override
    public ProxySelectModel getCurrentMapProxySettings() {
        return new ProxySelectModel(
            proxyMapper.findProxyLayerSelectedEntities().stream().map(o -> o.getName()).collect(Collectors.toList()),
            proxyMapper.findProxySourceSelectedEntities().stream().map(o -> o.getName()).collect(Collectors.toList()),
            proxyMapper.findProxyCacheSelectedEntities().stream().map(o -> o.getName()).collect(Collectors.toList())
        );
    }

    @Override
    public Map<String, Object> getCachedRequestData() {
        Map<String, Object> capability = MapProxyUtil.getCapabilities();
        Map<String, Object> wmsCapability = (Map<String, Object>) capability.get("WMS_Capabilities");
        Map<String, Object> mainRequest = (Map<String, Object>) wmsCapability.get("Capability");

        return (Map<String, Object>) mainRequest.get("Request");
    }

    @Override
    public Map<String, Object> getCachedLayerData() {
        Map<String, Object> capability = MapProxyUtil.getCapabilities();
        Map<String, Object> wmsCapability = (Map<String, Object>) capability.get("WMS_Capabilities");
        Map<String, Object> mainCapability = (Map<String, Object>) wmsCapability.get("Capability");

        return (Map<String, Object>) mainCapability.get("Layer");
    }

    @Override
    public Map<String, Object> getBoundingBoxInfoWithCrs() {
        Map<String, Object> capability = MapProxyUtil.getCapabilities();
        Map<String, Object> wmsCapability = (Map<String, Object>) capability.get("WMS_Capabilities");
        Map<String, Object> mainCapability = (Map<String, Object>) wmsCapability.get("Capability");
        Map<String, Object> layerData = (Map<String, Object>) mainCapability.get("Layer");

        List<Map<String, Object>> baseBoundingBoxList = (ArrayList<Map<String, Object>>) layerData.get("BoundingBox");
        Map<String, Object> boundingBoxMap = new HashMap<>();
        for(Map<String, Object> crs : baseBoundingBoxList){
            if(crs.keySet().contains("CRS"))
                boundingBoxMap.put((String) crs.get("CRS"), crs);
        }
        return boundingBoxMap;
    }
}
