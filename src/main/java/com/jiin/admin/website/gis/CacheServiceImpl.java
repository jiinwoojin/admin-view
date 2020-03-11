package com.jiin.admin.website.gis;

import com.jiin.admin.website.util.MapProxyUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CacheServiceImpl implements CacheService {
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
