package com.jiin.admin.website.server.controller;

import com.jiin.admin.servlet.AdminServerServlet;
import com.jiin.admin.website.util.DockerUtil;
import com.jiin.admin.website.util.RestClientUtil;
import com.jiin.admin.website.util.SocketUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("extension-check")
    public Map<String, Object> getExtensionCheckByIpAndPort(@RequestBody Map<String, Object> param) {
        return new HashMap<String, Object>() {{
            put("result", SocketUtil.loadIsTcpPortOpen((String) param.get("ip"), (Integer) param.get("port")) ? "RUNNING" : "DEAD");
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

    @PostMapping("remote-docker-check")
    public Map<String, Object> postDockerCheckByIpAndContainerName(HttpServletRequest request, @RequestBody Map<String, String> param) {
        String ip = param.get("ip");
        String containerName = param.get("name");
        String path = String.format("%s/%s/api/service/docker-check", CONTEXT_PATH, AdminServerServlet.CONTEXT_PATH, containerName);
        String query = String.format("name=%s", containerName);
        return RestClientUtil.getREST(request.isSecure(), ip, path, query);
    }
}
