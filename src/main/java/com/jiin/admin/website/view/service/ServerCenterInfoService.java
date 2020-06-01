package com.jiin.admin.website.view.service;

import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.ServerCenterInfoModel;

import java.util.List;

public interface ServerCenterInfoService {
    List<ServerCenterInfo> loadDataList();
    String[] loadZoneList();
    String[] loadKindList();
    boolean loadDataHasInFile(String name);
    boolean saveData(ServerCenterInfoModel model);
    boolean removeDataByName(String name);
}
