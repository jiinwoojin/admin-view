package com.jiin.admin.website.view.service;

import com.jiin.admin.Constants;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.ServerCenterInfoModel;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.YAMLFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServerCenterInfoServiceImpl implements ServerCenterInfoService {
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${project.data-path}")
    private String dataPath;

    /**
     * 서버 설정 기반 YAML 파일을 불러온다.1
     */
    private File loadServerConfigYAMLFile() throws IOException {
        File file = Paths.get(dataPath, Constants.SERVER_INFO_FILE_PATH, Constants.SERVER_INFO_FILE_NAME).toFile();

        if (!file.exists()) {
            file.createNewFile();
        }

        return file;
    }

    /**
     * YAML 파일에 있는 모든 요소들을 MAP 데이터로 변환한다.
     */
    private Map<String, Object> loadMapDataAtYAMLFile() {
        Map<String, Object> map;
        try {
            map = YAMLFileUtil.fetchMapByYAMLFile(this.loadServerConfigYAMLFile());
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
            return null;
        }

        return map;
    }

    /**
     * YAML 파일을 기반으로 서버 설정 목록을 생성한다.
     */
    private List<ServerCenterInfo> loadDataListAtYAMLFile() {
        Map<String, Object> map = this.loadMapDataAtYAMLFile();
        if (map == null) return new ArrayList<>();
        if (map.containsKey("remote")) {
            List<ServerCenterInfo> list = new ArrayList<>();
            Map<String, Object> remoteMap = (Map<String, Object>) map.get("remote");
            for (String key : remoteMap.keySet()) {
                if (!key.equals("count"))
                    list.add(ServerCenterInfo.convertDTO(key, (Map<String, Object>) remoteMap.get(key)));
            }
            return list;
        } else return new ArrayList<>();
    }

    /**
     * YAML 파일에 서버 정보 데이터들을 저장해서 YAML 파일을 재작성한다.
     * @param remotes List of ServerCenterInfo
     */
    private boolean saveServerInfosAtYAMLFile(ServerCenterInfo local, List<ServerCenterInfo> remotes) {
        Map<String, Object> map = this.loadMapDataAtYAMLFile();
        if (map == null) return false;

        String mainPath = dataPath + Constants.SERVER_INFO_FILE_PATH + "/" + Constants.SERVER_INFO_FILE_NAME;

        // 1단계. remote 내용 변경
        Map<String, Object> remoteMap = new LinkedHashMap<>();
        int cnt = remotes.size();
        remoteMap.put("count", cnt);
        for (int i = 0; i < cnt; i++) {
            remoteMap.put(remotes.get(i).getKey(), ServerCenterInfo.convertMap(remotes.get(i)));
        }
        map.put("remote", remoteMap);

        Map<String, Object> convertMap = new LinkedHashMap<>();
        convertMap.put("config", map.get("config"));
        convertMap.put("local", ServerCenterInfo.convertMap(local));
        convertMap.put("remote", map.get("remote"));

        // 2단계. 새로 저장
        String context = YAMLFileUtil.fetchYAMLStringByMap(convertMap, "BLOCK");

        context = context.replace("\nlocal:", "\n\nlocal:");
        context = context.replace("\nremote:", "\n\nremote:");

        FileSystemUtil.createAtFile(mainPath, context);

        return true;
    }

    /**
     * YAML 파일을 기반으로 서버 센터 목록을 불러온다.
     */
    @Override
    public String[] loadZoneList() {
        Map<String, Object> map = this.loadMapDataAtYAMLFile();
        ServerCenterInfo local = this.loadLocalInfoData();
        if (map == null) return new String[0];
        else {
            Map<String, Object> configMap = (Map<String, Object>) map.get("config");
            String zones = (String) configMap.get("zone");

            String[] zoneArray = zones.split(",");
            List<String> list = Arrays.stream(zoneArray).collect(Collectors.toList());
            int idx = list.indexOf(local.getZone());
            if(idx >= 0 && idx < list.size()){
                list.remove(idx);
                list.add(0, local.getZone());
            }

            return zoneArray;
        }
    }

    /**
     * YAML 파일을 기반으로 서버 종류 목록을 불러온다.
     */
    @Override
    public String[] loadKindList() {
        Map<String, Object> map = this.loadMapDataAtYAMLFile();
        if (map == null) return new String[0];
        else {
            Map<String, Object> configMap = (Map<String, Object>) map.get("config");
            String kinds = (String) configMap.get("kind");
            return kinds.split(",");
        }
    }

    /**
     * YAML 파일을 기반으로 서버 설정 목록을 불러온다.
     */
    @Override
    public List<ServerCenterInfo> loadRemoteList() {
        return this.loadDataListAtYAMLFile();
    }

    /**
     * 같은 센터 (kind, zone) 안에 있는 서버 정보 목록들을 불러온다.
     */
    @Override
    public List<ServerCenterInfo> loadNeighborList() {
        ServerCenterInfo local = this.loadLocalInfoData();
        return this.loadDataListAtYAMLFile().stream()
                .filter(o -> o.getZone().equals(local.getZone()) && o.getKind().equals(local.getKind()))
                .collect(Collectors.toList());
    }

    /**
     * YAML 파일을 기반으로 대시보드를 위한 서버 목록을 형성한다.
     */
    @Override
    public Map<String, Object> loadDataMapZoneBase() {
        List<ServerCenterInfo> servers = this.loadDataListAtYAMLFile();

        ServerCenterInfo local = this.loadLocalInfoData();
        if (local != null) servers.add(0, local);

        Map<String, Object> map = new LinkedHashMap<>();
        for (ServerCenterInfo server : servers) {
            List<ServerCenterInfo> tmpList = (List<ServerCenterInfo>) map.getOrDefault(server.getZone(), new ArrayList<ServerCenterInfo>());
            tmpList.add(server);
            map.put(server.getZone(), tmpList);
        }
        return map;
    }

    /**
     * Local 서버 정보를 가져온다.
     */
    @Override
    public ServerCenterInfo loadLocalInfoData() {
        Map<String, Object> map = this.loadMapDataAtYAMLFile();
        Map<String, Object> local = (Map<String, Object>) map.get("local");

        return (map != null) ? ServerCenterInfo.convertDTO(String.format("%s-%s-%s", local.get("zone"), local.get("kind"), local.get("name")), local) : null;
    }

    /**
     * YAML 파일을 기반으로 중복 여부를 확인한다.
     * @param key String
     */
    @Override
    public boolean loadDataHasInFile(String key) {
        return this.loadDataListAtYAMLFile().stream()
                .filter(o -> o.getKey() != null)
                .filter(o -> o.getKey().equals(key))
                .count() > 0L;
    }

    /**
     * YAML 파일을 기반으로 로컬 서버 설정을 저장한다.
     * @param model ServerCenterInfoModel
     */
    @Override
    public boolean saveLocalData(ServerCenterInfoModel model) {
        List<ServerCenterInfo> infos = this.loadDataListAtYAMLFile();
        model.setKey(String.format("%s-%s-%s", model.getZone(), model.getKind(), model.getName()));
        return this.saveServerInfosAtYAMLFile(ServerCenterInfoModel.convertDTO(model), infos);
    }

    /**
     * YAML 파일을 기반으로 연동 서버 설정을 추가(INSERT) 및 수정(UPDATE) 한다.
     * @param model ServerCenterInfoModel
     */
    @Override
    public boolean saveRemoteData(ServerCenterInfoModel model) {
        ServerCenterInfo local = this.loadLocalInfoData();
        if (model.getKey().equals(local.getKey())) return this.saveLocalData(model);
        List<ServerCenterInfo> infos = this.loadDataListAtYAMLFile();
        switch(model.getMethod()) {
            case "INSERT" :
                model.setKey(String.format("%s-%s-%s", model.getZone(), model.getKind(), model.getName()));
                infos.add(ServerCenterInfoModel.convertDTO(model));
                return this.saveServerInfosAtYAMLFile(this.loadLocalInfoData(), infos);
            case "UPDATE" :
                infos = infos.stream().filter(Objects::nonNull)
                        .map(o -> {
                            if (o.getKey().equals(model.getKey())) {
                                ServerCenterInfo newInfo = new ServerCenterInfo(model.getKey(), model.getName(), model.getIp(), model.getZone(), model.getKind(), model.getDescription());
                                newInfo.setKey(String.format("%s-%s-%s", model.getZone(), model.getKind(), model.getName()));
                                return newInfo;
                            } else {
                                return o;
                            }
                        })
                        .collect(Collectors.toList());
                return this.saveServerInfosAtYAMLFile(this.loadLocalInfoData(), infos);
            default :
                return false;
        }
    }

    /**
     * YAML 파일을 기반으로 서버 설정을 삭제한다.
     * @param key String
     */
    @Override
    public boolean removeDataByKey(String key) {
        if (!this.loadDataHasInFile(key)) return false;
        List<ServerCenterInfo> infos = this.loadDataListAtYAMLFile();
        infos = infos.stream().filter(o -> !o.getKey().equals(key))
                .collect(Collectors.toList());
        return this.saveServerInfosAtYAMLFile(this.loadLocalInfoData(), infos);
    }
}
