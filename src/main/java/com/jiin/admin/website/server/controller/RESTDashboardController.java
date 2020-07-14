package com.jiin.admin.website.server.controller;

import com.jiin.admin.servlet.AdminServerServlet;
import com.jiin.admin.vo.GeoDockerContainerInfo;
import com.jiin.admin.vo.ServerBasicPerformance;
import com.jiin.admin.vo.SynchronizeBasicInfo;
import com.jiin.admin.website.server.service.DashboardStatusService;
import com.jiin.admin.website.util.RestClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
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
        return dashboardStatusService.loadSyncBasicStatus((String) param.get("remoteIp"), Optional.of(Integer.parseInt((String) param.get("remoteBasicDBPort"))).orElse(POSTGRE_SQL_BASIC_PORT), Optional.of(Integer.parseInt((String) param.get("remoteFilePort"))).orElse(SYNCTHING_PORT));
    }

    @PostMapping("remote-performance")
    public ServerBasicPerformance postRemoteBasicPerformanceJSON(HttpServletRequest request, @RequestBody Map<String, Object> param){
        String ip = (String) param.get("ip");
        Map<String, Object> result = RestClientUtil.getREST(request.isSecure(), ip, String.format("%s/%s/api/dashboard/performance", request.getContextPath(), AdminServerServlet.CONTEXT_PATH), "");
        if (result.size() > 0) {
            ServerBasicPerformance performance = new ServerBasicPerformance();
            Class clazz = performance.getClass();
            for(String key : result.keySet()){
                Field field = null;
                try {
                    field = clazz.getDeclaredField(key);
                } catch (NoSuchFieldException e) {
                    log.error("ERROR - " + e.getMessage());
                }
                field.setAccessible(true);
                try {
                    field.set(performance, result.get(key));
                } catch (IllegalAccessException e) {
                    log.error("ERROR - " + e.getMessage());
                }
            }
            return performance;
        } else {
            return new ServerBasicPerformance();
        }
    }

    @PostMapping("remote-service-status")
    public Map<String, GeoDockerContainerInfo> postRemoteServiceStatusJSON(HttpServletRequest request, @RequestBody Map<String, Object> param){
        String ip = (String) param.get("ip");
        Map<String, Object> result = RestClientUtil.getREST(request.isSecure(), ip, String.format("%s/%s/api/dashboard/service-status", request.getContextPath(), AdminServerServlet.CONTEXT_PATH), "");
        Map<String, GeoDockerContainerInfo> output = new HashMap<>();
        if (result.size() > 0) {
            for (String key : result.keySet()) {
                GeoDockerContainerInfo container = new GeoDockerContainerInfo();
                Map<String, Object> data = (Map<String, Object>) result.get(key);
                container.setStatus((String) data.get("status"));
                container.setPort((Integer) data.get("port"));
                container.setName((String) data.get("name"));
                container.setRun((Boolean) data.get("run"));
                container.setRestart((Boolean) data.get("restart"));
                container.setDead((Boolean) data.get("dead"));
                output.put(key, container);
            }
            return output;
        } else {
            return new HashMap<>();
        }
    }

    @PostMapping("remote-sync-basic-status")
    public SynchronizeBasicInfo postSynchronizeBasicInfoJSON(HttpServletRequest request, @RequestBody Map<String, Object> param){
        String ip = (String) param.get("ip");
        Map<String, String> params = new HashMap<>();
        for(String key : param.keySet()){
            params.put(key, (String) param.get(key));
        }
        if (!param.keySet().contains("remoteIp")) {
            params.put("remoteIp", ip);
        }
        Map<String, Object> result = RestClientUtil.postREST(request.isSecure(), ip, String.format("%s/%s/api/dashboard/sync-basic-status", request.getContextPath(), AdminServerServlet.CONTEXT_PATH), params);
        if (result.size() > 0) {
            SynchronizeBasicInfo syncBasic = new SynchronizeBasicInfo();
            Class clazz = syncBasic.getClass();
            for(String key : result.keySet()){
                Field field = null;
                try {
                    field = clazz.getDeclaredField(key);
                } catch (NoSuchFieldException e) {
                    log.error("ERROR - " + e.getMessage());
                }
                field.setAccessible(true);
                try {
                    field.set(syncBasic, result.get(key));
                } catch (IllegalAccessException e) {
                    log.error("ERROR - " + e.getMessage());
                }
            }
            return syncBasic;
        } else {
            return new SynchronizeBasicInfo();
        }
    }
}
