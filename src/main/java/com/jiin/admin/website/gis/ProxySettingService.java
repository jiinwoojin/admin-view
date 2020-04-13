package com.jiin.admin.website.gis;

import com.jiin.admin.website.model.ProxyCacheModel;
import com.jiin.admin.website.model.ProxyLayerModel;
import com.jiin.admin.website.model.ProxySelectModel;
import com.jiin.admin.website.model.ProxySourceModel;

import java.util.Map;

public interface ProxySettingService {
    Map<String, Object> getProxyLayerEntities();
    Map<String, Object> getProxySourceEntities();
    Map<String, Object> getProxyCacheEntities();

    ProxySelectModel getCurrentMapProxySettings();

    Map<String, Object> getCachedRequestData();
    Map<String, Object> getCachedLayerData();
    Map<String, Object> getBoundingBoxInfoWithCrs();

    boolean createProxyLayerEntityWithModel(ProxyLayerModel proxyLayerModel);
    boolean createProxySourceEntityWithModel(ProxySourceModel proxySourceModel);
    boolean createProxyCacheEntityWithModel(ProxyCacheModel proxyCacheModel);

    boolean updateProxyLayerEntityWithModel(ProxyLayerModel proxyLayerModel);
    boolean updateProxySourceEntityWithModel(ProxySourceModel proxySourceModel);
    boolean updateProxyCacheEntityWithModel(ProxyCacheModel proxyCacheModel);

    void deleteProxyLayerEntityById(long id);
    void deleteProxySourceEntityById(long id);
    void deleteProxyCacheEntityById(long id);
}
