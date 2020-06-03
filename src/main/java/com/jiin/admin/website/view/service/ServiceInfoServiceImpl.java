package com.jiin.admin.website.view.service;

import com.jiin.admin.vo.GeoContainerInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
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

    @Value("${project.server-port.postgresql-osm-port}")
    private int POSTGRESQL_OSM_PORT;

    @Value("${project.server-port.postgresql-basic-port}")
    private int POSTGRESQL_BASIC_PORT;

    @Value("${project.server-port.pg-pool-port}")
    private int PG_POOL_PORT;

    @Value("${project.server-port.terrain-port}")
    private int TERRAIN_SERVER_PORT;

    @Value("${project.server-port.vector-port}")
    private int TEGOLA_PORT;

    @Value("${project.server-port.syncthing-tcp-port}")
    private int SYNCTHING_PORT;

    /**
     * 대시보드 화면에서 Docker Container 이름을 기반으로 데이터를 추출하기 위한 Map 을 생성한다.
     * @param
     */
    @Override
    public Map<String, GeoContainerInfo> loadGeoContainerMap() {
        return new LinkedHashMap<String, GeoContainerInfo>() {{
            put("MapServer", new GeoContainerInfo(MAP_SERVER_NAME, "UNKNOWN", 0));
            put("MapProxy", new GeoContainerInfo(MAP_PROXY_NAME, "UNKNOWN", 0));
            put("Ji-in Height", new GeoContainerInfo(MAPNIK_NAME, "UNKNOWN", 0));
            put("RabbitMQ", new GeoContainerInfo(RABBIT_MQ_NAME, "UNKNOWN", 0));
            put("Mapnik", new GeoContainerInfo(MAPNIK_NAME, "UNKNOWN", 0));
            put("Nginx", new GeoContainerInfo(NGINX_NAME, "UNKNOWN", 0));
            put("PostgreSQL OSM", new GeoContainerInfo("postgresql_osm", "UNKNOWN", POSTGRESQL_OSM_PORT));
            put("PostgreSQL Basic", new GeoContainerInfo("postgresql_basic", "UNKNOWN", POSTGRESQL_BASIC_PORT));
            put("PGPool", new GeoContainerInfo("pgpool2", "UNKNOWN", PG_POOL_PORT));
            put("Tegola", new GeoContainerInfo("tegola", "UNKNOWN", TEGOLA_PORT));
            put("Terrain Server", new GeoContainerInfo("terrain_server", "UNKNOWN", TERRAIN_SERVER_PORT));
            put("Syncthing", new GeoContainerInfo("syncthing", "UNKNOWN", SYNCTHING_PORT));
        }};
    }
}
