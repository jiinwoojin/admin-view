package com.jiin.admin.website.gis;

import com.jiin.admin.website.model.ProxySelectModel;

import java.util.Map;

public interface ProxySettingService {
    Map<String, Object> getProxyLayerEntities();
    Map<String, Object> getProxySourceEntities();
    Map<String, Object> getProxyCacheEntities();

    ProxySelectModel getCurrentMapProxySettings();

    Map<String, Object> getCachedRequestData();
    Map<String, Object> getCachedLayerData();
    Map<String, Object> getBoundingBoxInfoWithCrs();
}
