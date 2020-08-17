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
    boolean loadDataSelectedById(String type, long id);
    boolean loadDataSelectedByName(String type, String name);

    Object loadDataList(String type);
    Object loadDataListBySelected(String type, Boolean selected);

    ProxySelectResponseModel loadProxySetting();

    Map<String, Object> writeYAMLFileForNeighbors(HttpServletRequest request, boolean result, ServerCenterInfo local, List<ServerCenterInfo> neighbors);
    boolean saveProxyLayerByModel(ProxyLayerModel proxyLayerModel);
    boolean saveProxySourceMapServerByModel(ProxySourceMapServerModel proxySourceMapServerModel);
    boolean saveProxySourceWMSByModel(ProxySourceWMSModel proxySourceWMSModel);
    boolean saveProxyCacheByModel(ProxyCacheModel proxyCacheModel);
    boolean saveProxyGlobalByModelList(List<ProxyGlobalModel> proxyGlobalModels, ServerCenterInfo local);
    boolean removeProxyDataByIdAndType(long id, String type);
    boolean removeProxyDataByNameAndType(String name, String type);

    boolean setProxyDataSelectByModel(ProxySelectRequestModel proxySelectModel, ServerCenterInfo local);
}
