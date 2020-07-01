package com.jiin.admin.website.view.service;

import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.ServerCenterInfoModel;

import java.util.List;
import java.util.Map;

public interface ServerCenterInfoService {
    String[] loadZoneList();
    String[] loadKindList();
    List<ServerCenterInfo> loadRemoteList();
    List<ServerCenterInfo> loadNeighborList();
    Map<String, Object> loadDataMapZoneBase();
    ServerCenterInfo loadLocalInfoData();
    boolean loadDataHasInFile(String name);
    boolean saveLocalData(ServerCenterInfoModel model);
    boolean saveRemoteData(ServerCenterInfoModel model);
    boolean removeDataByKey(String key);
}
