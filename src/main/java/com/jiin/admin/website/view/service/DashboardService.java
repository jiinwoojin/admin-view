package com.jiin.admin.website.view.service;

import com.jcraft.jsch.*;
import com.jiin.admin.entity.*;
import com.jiin.admin.website.server.mapper.CountMapper;
import com.jiin.admin.website.server.vo.DataCounter;
import com.jiin.admin.website.server.vo.ServerBasicPerformance;
import com.jiin.admin.website.view.mapper.AccountMapper;
import com.jiin.admin.website.view.mapper.ServiceMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DashboardService {
    @Resource
    private CountMapper countMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private ServiceMapper serviceMapper;

    private static final int SFTP_PORT = 22;
    private static final int BUFFER_SIZE = 1024;

    // Java 기반 Linux Shell 작동 (개인 정보 취합 이후 80 포트로 서버 정보를 보내기 위한 코드)
    private String getLinuxShellWithCommand(String command){
        Process process = null;
        StringBuffer sb = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec(command);
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                String line;
                while((line = reader.readLine()) != null){
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    // 22번 포트 사용 불가를 대비하여 이 함수는 폐기 예정.
    private String getShellCMD(ServerConnectionEntity connection, String command) {
        Session session = null;
        Channel channel = null;
        InputStream is;
        StringBuffer sb = new StringBuffer();
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(connection.getUsername(), connection.getIpAddress(), SFTP_PORT);
            session.setPassword(connection.getPassword());

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

    public ServerBasicPerformance getServerBasicPerformance(ServerConnectionEntity connection){
        ServerBasicPerformance sp = new ServerBasicPerformance();
        int errCnt = 0;

        // 서버 이름 설정
        sp.setServerName(connection.getTitle());
        sp.setStatus("OFF");
        return sp;

        // 아래 문장은 점심 먹고 해결 할 생각.
//        ServerBasicPerformance sp = new ServerBasicPerformance();
//        int errCnt = 0;
//
//        // 서버 이름 설정
//        sp.setServerName(connection.getTitle());
//
//        // RAM 정보
//        // 2번 : 전체, 3번 : 사용, 7번 : 사용 가능 (단위 : MB)
//        String cpuRes = this.getLinuxShellWithCommand("free -m | awk \'NR == 2 {print $2,$3,$7}\'");
//        System.out.println(cpuRes);
//        if(cpuRes != null){
//            String[] split = cpuRes.split(" ");
//            if(split.length == 3) {
//                sp.setTotalMemory(Long.parseLong(split[0].trim()));
//                sp.setUsedMemory(Long.parseLong(split[1].trim()));
//                sp.setAvailableMemory(Long.parseLong(split[2].trim()));
//            } else {
//                errCnt += 1;
//            }
//        }
//
//        // Disk 정보
//        // 2번 : 전체, 3번 : 사용, 4번 : 나머지 (단위 : GB)
//        String diskRes = this.getLinuxShellWithCommand("df -P | grep -v ^Filesystem | awk \'{sum_tot += $2; sum_used += $3; sum_rem += $4} END { print sum_tot/1024/1024, sum_used/1024/1024, sum_rem/1024/1024 }\'");
//        System.out.println(diskRes);
//        if(diskRes != null){
//            String[] split = diskRes.split(" ");
//            if(split.length > 0 ) {
//                sp.setTotalCapacity(Double.parseDouble(split[0].trim()));
//                sp.setUsedCapacity(Double.parseDouble(split[1].trim()));
//            } else {
//                errCnt += 1;
//            }
//        }
//
//        // CPU 정보
//        String cpuUsage = this.getLinuxShellWithCommand("mpstat | tail -1 | awk \'{print 100-$NF}\'");
//        System.out.println(cpuUsage);
//        if(cpuUsage != null){
//            sp.setCpuUsage(Double.parseDouble(cpuUsage.trim()));
//        } else {
//            errCnt += 1;
//        }
//
//        // 사용자 접속 정보
//        String connections = this.getLinuxShellWithCommand("who | awk \'END{print NR}\'");
//        System.out.println(connections);
//        if(connections != null){
//            sp.setConnections(Long.parseLong(connections.trim()));
//        } else {
//            errCnt += 1;
//        }
//
//        if(errCnt == 4){
//            sp.setStatus("OFF");
//        } else if(errCnt != 0){
//            sp.setStatus("ERROR");
//        } else {
//            sp.setStatus("ON");
//        }
//
//        return sp;
    }

    // 자신의 서버에 해당하는 성능 정보를 가져온다. (지금은 리눅스만 가능)
    public ServerBasicPerformance getOwnServerBasicPerformance(){
        // 제공하는 서버가 윈도우가 아닌 경우에만 실행한다. (윈도우일 때, 제공 방안은 추후 개발 필요.)
        if(!System.getProperty("os.name").toLowerCase().contains("win")) {
            String ipAddr = getLinuxShellWithCommand("hostname -I");
            String[] split = ipAddr.split(" ");
            if(split.length > 0){
                ServerConnectionEntity connection = serviceMapper.findServerConnectionsByIpAddress(split[0]);
                if(connection != null) return this.getServerBasicPerformance(connection);
                else return new ServerBasicPerformance();
            } else return new ServerBasicPerformance();
        } else return new ServerBasicPerformance();
    }

    // 자신의 서버와 연결이 되는 목록을 가져온다. (지금은 리눅스만 가능)
    public List<ServerConnectionEntity> getOwnRelateConnectionsList() {
        // 제공하는 서버가 윈도우가 아닌 경우에만 실행한다. (윈도우일 때, 제공 방안은 추후 개발 필요.)
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            String ipAddr = getLinuxShellWithCommand("hostname -I");
            String[] split = ipAddr.split(" ");
            if (split.length > 0) {
                return serviceMapper.findOwnRelateConnectionsByIpAddress(split[0]);
            } else return new ArrayList<>();
        } else return new ArrayList<>();
    }

    // 자신의 서버와 연결이 되는 목록을 가져온다. (지금은 리눅스만 가능)
    public Map<String, ServerBasicPerformance> getOwnRelateConnectionsMap() {
        Map<String, ServerBasicPerformance> map = new LinkedHashMap<>();
        // 제공하는 서버가 윈도우가 아닌 경우에만 실행한다. (윈도우일 때, 제공 방안은 추후 개발 필요.)
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            String ipAddr = getLinuxShellWithCommand("hostname -I");
            String[] split = ipAddr.split(" ");
            if (split.length > 0) {
                ServerConnectionEntity ownConn = serviceMapper.findServerConnectionsByIpAddress(split[0]);
                for(ServerConnectionEntity connection : serviceMapper.findOwnRelateConnectionsByIpAddress(split[0])){
                    String own = ownConn.getKey();
                    String key = connection.getKey();

                    String ownCenter = own.split("-")[0];
                    String ownName = own.split("-")[1];

                    String center = key.split("-")[0];
                    String name = key.split("-")[1];

                    if(ownCenter.equals(center) && ownName.equals(name)) {
                        map.put(center.startsWith("N") ? center.substring(1) : center, this.getServerBasicPerformance(connection));
                    }
                    if(ownName.equals(name) && !ownCenter.equals(center)){
                        map.put(center.startsWith("N") ? center.substring(1) : center, this.getServerBasicPerformance(connection));
                    }
                }
                return map;
            } else return map;
        } else return map;
    }

    // 연결 관계를 가져오는 메소드 (지금은 리눅스만 가능)
    public Map<String, List<String>> getConnectRelations(){
        Map<String, List<String>> map = new LinkedHashMap<>();

        // 제공하는 서버가 윈도우가 아닌 경우에만 실행한다. (윈도우일 때, 제공 방안은 추후 개발 필요.)
        if(!System.getProperty("os.name").toLowerCase().contains("win")){
            String ipAddr = getLinuxShellWithCommand("hostname -I");
            String[] split = ipAddr.split(" ");
            if(split.length > 0){
                for(ServerConnectionEntity connection : serviceMapper.findOwnRelateConnectionsByIpAddress(split[0])){
                    String key = connection.getKey();
                    String center = key.split("-")[0];
                    if(center.startsWith("N")){
                        center = center.substring(1);
                    }

                    List<String> options = map.getOrDefault(center, new ArrayList<>());
                    options.add(String.format("%s:%s", connection.getKey(), connection.getTitle()));
                    map.put(center, options);
                }
            }
        }

        return map;
    }

    // 진행 예정 (동기화 모니터링)
//    private void getSyncthingWebInfo(String host, int port) throws IOException {
//        String url = String.format("http://%s:%d", host, port);
//        ProcessBuilder builder = new ProcessBuilder("curl -s http://192.168.1.141:8384/rest/stats/folder | json");
//
//        Connection.Response response;
//        StringBuffer sb = new StringBuffer();
//        try {
//            response = Jsoup.connect(url).method(Connection.Method.GET).execute();
//            Document doc = response.parse();
//            Elements elem = doc.select("div[id=\"folders\"]");
//            System.out.println(elem.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//
//        }
//    }

    // 진행 예정 (동기화 모니터링)
//    public SynchronizeBasicInfo getSyncBasicInfo(String key) throws IOException {
//        for(String k : serverMap.keySet()){
//            getSyncthingWebInfo(serverMap.get(k).getIpAddress(), 8384);
//        }
//
//        SynchronizeBasicInfo basicInfo = new SynchronizeBasicInfo();
//
//        return basicInfo;
//    }

    // Syncthing 으로 최신 파일 동기화 상태를 가져오는 메소드
    // 진행 예정 (동기화 모니터링)
//    public Map<String, List<SynchronizeBasicInfo>> getFirstSyncBasicInfo(String type) throws IOException {
//        Map<String, SynchronizeBasicInfo> map = new LinkedHashMap<>();
//        if(type.equals("SI")){
//            map.put("B1", getSyncBasicInfo("B1-Svr1"));
//            map.put("U3", getSyncBasicInfo("U3-Svr1"));
//            map.put("GOC", getSyncBasicInfo("GOC-Svr1"));
//        } else {
//            map.put("B1", getSyncBasicInfo("NB1-Svr1"));
//            map.put("U3", getSyncBasicInfo("NU3-Svr1"));
//        }
//        return null;
//    }

    // 데이터 개수를 가져오는 메소드
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
