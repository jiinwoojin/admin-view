package com.jiin.admin.website.view.component;

import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.util.RestClientUtil;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 서버 측에서 이중화 요청이 필요할 때, 각 이웃된 서버에게 REST API 를 날리기 위한 컴포넌트.
@Component
public class DuplexRESTComponent {
    @Value("${server.servlet.context-path}")
    private String CONTEXT_PATH;

    @Resource
    private ServerCenterInfoService serverCenterInfoService;

    public Map<String, Object> sendDuplexRESTWithData(HttpServletRequest request, String url, Map<String, String> data) {
        int success = 0;
        List<ServerCenterInfo> neighbors = serverCenterInfoService.loadNeighborList();
        for (ServerCenterInfo neighbor : neighbors) {
            Map<String, Object> map = RestClientUtil.postREST(request.isSecure(), neighbor.getIp(), CONTEXT_PATH + url, data);
            if (map != null) {
                success += (boolean) map.getOrDefault("result", false) ? 1 : 0;
            }
        }

        final int result = success;
        return new HashMap<String, Object>() {
            {
                put("success", result);
                put("failure", neighbors.size() - result);
            }
        };
    }
}
