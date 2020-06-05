package com.jiin.admin.website.server.controller;

import com.jiin.admin.vo.GeoDockerContainerInfo;
import com.jiin.admin.vo.ServerBasicPerformance;
import com.jiin.admin.vo.SynchronizeBasicInfo;
import com.jiin.admin.website.server.service.DashboardStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/dashboard")
public class RESTDashboardController {
    @Value("${project.server-port.postgresql-basic-port}")
    private int POSTGRE_SQL_BASIC_PORT;

    @Value("${project.server-port.syncthing-tcp-port}")
    private int SYNCTHING_PORT;

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

    @GetMapping("sync-basic-status")
    public SynchronizeBasicInfo getSynchronizeBasicInfoJSON(@RequestParam String remoteIp, @RequestParam Integer remoteBasicDBPort, @RequestParam Integer remoteFilePort) {
        return dashboardStatusService.loadSyncBasicStatus(remoteIp, Optional.of(remoteBasicDBPort).orElse(POSTGRE_SQL_BASIC_PORT), Optional.of(remoteFilePort).orElse(SYNCTHING_PORT));
    }
}
