package com.jiin.admin.website.server.controller;

import com.jiin.admin.entity.ServerConnectionEntity;
import com.jiin.admin.website.gis.StatusService;
import com.jiin.admin.website.server.vo.ServerBasicPerformance;
import com.jiin.admin.website.server.vo.SynchronizeBasicInfo;
import com.jiin.admin.website.view.service.ServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/dashboard")
public class DashboardRestController {
    @Resource
    private ServerInfoService serverInfoService;

    @Autowired
    private StatusService statusService;

    @GetMapping("performance")
    public ServerBasicPerformance getOwnBasicServerPerformanceJSON(){
        return serverInfoService.getOwnServerBasicPerformance();
    }

    @GetMapping("connection/{key}")
    public ServerConnectionEntity getAnotherConnectionJSON(@PathVariable String key){
        return serverInfoService.getAnotherConnection(key);
    }

    @GetMapping("sync-brief/{serverName}")
    public SynchronizeBasicInfo synchronizeBasicInfoJSON(@PathVariable String serverName) throws IOException {
        return null;
        // return dashboardService.getSyncBasicInfo(serverName);
    }

    @GetMapping("memory/{center}")
    public Map<String, String> memoryStatusJSON(@PathVariable String center){
        return statusService.centerServerMemorization(center);
    }
}
