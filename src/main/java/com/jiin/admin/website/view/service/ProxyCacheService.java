package com.jiin.admin.website.view.service;

import com.jiin.admin.website.model.ProxyCacheModelV2;
import com.jiin.admin.website.model.ProxyLayerModelV2;
import com.jiin.admin.website.model.ProxySelectModel;
import com.jiin.admin.website.model.ProxySourceModelV2;

public interface ProxyCacheService {
    String loadDataDir();
    String loadProxyMainDir();
    String loadMapServerBinary();

    Object loadDataList(String type);
    Object loadDataListBySelected(String type, Boolean selected);
    Object loadDataModel(String type);

    ProxySelectModel loadProxySetting();

    boolean saveProxyLayerByModel(ProxyLayerModelV2 proxyLayerModelV2);
    boolean saveProxySourceByModel(ProxySourceModelV2 proxySourceModelV2);
    boolean saveProxyCacheByModel(ProxyCacheModelV2 proxyCacheModelV2);

    boolean removeProxyDataByIdAndType(long id, String type);

    boolean setProxyDataSelectByModel(ProxySelectModel proxySelectModel);
}
