package com.jiin.admin.website.view.service;

import com.jiin.admin.entity.*;
import com.jiin.admin.entity.ServicePortEntity;
import com.jiin.admin.website.model.ServerConnectionModel;
import com.jiin.admin.website.model.ServerFormModel;
import com.jiin.admin.website.model.ServerRelationModel;
import com.jiin.admin.website.model.ServicePortModel;
import com.jiin.admin.website.server.mapper.CountMapper;
import com.jiin.admin.website.server.vo.DataCounter;
import com.jiin.admin.website.server.vo.GeoManagerProcessInfo;
import com.jiin.admin.website.server.vo.ServerBasicPerformance;
import com.jiin.admin.website.view.mapper.AccountMapper;
import com.jiin.admin.website.view.mapper.ServiceMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class ServerInfoService {
    @Resource
    private CountMapper countMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private ServiceMapper serviceMapper;

    // private static final int SFTP_PORT = 22;
    // private static final int BUFFER_SIZE = 1024;

    // Java 기반 Linux Shell 작동 (개인 정보 취합 이후 80 포트로 서버 정보를 보내기 위한 코드)
    private String getLinuxShellWithCommand(String command){
        Process process = null;
        StringBuffer sb = new StringBuffer();
        try {
            // 이걸 해 줘야 파이프(|) 관련 문제를 안 겪는다. 구현 시 참고할 것.
            process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", command });
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

    // 리눅스에서 자신의 메인 서버 IP 주소를 가져온다.
    private String getOwnIpAddressInLinux(){
        if(!System.getProperty("os.name").toLowerCase().contains("win")) {
            String ipAddr = getLinuxShellWithCommand("hostname -I");
            String[] split = ipAddr.split(" ");
            if(split.length > 0) return split[0];
        }
        return null;
    }

//    // 22번 포트 사용을 지원하지 않아 이 함수는 폐기.
//    private String getShellCMD(ServerConnectionEntity connection, String command) {
//        Session session = null;
//        Channel channel = null;
//        InputStream is;
//        StringBuffer sb = new StringBuffer();
//        try {
//            JSch jsch = new JSch();
//            session = jsch.getSession(connection.getUsername(), connection.getIpAddress(), SFTP_PORT);
//            session.setPassword(connection.getPassword());
//
//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking", "no");
//            session.setConfig(config);
//
//            session.connect();
//
//            channel = session.openChannel("exec");
//
//            ChannelExec exec = (ChannelExec) channel;
//            exec.setCommand(command);
//            exec.setInputStream(null);
//
//            is = channel.getInputStream(); // 출력 스트림
//            byte[] buffer = new byte[BUFFER_SIZE];
//
//            exec.connect();
//            while(true){
//                while (is.available() > 0) {
//                    int i = is.read(buffer, 0, BUFFER_SIZE);
//                    if (i < 0) break;
//                    sb.append(new String(buffer, 0, i));
//                }
//
//                if (channel.isClosed()) {
//                    if (is.available() > 0) continue;
//                    break;
//                }
//
//                TimeUnit.MILLISECONDS.sleep(100);
//            }
//        } catch (JSchException | IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (channel != null) {
//                channel.disconnect();
//            }
//            if (session != null) {
//                session.disconnect();
//            }
//            return sb.toString();
//        }
//    }

    // 서버의 간략한 정보를 불러온다. (Linux 전용)
    public ServerBasicPerformance getServerBasicPerformance(ServerConnectionEntity connection){
        ServerBasicPerformance sp = new ServerBasicPerformance();
        int errCnt = 0;

        // 서버 이름 설정
        sp.setServerName(connection.getTitle());

        // RAM 정보
        // 2번 : 전체, 3번 : 사용, 7번 : 사용 가능 (단위 : MB)
        String cpuRes = this.getLinuxShellWithCommand("free -m | awk \'NR == 2 {print $2,$3,$7}\'");
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
        String diskRes = this.getLinuxShellWithCommand("df -P | grep -v ^Filesystem | awk \'{sum_tot += $2; sum_used += $3; sum_rem += $4} END { print sum_tot/1024/1024, sum_used/1024/1024, sum_rem/1024/1024 }\'");
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
        String cpuUsage = this.getLinuxShellWithCommand("mpstat | tail -1 | awk \'{print 100-$NF}\'");
        if(cpuUsage != null){
            sp.setCpuUsage(Double.parseDouble(cpuUsage.trim()));
        } else {
            errCnt += 1;
        }

        // 사용자 접속 정보
        String connections = this.getLinuxShellWithCommand("who | awk \'END{print NR}\'");
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

    // Dashboard
    // 리눅스 서버에서 실행 중인 프로세스와 포트 번호 확인 기능을 통해 GIS 실행 여부를 확인한다.
    public Map<String, GeoManagerProcessInfo> loadGeoManagerProcessInfoMap(List<String> geoManagers){
        for(String manager : geoManagers){
            String processResult = this.getLinuxShellWithCommand(String.format("ps -efl | grep %s", manager));
            System.out.println(processResult); // Parsing Confirm
            System.out.println("-----------");
        }
        return new HashMap<>();
    }

    public Map<String, GeoManagerProcessInfo> loadGeoManagerProcessInfoMapByUIType(String uiType){
        switch(uiType){
            case "DASHBOARD" :
                return loadGeoManagerProcessInfoMap(
                    Arrays.asList("mapserver", "mapproxy", "tegola", "terrain-server", "mapnik")
                );

            default :
                return new HashMap<>();
        }
    }


    // 자신의 서버에 해당하는 성능 정보를 가져온다. (지금은 리눅스만 가능)
    public ServerBasicPerformance getOwnServerBasicPerformance(){
        String ipAddr = this.getOwnIpAddressInLinux();
        if(ipAddr != null){
            ServerConnectionEntity connection = serviceMapper.findServerConnectionsByIpAddress(ipAddr);
            if(connection != null) return this.getServerBasicPerformance(connection);
        }
        return new ServerBasicPerformance();
    }

    // 본인이 접속한 디바이스의 정보를 가져온다. (단, 리눅스를 사용하지 않아야 함. 즉, 수정 필요.)
    public ServerConnectionEntity getOwnServerConnection(){
        String ipAddr = this.getOwnIpAddressInLinux();
        if(ipAddr != null){
            return serviceMapper.findServerConnectionsByIpAddress(ipAddr);
        }
        return new ServerConnectionEntity();
    }

    // 다른 서버에 대한 기본 정보를 가져온다.
    public ServerConnectionEntity getAnotherConnection(String key){
        return serviceMapper.findServerConnectionByName(key);
    }

    // 자신의 서버와 연결이 되는 목록을 가져온다. (지금은 리눅스만 가능)
    public List<ServerConnectionEntity> getOwnRelateConnectionsList() {
        String ipAddr = this.getOwnIpAddressInLinux();
        if(ipAddr != null){
            return serviceMapper.findOwnRelateConnectionsByIpAddress(ipAddr);
        } else return new ArrayList<>();
    }

    // 현재 서버에 해당하는 타입의 모든 정보들을 가져온다. (지금은 리눅스만 가능)
    public List<ServerConnectionEntity> getOwnAllConnectionsListSameType(){
        String ipAddr = this.getOwnIpAddressInLinux();
        if(ipAddr != null){
            ServerConnectionEntity own = serviceMapper.findServerConnectionsByIpAddress(ipAddr);
            if(own != null) return serviceMapper.findServerConnectionByType(own.getType().name());
        }
        return new ArrayList<>();
    }

    // 자신의 서버와 연결이 되는 목록을 가져온다. (지금은 리눅스만 가능)
    public Map<String, String> getEachFirstConnectionsMap() {
        Map<String, String> mapInc = new LinkedHashMap<>();
        Map<String, String> mapAno = new HashMap<>();

        String ipAddr = this.getOwnIpAddressInLinux();

        // 제공하는 서버가 윈도우가 아닌 경우에만 실행한다. (윈도우일 때, 제공 방안은 추후 개발 필요.)
        if (!System.getProperty("os.name").toLowerCase().contains("win") && ipAddr != null) {
            List<ServerConnectionEntity> list = serviceMapper.findOwnRelateConnectionsByIpAddress(ipAddr);
            if(list.size() == 0) return mapInc;
            else {
                ServerConnectionEntity ownConn = serviceMapper.findServerConnectionsByIpAddress(ipAddr);
                String ownKey = ownConn.getKey();
                String ownName = ownKey.split("-")[1];

                for(ServerConnectionEntity anoConn : list){
                    if(mapInc.containsKey("B3") && mapInc.containsKey("U1") && mapInc.containsKey("GOC")) break;

                    String anoKey = anoConn.getKey();
                    String anoCenter = anoKey.split("-")[0];
                    String anoName = anoKey.split("-")[1];

                    if(ownName.equals(anoName)) {
                        mapInc.put(anoCenter.startsWith("N") ? anoCenter.substring(1) : anoCenter, anoKey);
                    } else {
                        mapAno.put(anoCenter.startsWith("N") ? anoCenter.substring(1) : anoCenter, anoKey);
                    }
                }
            }

            if(mapInc.containsKey("B1") && mapInc.containsKey("U3") && mapInc.containsKey("GOC")) return mapInc;

            for(String c : Arrays.asList("B1", "U3", "GOC")){
                if(!mapInc.containsKey(c)){
                    mapInc.put(c, mapAno.get(c));
                }
            }

            return mapInc;
        }

        return mapInc;
    }

    // 연결 관계를 가져오는 메소드 (지금은 리눅스만 가능)
    public Map<String, List<String>> getConnectRelations(){
        Map<String, List<String>> map = new LinkedHashMap<>();
        String ipAddr = this.getOwnIpAddressInLinux();

        // 제공하는 서버가 윈도우가 아닌 경우에만 실행한다. (윈도우일 때, 제공 방안은 추후 개발 필요.)
        if(!System.getProperty("os.name").toLowerCase().contains("win") && ipAddr != null){
            for(ServerConnectionEntity connection : serviceMapper.findOwnRelateConnectionsByIpAddress(ipAddr)){
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

        return map;
    }

    // 자신의 포트 정보를 가져오는 메소드 (지금은 리눅스만 가능)
    public ServicePortEntity getServicePortInfo(){
        String ipAddr = this.getOwnIpAddressInLinux();

        // 제공하는 서버가 윈도우가 아닌 경우에만 실행한다. (윈도우일 때, 제공 방안은 추후 개발 필요.)
        if(!System.getProperty("os.name").toLowerCase().contains("win") && ipAddr != null){
            ServerConnectionEntity entity = serviceMapper.findServerConnectionsByIpAddress(ipAddr);
            return entity != null ? serviceMapper.findPortConfigBySvrId(entity.getId()) : new ServicePortEntity();
        }

        return new ServicePortEntity();
    }

    // 연동 가능한 서버의 포트 정보를 가져오는 메소드
    public ServicePortModel getServicePortInfoWithId(long id){
        // 제공하는 서버가 윈도우가 아닌 경우에만 실행한다. (윈도우일 때, 제공 방안은 추후 개발 필요.)
        if(!System.getProperty("os.name").toLowerCase().contains("win")){
            ServicePortEntity entity = serviceMapper.findPortConfigBySvrId(id);
            return entity != null ? ServicePortModel.convertToModel(entity): new ServicePortModel();
        }

        return new ServicePortModel();
    }

    // 서버 및 포트 번호를 수정하기 위한 메소드
    @Transactional
    public boolean serverInfoSave(ServerFormModel serverFormModel){
        ServerConnectionEntity connectionEntity = serviceMapper.findServerConnectionByName(serverFormModel.getKey());
        ServicePortEntity portEntity = serviceMapper.findPortConfigBySvrId(serverFormModel.getId());

        ServerConnectionModel connectionModel = new ServerConnectionModel(serverFormModel.getId(), serverFormModel.getKey(), serverFormModel.getTitle(), serverFormModel.getServerType(), serverFormModel.getIpAddress(), serverFormModel.getAdminServerPort(), serverFormModel.getUsername(), serverFormModel.getPassword());
        ServicePortModel portModel = new ServicePortModel(0L, serverFormModel.getId(), serverFormModel.getPostgreSQLPort(), serverFormModel.getWatchdogPort(), serverFormModel.getWatchdogHbPort(), serverFormModel.getPcpProcessPort(), serverFormModel.getPgPool2Port(), serverFormModel.getAdminServerPort(), serverFormModel.getMapProxyPort(), serverFormModel.getMapServerPort(), serverFormModel.getVectorTilePort(), serverFormModel.getJiinHeightPort(), serverFormModel.getLosPort(), serverFormModel.getMinioPort(), serverFormModel.getMapnikPort(), serverFormModel.getSyncthingTcpPort(), serverFormModel.getSyncthingUdpPort());

        if(serverFormModel.getSaveType().equals("UPDATE")){
            if(portEntity != null && connectionEntity != null){
                serviceMapper.updateServerConnectionWithModel(connectionModel);
                serviceMapper.updateServicePortWithModel(portModel);
                return true;
            } else return false;
        } else if(serverFormModel.getSaveType().equals("INSERT")){
            if(portEntity == null && connectionEntity == null){
                serviceMapper.insertServerConnectionWithModel(connectionModel);
                ServerConnectionEntity entity = serviceMapper.findRecentlyInsertConnectionEntity();
                if(entity != null) portModel.setSvrId(entity.getId());
                serviceMapper.insertServicePortWithModel(portModel);
                return true;
            } else return false;
        } else return false;
    }

    // 서버와 포트 번호 설정을 삭제하기 위한 메소드
    @Transactional
    public boolean serverInfoDelete(long svrId){
        ServicePortEntity portEntity = serviceMapper.findPortConfigBySvrId(svrId);
        if(portEntity != null){
            serviceMapper.deleteServerRelationBySvrIdCascade(svrId);
            serviceMapper.deleteServicePortBySvrId(svrId);
            serviceMapper.deleteServerConnection(svrId);
            return true;
        } else return false;
    }

    // 서버 연동 설정을 위한 메소드
    @Transactional
    public boolean settingServerRelation(long mainSvrId, List<Long> subSvrIds){
        serviceMapper.deleteServerRelationByMainSvrId(mainSvrId);
        for(Long id : subSvrIds){
            serviceMapper.insertServerRelationWithModel(new ServerRelationModel(0L, mainSvrId, id));
        }
        return true;
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
        dataCounter.setRasterLayerCount(countMapper.countLayersByType("RASTER"));
        dataCounter.setVectorLayerCount(countMapper.countLayersByType("VECTOR"));
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
