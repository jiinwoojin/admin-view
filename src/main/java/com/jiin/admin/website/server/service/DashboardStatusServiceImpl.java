package com.jiin.admin.website.server.service;

import com.jiin.admin.Constants;
import com.jiin.admin.vo.ServerBasicPerformance;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.util.LinuxCommandUtil;
import com.jiin.admin.website.util.YAMLFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class DashboardStatusServiceImpl implements DashboardStatusService {
    @Value("${project.data-path}")
    private String dataPath;

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
}
