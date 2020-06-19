package com.jiin.admin.website.server.service;

import com.jiin.admin.Constants;
import com.jiin.admin.vo.GeoDockerContainerInfo;
import com.jiin.admin.vo.ServerBasicPerformance;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.vo.SynchronizeBasicInfo;
import com.jiin.admin.website.util.DockerUtil;
import com.jiin.admin.website.util.LinuxCommandUtil;
import com.jiin.admin.website.util.SocketUtil;
import com.jiin.admin.website.util.YAMLFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DashboardStatusServiceImpl implements DashboardStatusService {
    @Value("${project.data-path}")
    private String dataPath;

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
    private int POSTGRE_SQL_OSM_PORT;

    @Value("${project.server-port.postgresql-basic-port}")
    private int POSTGRE_SQL_BASIC_PORT;

    @Value("${project.server-port.pg-pool-port}")
    private int PG_POOL_PORT;

    @Value("${project.server-port.syncthing-tcp-port}")
    private int SYNCTHING_TCP_PORT;

    // UDP 는 추가 여부 확인 이후에 반영할 것.

    /**
     * server-info.yaml 에 기재된 로컬 정보를 가져온다.
     * @param
     */
    private ServerCenterInfo loadLocalServerInfo() throws IOException {
        String path = dataPath + Constants.SERVER_INFO_FILE_PATH + "/" + Constants.SERVER_INFO_FILE_NAME;
        Map<String, Object> map = YAMLFileUtil.fetchMapByYAMLFile(new File(path));
        return ServerCenterInfo.convertDTO("local", (Map<String, Object>) map.get("local"));
    }

    /**
     * 대시보드 화면에 보여줄 서버의 성능 요소를 보여준다.
     * @param
     */
    @Override
    public ServerBasicPerformance loadLocalBasicPerformance() {
        ServerCenterInfo localInfo;
        try {
            localInfo = loadLocalServerInfo();
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
            return null;
        }

        ServerBasicPerformance sp = new ServerBasicPerformance();
        int errCnt = 0;

        // 서버 이름 설정
        sp.setServerName(localInfo.getName());

        // RAM 정보
        // 2번 : 전체, 3번 : 사용, 7번 : 사용 가능 (단위 : MB)
        String cpuRes = LinuxCommandUtil.fetchShellContextByLinuxCommand("free -m | awk \'NR == 2 {print $2,$3,$7}\'");
        if(cpuRes != null){
            String[] split = cpuRes.split(" ");
            if(split.length == 3) {
                sp.setTotalMemory(Long.parseLong(split[0].trim()));
                sp.setUsedMemory(Long.parseLong(split[1].trim()));
                sp.setAvailableMemory(Long.parseLong(split[2].trim()));
            } else {
                errCnt += 1;
            }
        }

        // Disk 정보
        // 2번 : 전체, 3번 : 사용, 4번 : 나머지 (단위 : GB)
        String diskRes = LinuxCommandUtil.fetchShellContextByLinuxCommand("df -P | grep -v ^Filesystem | awk \'{sum_tot += $2; sum_used += $3; sum_rem += $4} END { print sum_tot/1024/1024, sum_used/1024/1024, sum_rem/1024/1024 }\'");
        if(diskRes != null){
            String[] split = diskRes.split(" ");
            if(split.length > 0) {
                sp.setTotalCapacity(Double.parseDouble(split[0].trim()));
                sp.setUsedCapacity(Double.parseDouble(split[1].trim()));
            } else {
                errCnt += 1;
            }
        }

        // CPU 정보
        String cpuUsage = LinuxCommandUtil.fetchShellContextByLinuxCommand("mpstat | tail -1 | awk \'{print 100-$NF}\'");
        if(cpuUsage != null){
            sp.setCpuUsage(Double.parseDouble(cpuUsage.trim()));
        } else {
            errCnt += 1;
        }

        // 사용자 접속 정보 (NGINX 로 대체)
        sp.setConnections(-1);
    /*
        String connections = LinuxCommandUtil.fetchShellContextByLinuxCommand("who | awk \'END{print NR}\'");
        if(connections != null){
            sp.setConnections(Long.parseLong(connections.trim()));
        } else {
            errCnt += 1;
        }
    */

        if(errCnt == 4){
            sp.setStatus("OFF");
        } else if(errCnt != 0){
            sp.setStatus("ERROR");
        } else {
            sp.setStatus("ON");
        }

        return sp;
    }

    /**
     * 대시보드 화면에 보여줄 서비스 상태를 보여준다. With Docker Container
     * @param
     */
    @Override
    public Map<String, GeoDockerContainerInfo> loadGeoDockerContainerStatus() {
        Map<String, GeoDockerContainerInfo> map = new HashMap<>();
        List<Map<String, JsonObject>> containers = DockerUtil.fetchContainerMetaInfoByProperty("State");
        for(Map<String, JsonObject> ctn : containers){
            if(ctn.containsKey("/" + MAP_PROXY_NAME) || ctn.containsKey("/" + MAP_SERVER_NAME) || ctn.containsKey("/" + MAPNIK_NAME) || ctn.containsKey("/" + HEIGHT_NAME) || ctn.containsKey("/" + RABBIT_MQ_NAME) || ctn.containsKey("/" + NGINX_NAME)) {
                String utcPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'";
                SimpleDateFormat sdf = new SimpleDateFormat(utcPattern);

                for(String ctnKey : ctn.keySet()){
                    String key = ctnKey.replace("/", "");
                    key = key.toLowerCase();
                    JsonObject json = ctn.get(ctnKey);

                    Date startTime = null;
                    Date finishTime = null;
                    try {
                        startTime = sdf.parse(json.getString("StartedAt"));
                        finishTime = sdf.parse(json.getString("FinishedAt"));
                    } catch (ParseException e) {
                        log.error("ERROR - " + e.getMessage());
                    }

                    map.put(key, new GeoDockerContainerInfo(
                        key, json.getString("Status"), json.getBoolean("Running"), json.getBoolean("Restarting"), json.getBoolean("Dead"), startTime, finishTime
                    ));
                }
            }
        }
        return map;
    }

    /**
     * 대시보드 화면에 보여줄 데이터베이스 및 파일 동기화 상태를 보여준다.
     * @param remoteIP String, remoteDBPort int, remoteFilePort int
     */
    @Override
    public SynchronizeBasicInfo loadSyncBasicStatus(String remoteIP, int remoteDBPort, int remoteFilePort) {
        ServerCenterInfo localInfo;
        try {
            localInfo = loadLocalServerInfo();
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
            return null;
        }

        SynchronizeBasicInfo basicInfo = new SynchronizeBasicInfo();
        basicInfo.setServerName(localInfo.getName());

        // PostgreSQL OSM / Basic, PGPool2, Syncthing 기본 상태를 설정한다.
        basicInfo.setPgsqlOSMStatus(SocketUtil.loadIsTcpPortOpen(localInfo.getIp(), POSTGRE_SQL_OSM_PORT) ? "RUNNING" : "DEAD");
        basicInfo.setPgsqlBasicStatus(SocketUtil.loadIsTcpPortOpen(localInfo.getIp(), POSTGRE_SQL_BASIC_PORT) ? "RUNNING" : "DEAD");
        basicInfo.setPgpoolStatus(SocketUtil.loadIsTcpPortOpen(localInfo.getIp(), PG_POOL_PORT) ? "RUNNING" : "DEAD");
        basicInfo.setSyncthingStatus(SocketUtil.loadIsTcpPortOpen(localInfo.getIp(), SYNCTHING_TCP_PORT) ? "RUNNING" : "DEAD");

        // remoteIP, remoteDBPort, remoteFilePort 를 사용해서 접속한 서버와의 동기화 Status 를 확인한다.
        // remoteIP : A 서버 (요청), localIP : B 서버 (응답) 순으로 진행해야 함.
        // select * from pg_stat_wal_receiver; 해당 쿼리를 해서 결과가 있을 경우 해당 DB는 standby DB
        // select * from pg_stat_replication; 해당 쿼리를 해서 결과가 있을 경우 해당 DB는 primary DB
        basicInfo.setWithSyncDBStatus("UNKNOWN");
        basicInfo.setWithSyncFileStatus("UNKNOWN");

        return basicInfo;
    }
}
