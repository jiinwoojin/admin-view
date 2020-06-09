package com.jiin.admin.website.view.service;

import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.ServerCenterInfoModel;

import java.util.List;
import java.util.Map;

public interface ServerCenterInfoService {
    List<ServerCenterInfo> loadRemoteList();
    Map<String, Object> loadDataMapZoneBase();
    ServerCenterInfo loadLocalInfoData();
    ServerCenterInfo loadRemoteInfoDataByKey(String key);
    String[] loadZoneList();
    String[] loadKindList();
    boolean loadDataHasInFile(String name);
    boolean saveLocalData(ServerCenterInfoModel model);
    boolean saveRemoteData(ServerCenterInfoModel model);
    boolean removeDataByKey(String key);

    List<ServerCenterInfo> sendServerInfoList(boolean isHTTP, ServerCenterInfo sentServer, String restContext);
    void sendDuplexRequest(boolean isHTTP, ServerCenterInfo sentServer, ServerCenterInfo targetServer, String restContext);
    void sendDuplexRequest(boolean isHTTP, List<ServerCenterInfo> sentServers, ServerCenterInfo targetServer, String restContext);
}
