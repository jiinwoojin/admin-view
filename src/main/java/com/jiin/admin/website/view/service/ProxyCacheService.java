package com.jiin.admin.website.view.service;

import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.*;

import java.util.List;

public interface ProxyCacheService {
    boolean saveYAMLFileByEachList(ServerCenterInfo local);

    String loadDataDir();
    String loadProxyCacheMainDir();
    String loadMapServerBinary();
    String loadProxyYamlSetting();

    Object loadDataList(String type);
    Object loadDataListBySelected(String type, Boolean selected);

    ProxySelectResponseModel loadProxySetting();

    boolean saveProxyLayerByModel(ProxyLayerModel proxyLayerModel, boolean synced);
    boolean saveProxySourceMapServerByModel(ProxySourceMapServerModel proxySourceMapServerModel, boolean synced);
    boolean saveProxySourceWMSByModel(ProxySourceWMSModel proxySourceWMSModel, ServerCenterInfo local, boolean synced);
    boolean saveProxyCacheByModel(ProxyCacheModel proxyCacheModel, boolean synced);
    boolean saveProxyGlobalByModelList(List<ProxyGlobalModel> proxyGlobalModels, ServerCenterInfo local);
    boolean removeProxyDataByIdAndType(long id, String type);
    boolean removeProxyDataByNameAndType(String name, String type);

    boolean setProxyDataSelectByModel(ProxySelectRequestModel proxySelectModel, ServerCenterInfo local);
}
