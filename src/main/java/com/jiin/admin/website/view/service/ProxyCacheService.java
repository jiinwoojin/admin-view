package com.jiin.admin.website.view.service;

import com.jiin.admin.website.model.*;

import java.util.List;

public interface ProxyCacheService {
    String loadDataDir();
    String loadProxyCacheMainDir();
    String loadMapServerBinary();
    String loadProxyYamlSetting();

    Object loadDataList(String type);
    Object loadDataListBySelected(String type, Boolean selected);

    ProxySelectResponseModel loadProxySetting();

    boolean saveProxyLayerByModel(ProxyLayerModel proxyLayerModel);
    boolean saveProxySourceMapServerByModel(ProxySourceMapServerModel proxySourceMapServerModel);
    boolean saveProxySourceWMSByModel(ProxySourceWMSModel proxySourceWMSModel);
    boolean saveProxyCacheByModel(ProxyCacheModel proxyCacheModel);
    boolean saveProxyGlobalByModelList(List<ProxyGlobalModel> proxyGlobalModels);
    boolean removeProxyDataByIdAndType(long id, String type);

    boolean setProxyDataSelectByModel(ProxySelectRequestModel proxySelectModel);
}
