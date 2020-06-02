package com.jiin.admin.website.server.controller;

import com.jiin.admin.vo.GeoDockerContainerInfo;
import com.jiin.admin.vo.ServerBasicPerformance;
import com.jiin.admin.vo.SynchronizeBasicInfo;
import com.jiin.admin.website.server.service.DashboardStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/dashboard")
public class RESTDashboardController {
    @Autowired
    private DashboardStatusService dashboardStatusService;

    @GetMapping("performance")
    public ServerBasicPerformance getLocalBasicPerformanceJSON(){
        return dashboardStatusService.loadLocalBasicPerformance();
    }

    @GetMapping("service-status")
    public Map<String, GeoDockerContainerInfo> getGeoServiceStatusJSON(){
        return dashboardStatusService.loadGeoDockerContainerStatus();
    }

    @GetMapping("synchronize")
    public SynchronizeBasicInfo getSynchronizeBasicInfoJSON(@PathVariable String serverName) throws IOException {
        // return dashboardService.getSyncBasicInfo(serverName);
        return null;
    }
}
