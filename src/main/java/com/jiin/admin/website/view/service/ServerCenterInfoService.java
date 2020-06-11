package com.jiin.admin.website.view.service;

import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.ServerCenterInfoModel;

import java.util.List;
import java.util.Map;

public interface ServerCenterInfoService {
    List<ServerCenterInfo> loadRemoteList();
    List<ServerCenterInfo> loadSameCenterList();
    Map<String, Object> loadDataMapZoneBase();
    ServerCenterInfo loadLocalInfoData();
    String[] loadZoneList();
    String[] loadKindList();
    boolean loadDataHasInFile(String name);
    boolean saveLocalData(ServerCenterInfoModel model);
    boolean saveRemoteData(ServerCenterInfoModel model);
    boolean removeDataByKey(String key);
}
