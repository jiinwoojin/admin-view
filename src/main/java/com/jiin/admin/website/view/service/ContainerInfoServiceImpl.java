package com.jiin.admin.website.view.service;

import com.jiin.admin.dto.ContainerHistoryDTO;
import com.jiin.admin.mapper.data.ContainerHistoryMapper;
import com.jiin.admin.vo.GeoBasicContainerInfo;
import com.jiin.admin.vo.GeoContainerInfo;
import com.jiin.admin.website.model.ContainerExecuteModel;
import com.jiin.admin.website.util.DockerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.json.JsonObject;
import java.io.IOException;
import java.util.*;

// import com.jiin.admin.website.util.LinuxCommandUtil;

@Slf4j
@Service
public class ContainerInfoServiceImpl implements ContainerInfoService {
    @Resource
    private ContainerHistoryMapper containerHistoryMapper;

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
     * Container 의 원래 소프트웨어 이름을 가져온다.
     * @param name String
     */
    private String loadGISSoftwareNameByContainerName(String name) {
        if (name.equalsIgnoreCase(MAP_SERVER_NAME)) {
            return "MapServer";
        } else if (name.equalsIgnoreCase(MAP_PROXY_NAME)) {
            return "MapProxy";
        } else if (name.equalsIgnoreCase(MAPNIK_NAME)) {
            return "Mapnik";
        } else if (name.equalsIgnoreCase(HEIGHT_NAME)) {
            return "Ji-in Height";
        } else if (name.equalsIgnoreCase(RABBIT_MQ_NAME)) {
            return "RabbitMQ";
        } else if (name.equalsIgnoreCase(NGINX_NAME)) {
            return "NGINX";
        } else {
            return "UNKNOWN";
        }
    }

    /**
     * Container 제어 관련 History 목록을 DB 에서 가져온다.
     * @param
     */
    @Override
    public List<ContainerHistoryDTO> loadContainerHistoryList() {
        return containerHistoryMapper.findAll();
    }

    @Override
    public boolean removeAllContainerHistoryData() {
        return containerHistoryMapper.deleteAll() > 0;
    }

    /**
     * Docker Container 및 다른 서비스들 이름을 기반으로 데이터를 추출하기 위한 Map 을 생성한다.
     * @param
     */
    @Override
    public Map<String, GeoContainerInfo> loadGeoServiceMap() {
        return new LinkedHashMap<String, GeoContainerInfo>() {{
            put("MapServer", new GeoContainerInfo(MAP_SERVER_NAME, "UNKNOWN", 0));
            put("MapProxy", new GeoContainerInfo(MAP_PROXY_NAME, "UNKNOWN", 0));
            put("Ji-in Height", new GeoContainerInfo(HEIGHT_NAME, "UNKNOWN", 0));
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

    /**
     * 서비스 이름과 기능, 호스트 네임을 입력해서 해당 서비스에 대한 명령을 실행한다.
     * @param model ContainerExecuteModel
     */
    @Override
    public void executeGeoServiceByContainerExecuteModel(ContainerExecuteModel model) {
        String service = model.getService();
        String command = model.getCommand();
        // Docker Container 대응
        if (service.equalsIgnoreCase(MAP_SERVER_NAME) || service.equalsIgnoreCase(MAP_PROXY_NAME) || service.equalsIgnoreCase(MAPNIK_NAME) || service.equalsIgnoreCase(HEIGHT_NAME) || service.equalsIgnoreCase(RABBIT_MQ_NAME) || service.equalsIgnoreCase(NGINX_NAME)) {
            try {
                DockerUtil.executeContainerByNameAndMethod(service, command);
                if (command.equalsIgnoreCase("START") || command.equalsIgnoreCase("STOP")) {
                    JsonObject json = DockerUtil.loadContainerByNameAndProperty(service, "State");
                    boolean succeed = true;
                    String status = json.getString("Status");
                    if ((command.equalsIgnoreCase("START") && !status.equalsIgnoreCase("RUNNING")) || (command.equalsIgnoreCase("STOP") && !status.equalsIgnoreCase("EXITED"))) {
                        succeed = !succeed;
                    }

                    containerHistoryMapper.insert(new ContainerHistoryDTO(
                        containerHistoryMapper.findNextSeqVal(),
                        loadGISSoftwareNameByContainerName(service),
                        command,
                        succeed,
                        model.getHostname(),
                        model.getUser(),
                        new Date()
                    ));
                }
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
                if (command.equalsIgnoreCase("START") || command.equalsIgnoreCase("STOP")) {
                    containerHistoryMapper.insert(new ContainerHistoryDTO(
                        containerHistoryMapper.findNextSeqVal(),
                        loadGISSoftwareNameByContainerName(service),
                        command,
                        false,
                        model.getHostname(),
                        model.getUser(),
                        new Date()
                    ));
                }
            }
        } else {
            // 해당 서비스에 대한 리셋 명령어 (아마 종료 명령어와 시작 명령어 동시일 거라 생각)
            // LinuxCommandUtil.fetchShellContextByLinuxCommand("cd /");
        }
    }
}
