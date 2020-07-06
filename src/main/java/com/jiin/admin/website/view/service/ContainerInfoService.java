package com.jiin.admin.website.view.service;

import com.jiin.admin.dto.ContainerHistoryDTO;
import com.jiin.admin.vo.GeoBasicContainerInfo;
import com.jiin.admin.vo.GeoContainerInfo;

import java.util.List;
import java.util.Map;

public interface ContainerInfoService {
    List<ContainerHistoryDTO> loadContainerHistoryList();
    boolean removeAllContainerHistoryData();
    List<GeoBasicContainerInfo> loadGeoContainerInfoList();
    Map<String, GeoContainerInfo> loadGeoServiceMap();
    void executeGeoServiceByNameAndMethod(String name, String method);
}