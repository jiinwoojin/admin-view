package com.jiin.admin.website.view.service;

import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ProxyCacheService {
    boolean saveYAMLFileByEachList(ServerCenterInfo local);

    String loadDataDir();
    String loadProxyCacheMainDir();
    String loadMapServerBinary();
    String loadProxyYamlSetting();

    Object loadDataList(String type);
    Object loadDataListBySelected(String type, Boolean selected);

    ProxySelectResponseModel loadProxySetting();

    Map<String, Object> writeYAMLFileForNeighbors(HttpServletRequest request, boolean result, ServerCenterInfo local, List<ServerCenterInfo> neighbors);
    boolean saveProxyLayerByModel(ProxyLayerModel proxyLayerModel, ServerCenterInfo local);
    boolean saveProxySourceMapServerByModel(ProxySourceMapServerModel proxySourceMapServerModel, ServerCenterInfo local);
    boolean saveProxySourceWMSByModel(ProxySourceWMSModel proxySourceWMSModel, ServerCenterInfo local);
    boolean saveProxyCacheByModel(ProxyCacheModel proxyCacheModel, ServerCenterInfo local);
    boolean saveProxyGlobalByModelList(List<ProxyGlobalModel> proxyGlobalModels, ServerCenterInfo local);
    boolean removeProxyDataByIdAndType(long id, String type, ServerCenterInfo local);
    boolean removeProxyDataByNameAndType(String name, String type, ServerCenterInfo local);

    boolean setProxyDataSelectByModel(ProxySelectRequestModel proxySelectModel, ServerCenterInfo local);
}
