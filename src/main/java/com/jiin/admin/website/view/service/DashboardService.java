package com.jiin.admin.website.view.service;

import com.jcraft.jsch.*;
import com.jiin.admin.entity.*;
import com.jiin.admin.website.model.ServerInfoModel;
import com.jiin.admin.website.server.mapper.CountMapper;
import com.jiin.admin.website.server.vo.DataCounter;
import com.jiin.admin.website.server.vo.ServerBasicPerformance;
import com.jiin.admin.website.view.mapper.AccountMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DashboardService {
    @Resource
    private CountMapper countMapper;

    @Resource
    private AccountMapper accountMapper;

    // 서버 설정을 위한 파일 (JSON, properties, yml) 혹은 공간 (DB) 은 협의 이후에 진행 필요.
    // 여기서 좌측 값을 입력하면, 우측 정보들이 나오는 방향으로 구현할 것.
    private static final Map<String, ServerInfoModel> serverMap = new HashMap<String, ServerInfoModel>(){{
        put("B1-Svr1", new ServerInfoModel("B1 SI Server 1", "SI", "192.168.1.141", "jiapp", "jiin0701!"));
        put("B1-Svr2", new ServerInfoModel("B1 SI Server 2", "SI", "192.168.1.142", "jiapp", "jiin0701!"));

        put("NB1-Svr1", new ServerInfoModel("B1 N-SI Server 1", "N-SI", "192.168.1.152", "jiapp", "jiin0701!"));
        put("NB1-Svr2", new ServerInfoModel("B1 N-SI Server 2", "N-SI", "192.168.1.153", "jiapp", "jiin0701!"));

        put("U3-Svr1", new ServerInfoModel("U3 SI Server 1", "SI", "192.168.1.155", "jiapp", "jiin0701!"));
        put("U3-Svr2", new ServerInfoModel("U3 SI Server 2", "SI", "192.168.1.156", "jiapp", "jiin0701!"));

        put("NU3-Svr1", new ServerInfoModel("U3 N-SI Server 1", "N-SI", "192.168.1.158", "jiapp", "jiin0701!"));
        put("NU3-Svr2", new ServerInfoModel("U3 N-SI Server 2", "N-SI", "192.168.1.159", "jiapp", "jiin0701!"));

        put("GOC-Svr1", new ServerInfoModel("GOC SI Server 1", "SI", "192.168.1.161", "jiapp", "jiin0701!"));
        put("GOC-Svr2", new ServerInfoModel("GOC SI Server 2", "SI", "192.168.1.162", "jiapp", "jiin0701!"));

        put("B1-CDS", new ServerInfoModel("B1 CDS", "CDS", "192.168.1.164", "jiapp", "jiin0701!"));
        put("U3-CDS", new ServerInfoModel("U3 CDS", "CDS", "192.168.1.165", "jiapp", "jiin0701!"));
    }};

    // SI 일 때 보여줄 정보들 (B1, U3, GOC) + CDS
    private static final Map<String, List<String>> SI_CONNECT = new HashMap<String, List<String>>(){{
        put("B1", new ArrayList<>(Arrays.asList("B1-Svr1:B1 SI Server 1", "B1-Svr2:B1 SI Server 2")));
        put("U3", new ArrayList<>(Arrays.asList("U3-Svr1:U3 SI Server 1", "U3-Svr2:U3 SI Server 2")));
        put("GOC", new ArrayList<>(Arrays.asList("GOC-Svr1:GOC SI Server 1", "GOC-Svr2:GOC SI Server 2")));
    }};

    // N-SI 일 때 보여줄 정보들 (B1, U3) + CDS
    private static final Map<String, List<String>> N_SI_CONNECT = new HashMap<String, List<String>>(){{
        put("B1", new ArrayList<>(Arrays.asList("NB1-Svr1:B1 N-SI Server 1", "NB1-Svr2:B1 N-SI Server 2")));
        put("U3", new ArrayList<>(Arrays.asList("NU3-Svr1:U3 N-SI Server 1", "NU3-Svr2:U3 N-SI Server 2")));
    }};

    private static final int SFTP_PORT = 22;
    private static final int BUFFER_SIZE = 1024;

    // Java 기반 Linux Shell 작동
    private String getShellCMD(ServerInfoModel serverInfo, String command) {
        Session session = null;
        Channel channel = null;
        InputStream is;
        StringBuffer sb = new StringBuffer();
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(serverInfo.getUsername(), serverInfo.getIpAddress(), SFTP_PORT);
            session.setPassword(serverInfo.getPassword());

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            channel = session.openChannel("exec");

            ChannelExec exec = (ChannelExec) channel;
            exec.setCommand(command);
            exec.setInputStream(null);

            is = channel.getInputStream(); // 출력 스트림
            byte[] buffer = new byte[BUFFER_SIZE];

            exec.connect();
            while(true){
                while (is.available() > 0) {
                    int i = is.read(buffer, 0, BUFFER_SIZE);
                    if (i < 0) break;
                    sb.append(new String(buffer, 0, i));
                }

                if (channel.isClosed()) {
                    if (is.available() > 0) continue;
                    break;
                }

                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
            return sb.toString();
        }
    }

    public ServerBasicPerformance getServerBasicPerformance(String key){
        ServerBasicPerformance sp = new ServerBasicPerformance();
        int errCnt = 0;

        // 서버 이름 설정
        sp.setServerName(serverMap.get(key).getName());

        // RAM 정보
        // 2번 : 전체, 3번 : 사용, 7번 : 사용 가능 (단위 : MB)
        String cpuRes = getShellCMD(serverMap.get(key), "free -m | awk \'NR == 2 {print $2,$3,$7}\'");
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
        String diskRes = getShellCMD(serverMap.get(key), "df -P | grep -v ^Filesystem | awk \'{sum_tot += $2; sum_used += $3; sum_rem += $4} END { print sum_tot/1024/1024, sum_used/1024/1024, sum_rem/1024/1024 }\'");
        if(diskRes != null){
            String[] split = diskRes.split(" ");
            if(split.length > 0 ) {
                sp.setTotalCapacity(Double.parseDouble(split[0].trim()));
                sp.setUsedCapacity(Double.parseDouble(split[1].trim()));
            } else {
                errCnt += 1;
            }
        }

        // CPU 정보
        String cpuUsage = getShellCMD(serverMap.get(key), "mpstat | tail -1 | awk \'{print 100-$NF}\'");
        if(cpuUsage != null){
            sp.setCpuUsage(Double.parseDouble(cpuUsage.trim()));
        } else {
            errCnt += 1;
        }

        // 사용자 접속 정보
        String connections = getShellCMD(serverMap.get(key), "who | awk \'END{print NR}\'");
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

    // 서버 기본 정보를 가져오는 함수
    public Map<String, ServerBasicPerformance> getFirstServerBasicPerformance(String type){
        Map<String, ServerBasicPerformance> map = new LinkedHashMap<>();
        if(type.equals("SI")){
            map.put("B1", getServerBasicPerformance("B1-Svr1"));
            map.put("U3", getServerBasicPerformance("U3-Svr1"));
            map.put("GOC", getServerBasicPerformance("GOC-Svr1"));
        } else {
            map.put("B1", getServerBasicPerformance("NB1-Svr1"));
            map.put("U3", getServerBasicPerformance("NU3-Svr1"));
        }
        return map;
    }

    // 연결 관계를 가져오는 함수
    public Map<String, List<String>> getConnectRelations(String type){
        Map<String, List<String>> map = new HashMap<>();
        Map<String, List<String>> based = type.equals("SI") ? SI_CONNECT : N_SI_CONNECT;
        for(String key : based.keySet()) {
            List<String> list = new ArrayList<>(based.get(key));
            if (key.equals("B1")) list.add("B1-CDS:B1 CDS");
            if (key.equals("U3")) list.add("U3-CDS:U3 CDS");
            map.put(key, list);
        }
        return map;
    }

    // 데이터 개수를 가져오는 함수
    public DataCounter getDataCounter(){
        DataCounter dataCounter = new DataCounter();
        dataCounter.setMapCount(countMapper.countByTableName(MapEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setSymbolCount(countMapper.countByTableName(MapSymbol.class.getAnnotation(Entity.class).name()));
        dataCounter.setVectorLayerCount(countMapper.countLayersByType("VECTOR"));
        dataCounter.setVectorLayerCount(countMapper.countLayersByType("RASTER"));
        dataCounter.setLayersProxyCount(countMapper.countByTableName(ProxyLayerEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setSourcesProxyCount(countMapper.countByTableName(ProxySourceEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setCachesProxyCount(countMapper.countByTableName(ProxyCacheEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setLayersSelectedProxyCount(countMapper.countByProxyDataSelectedName(ProxyLayerEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setSourcesSelectedProxyCount(countMapper.countByProxyDataSelectedName(ProxySourceEntity.class.getAnnotation(Entity.class).name()));
        dataCounter.setCachesSelectedProxyCount(countMapper.countByProxyDataSelectedName(ProxyCacheEntity.class.getAnnotation(Entity.class).name()));

        List<Map<String, Long>> accountCountList = new ArrayList<>();
        List<RoleEntity> roles = accountMapper.findAllRoles();
        roles.stream().forEach(r -> accountCountList.add(new HashMap<String, Long>() {{
            put(String.format("%s [%s]", r.getLabel(), r.getTitle()), countMapper.countAccountsByRoleId(r.getId()));
        }}));
        dataCounter.setUserCount(accountCountList);

        return dataCounter;
    }
}
