package com.jiin.admin.website.server.service;

import com.jiin.admin.vo.GeoDockerContainerInfo;
import com.jiin.admin.vo.ServerBasicPerformance;

import java.util.Map;

public interface DashboardStatusService {
    ServerBasicPerformance loadLocalBasicPerformance();
    Map<String, GeoDockerContainerInfo> loadGeoDockerContainerStatus();
}
