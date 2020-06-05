package com.jiin.admin.website.server.service;

import com.jiin.admin.Constants;
import com.jiin.admin.vo.GeoDockerContainerInfo;
import com.jiin.admin.vo.ServerBasicPerformance;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.util.DockerUtil;
import com.jiin.admin.website.util.LinuxCommandUtil;
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
            log.error("로컬 정보를 가져올 수 없습니다.");
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

        // 사용자 접속 정보
        String connections = LinuxCommandUtil.fetchShellContextByLinuxCommand("who | awk \'END{print NR}\'");
        if(connections != null){
            sp.setConnections(Long.parseLong(connections.trim()));
        } else {
            errCnt += 1;
        }

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
                        log.error("날짜 파싱 오류입니다. 다시 시도 바랍니다.");
                    }

                    map.put(key, new GeoDockerContainerInfo(
                        key, json.getString("Status"), json.getBoolean("Running"), json.getBoolean("Restarting"), json.getBoolean("Dead"), startTime, finishTime
                    ));
                }
            }
        }
        return map;
    }
}
