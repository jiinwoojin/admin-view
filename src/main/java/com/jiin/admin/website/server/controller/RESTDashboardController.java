package com.jiin.admin.website.server.controller;

import com.jiin.admin.vo.GeoDockerContainerInfo;
import com.jiin.admin.vo.ServerBasicPerformance;
import com.jiin.admin.vo.SynchronizeBasicInfo;
import com.jiin.admin.website.server.service.DashboardStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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
    public ServerBasicPerformance getLocalBasicPerformanceJSON() {
        return dashboardStatusService.loadLocalBasicPerformance();
    }

    @GetMapping("service-status")
    public Map<String, GeoDockerContainerInfo> getGeoServiceStatusJSON() {
        return dashboardStatusService.loadGeoDockerContainerStatus();
    }

    @PostMapping("sync-basic-status")
    public SynchronizeBasicInfo getSynchronizeBasicInfoJSON(@RequestBody Map<String, Object> param) {
        return dashboardStatusService.loadSyncBasicStatus((String) param.get("remoteIp"), Optional.of((Integer) param.get("remoteBasicDBPort")).orElse(POSTGRE_SQL_BASIC_PORT), Optional.of((Integer) param.get("remoteFilePort")).orElse(SYNCTHING_PORT));
    }
}
