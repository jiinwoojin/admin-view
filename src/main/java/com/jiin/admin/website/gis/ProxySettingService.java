package com.jiin.admin.website.gis;

import com.jiin.admin.website.model.ProxyCacheModel;
import com.jiin.admin.website.model.ProxyLayerModel;
import com.jiin.admin.website.model.ProxySelectModel;
import com.jiin.admin.website.model.ProxySourceModel;

import java.io.IOException;
import java.util.Map;

public interface ProxySettingService {
    Map<String, Object> getProxyLayerEntities();
    Map<String, Object> getProxySourceEntities();
    Map<String, Object> getProxyCacheEntities();

    Map<String, Object> getProxyLayerEntitiesIsSelected();
    Map<String, Object> getProxySourceEntitiesIsSelected();
    Map<String, Object> getProxyCacheEntitiesIsSelected();

    ProxySelectModel getCurrentMapProxySettings();
    ProxyLayerModel initializeProxyLayerModel();
    ProxySourceModel initializeProxySourceModel();
    ProxyCacheModel initializeProxyCacheModel();

    boolean createProxyLayerEntityWithModel(ProxyLayerModel proxyLayerModel);
    boolean createProxySourceEntityWithModel(ProxySourceModel proxySourceModel);
    boolean createProxyCacheEntityWithModel(ProxyCacheModel proxyCacheModel);

    boolean updateProxyLayerEntityWithModel(ProxyLayerModel proxyLayerModel);
    boolean updateProxySourceEntityWithModel(ProxySourceModel proxySourceModel);
    boolean updateProxyCacheEntityWithModel(ProxyCacheModel proxyCacheModel);

    void deleteProxyLayerEntityById(long id);
    void deleteProxySourceEntityById(long id);
    void deleteProxyCacheEntityById(long id);

    void checkProxyDataSettingsWithModel(ProxySelectModel proxySelectModel) throws IOException;
}
