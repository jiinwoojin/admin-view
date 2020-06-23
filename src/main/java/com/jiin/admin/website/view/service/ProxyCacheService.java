package com.jiin.admin.website.view.service;

import com.jiin.admin.website.model.*;

public interface ProxyCacheService {
    String loadDataDir();
    String loadProxyCacheMainDir();
    String loadMapServerBinary();
    String loadProxyYamlSetting();

    Object loadDataList(String type);
    Object loadDataListBySelected(String type, Boolean selected);

    ProxySelectModel loadProxySetting();

    boolean saveProxyLayerByModel(ProxyLayerModel proxyLayerModel);
    boolean saveProxySourceMapServerByModel(ProxySourceMapServerModel proxySourceMapServerModel);
    boolean saveProxySourceWMSByModel(ProxySourceWMSModel proxySourceWMSModel);
    boolean saveProxyCacheByModel(ProxyCacheModel proxyCacheModel);

    boolean removeProxyDataByIdAndType(long id, String type);

    boolean setProxyDataSelectByModel(ProxySelectModel proxySelectModel);
}
