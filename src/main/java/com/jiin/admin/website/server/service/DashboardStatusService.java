package com.jiin.admin.website.server.service;

import com.jiin.admin.vo.GeoDockerContainerInfo;
import com.jiin.admin.vo.ServerBasicPerformance;
import com.jiin.admin.vo.SynchronizeBasicInfo;

import java.util.Map;

public interface DashboardStatusService {
    ServerBasicPerformance loadLocalBasicPerformance();
    Map<String, GeoDockerContainerInfo> loadGeoDockerContainerStatus();
    SynchronizeBasicInfo loadSyncBasicStatus(String remoteIP, int remoteBasicDBPort, int remoteFilePort);
}
