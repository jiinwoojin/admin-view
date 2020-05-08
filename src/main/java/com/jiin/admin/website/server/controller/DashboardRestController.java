package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.gis.StatusService;
import com.jiin.admin.website.server.vo.ServerBasicPerformance;
import com.jiin.admin.website.view.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("api/dashboard")
public class DashboardRestController {
    @Resource
    private DashboardService dashboardService;

    @Autowired
    private StatusService statusService;

    @GetMapping("performance/{serverName}")
    public ServerBasicPerformance serverBasicPerformanceJSON(@PathVariable String serverName){
        return dashboardService.getServerBasicPerformance(serverName);
    }

    @GetMapping("memory/{center}")
    public Map<String, String> memoryStatusJSON(@PathVariable String center){
        return statusService.centerServerMemorization(center);
    }
}
