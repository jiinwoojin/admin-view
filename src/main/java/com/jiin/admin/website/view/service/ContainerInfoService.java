package com.jiin.admin.website.view.service;

import com.jiin.admin.dto.ContainerHistoryDTO;
import com.jiin.admin.vo.GeoBasicContainerInfo;
import com.jiin.admin.vo.GeoContainerInfo;
import com.jiin.admin.website.model.ContainerExecuteModel;

import java.util.List;
import java.util.Map;

public interface ContainerInfoService {
    List<ContainerHistoryDTO> loadContainerHistoryList();
    boolean removeAllContainerHistoryData();
    Map<String, GeoContainerInfo> loadGeoServiceMap();
    void executeGeoServiceByContainerExecuteModel(ContainerExecuteModel containerExecuteModel);
}
