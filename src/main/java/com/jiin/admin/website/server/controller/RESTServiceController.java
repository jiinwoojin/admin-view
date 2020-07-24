package com.jiin.admin.website.server.controller;

import com.jiin.admin.servlet.AdminServerServlet;
import com.jiin.admin.vo.GeoContainerInfo;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.util.DockerUtil;
import com.jiin.admin.website.util.RestClientUtil;
import com.jiin.admin.website.util.SocketUtil;
import com.jiin.admin.website.view.service.ContainerInfoService;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/service")
public class RESTServiceController {
    @Value("${server.servlet.context-path}")
    private String CONTEXT_PATH;

    @Resource
    private ServerCenterInfoService serverCenterInfoService;

    @Resource
    private ContainerInfoService containerInfoService;

    @PostMapping("extension-check")
    public Map<String, Object> getExtensionCheckByIpAndPort(@RequestBody Map<String, Object> param) {
        return new HashMap<String, Object>() {{
            put("result", SocketUtil.loadIsTcpPortOpen((String) param.get("ip"), (Integer) param.get("port")) ? "RUNNING" : "DEAD");
        }};
    }

    @PostMapping("extension-initialize-check")
    public Map<String, Object> getExtensionInitializeCheckByService(@RequestBody Map<String, Object> param) {
        Map<String, GeoContainerInfo> containers = containerInfoService.loadGeoServiceMap();
        return new HashMap<String, Object>() {{
            for (String key : containers.keySet()) {
                GeoContainerInfo container = containers.get(key);
                if(container.getPort() != 0) {
                    put(container.getName(), SocketUtil.loadIsTcpPortOpen((String) param.get("ip"), container.getPort()) ? "RUNNING" : "DEAD");
                }
            }
        }};
    }

    @GetMapping("docker-check")
    public Map<String, Object> getDockerCheckByContainerName(@RequestParam String name) throws IOException {
        JsonObject json = DockerUtil.loadContainerByNameAndProperty(name, "State");
        return new HashMap<String, Object>() {{
            if (json != null) {
                put("result", json.getJsonString("Status").getString().toUpperCase());
            } else {
                put("result", "UNKNOWN");
            }
        }};
    }

    @GetMapping("docker-initialize-check")
    public Map<String, Object> getDockerInitializeCheck() throws IOException {
        Map<String, GeoContainerInfo> containers = containerInfoService.loadGeoServiceMap();
        Map<String, Object> result = new HashMap<>();
        for (String key : containers.keySet()) {
            GeoContainerInfo container = containers.get(key);
            if (container.getPort() == 0) {
                JsonObject json = DockerUtil.loadContainerByNameAndProperty(container.getName(), "State");
                if (json != null) {
                    result.put(container.getName(), json.getJsonString("Status").getString().toUpperCase());
                } else {
                    result.put(container.getName(), "UNKNOWN");
                }
            }
        }
        return result;
    }

    @PostMapping("remote-docker-check")
    public Map<String, Object> postDockerCheckByIpAndContainerName(HttpServletRequest request, @RequestBody Map<String, String> param) {
        String ip = param.get("ip");
        String containerName = param.get("name");
        String path = String.format("%s/%s/api/service/docker-check", CONTEXT_PATH, AdminServerServlet.CONTEXT_PATH);
        String query = String.format("name=%s", containerName);
        return RestClientUtil.getREST(request.isSecure(), ip, path, query);
    }

    @PostMapping("remote-docker-initialize-check")
    public Map<String, Object> postDockerInitializeCheckByService(HttpServletRequest request, @RequestBody Map<String, String> param) {
        String ip = param.get("ip");
        String path = String.format("%s/%s/api/service/docker-initialize-check", CONTEXT_PATH, AdminServerServlet.CONTEXT_PATH);
        return RestClientUtil.getREST(request.isSecure(), ip, path, "");
    }

    @GetMapping("local-info")
    public ServerCenterInfo loadLocalServerCenterInfo(){
        return serverCenterInfoService.loadLocalInfoData();
    }
}
