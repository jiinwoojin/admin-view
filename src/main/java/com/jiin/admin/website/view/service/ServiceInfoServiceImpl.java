package com.jiin.admin.website.view.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceInfoServiceImpl implements ServiceInfoService {
    @Value("${project.docker-name.mapserver-name}")
    private String MAP_SERVER_NAME;

    @Value("${project.docker-name.mapproxy-name}")
    private String MAP_PROXY_NAME;

    @Value("${project.docker-name.mapnik-name}")
    private String MAPNIK_NAME;

    @Value("${project.docker-name.height-name}")
    private String HEIGHT_NAME;

    @Value("${project.docker-name.rabbitmq-name}")
    private String RABBIT_MQ_NAME;

    @Value("${project.docker-name.nginx-name}")
    private String NGINX_NAME;

    /**
     * 대시보드 화면에서 Docker Container 이름을 기반으로 데이터를 추출하기 위한 Map 을 생성한다.
     * @param
     */
    @Override
    public Map<String, String> loadGeoDockerContainerNameMap() {
        return new HashMap<String, String>() {{
            put("MAPSERVER", MAP_SERVER_NAME);
            put("MAPPROXY", MAP_PROXY_NAME);
            put("HEIGHT", HEIGHT_NAME);
            put("RABBITMQ", RABBIT_MQ_NAME);
            put("MAPNIK", MAPNIK_NAME);
            put("NGINX", NGINX_NAME);
        }};
    }
}
