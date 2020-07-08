package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.util.DockerUtil;
import com.jiin.admin.website.util.SocketUtil;
import org.springframework.web.bind.annotation.*;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/service")
public class RESTServiceController {
    @PostMapping("extension-check")
    public Map<String, Object> getExtensionCheckByIpAndPort(@RequestBody Map<String, Object> param){
        return new HashMap<String, Object>() {{
            put("result", SocketUtil.loadIsTcpPortOpen((String) param.get("ip"), (Integer) param.get("port")) ? "RUNNING" : "DEAD");
        }};
    }

    @GetMapping("docker-check")
    public Map<String, Object> getDockerCheckWithByContainerName(@RequestParam String name) throws IOException {
        JsonObject json = DockerUtil.loadContainerByNameAndProperty(name, "State");
        return new HashMap<String, Object>() {{
            if(json != null) {
                put("result", json.getJsonString("Status").getString().toUpperCase());
            } else {
                put("result", "UNKNOWN");
            }
        }};
    }
}
