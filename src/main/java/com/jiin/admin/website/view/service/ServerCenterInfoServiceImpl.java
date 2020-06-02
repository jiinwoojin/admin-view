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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ServerCenterInfoServiceImpl implements ServerCenterInfoService {
    @Value("${project.data-path}")
    private String dataPath;

    /**
     * 서버 설정 기반 YAML 파일을 불러온다.
     * @param
     */
    private File loadServerConfigYAMLFile() throws IOException {
        File file = new File(dataPath + Constants.SERVER_INFO_FILE_PATH + "/" + Constants.SERVER_INFO_FILE_NAME);
        if(file.exists()) return file;
        else {
            file.createNewFile();
            return file;
        }
    }

    /**
     * YAML 파일에 있는 모든 요소들을 MAP 데이터로 변환한다.
     * @param
     */
    private Map<String, Object> loadMapDataAtYAMLFile() {
        Map<String, Object> map = null;
        try {
            map = YAMLFileUtil.fetchMapByYAMLFile(this.loadServerConfigYAMLFile());
        } catch (IOException e) {
            log.error("YAML 파일을 찾을 수 없습니다.");
            return null;
        }
        return map;
    }

    /**
     * YAML 파일을 기반으로 서버 설정 목록을 생성한다.
     * @param
     */
    private List<ServerCenterInfo> loadDataListAtYAMLFile(){
        Map<String, Object> map = this.loadMapDataAtYAMLFile();
        if(map == null) return new ArrayList<>();
        if(map.containsKey("remote")) {
            List<ServerCenterInfo> list = new ArrayList<>();
            Map<String, Object> remoteMap = (Map<String, Object>) map.get("remote");
            int cnt = (Integer) remoteMap.get("count");
            for(int i = 1; i <= cnt; i++){
                list.add(ServerCenterInfo.convertDTO("server-" + i, (Map<String, Object>) remoteMap.get(String.format("server-%d", i))));
            }
            return list;
        } else return new ArrayList<>();
    }

    /**
     * YAML 파일에 서버 정보 데이터들을 저장해서 YAML 파일을 재작성한다.
     * @param servers List of ServerCenterInfo
     */
    private boolean saveServerInfosAtYAMLFile(List<ServerCenterInfo> servers){
        Map<String, Object> map = this.loadMapDataAtYAMLFile();
        if(map == null) return false;
        if(map.containsKey("remote")) {
            // 0단계. 백업
            DateFormat format = new SimpleDateFormat("yyMMdd_hhmmss");
            Date date = new Date();
            String nowTime = format.format(date);

            String backupPath = dataPath + Constants.SERVER_INFO_FILE_PATH + "/" + String.format("server_info_%s.yaml", nowTime);
            String mainPath = dataPath + Constants.SERVER_INFO_FILE_PATH + "/" + Constants.SERVER_INFO_FILE_NAME;

            try {
                String beforeContext = FileSystemUtil.fetchFileContext(mainPath);
                FileSystemUtil.createAtFile(backupPath, beforeContext);
            } catch (IOException e) {
                log.error("YAML 파일 백업 진행 실패.");
                return false;
            }

            // 1단계. remote 내용 변경
            Map<String, Object> remoteMap = new LinkedHashMap<>();
            int cnt = servers.size();
            remoteMap.put("count", cnt);
            for(int i = 0; i < cnt; i++){
                remoteMap.put(String.format("server-%d", i + 1), ServerCenterInfo.convertMap(servers.get(i)));
            }
            map.put("remote", remoteMap);

            Map<String, Object> convertMap = new LinkedHashMap<>();
            convertMap.put("config", map.get("config"));
            convertMap.put("local", map.get("local"));
            convertMap.put("remote", map.get("remote"));

            // 2단계. 새로 저장
            String context = YAMLFileUtil.fetchYAMLStringByMap(convertMap, "BLOCK");

            context = context.replace("\nlocal:", "\n\nlocal:");
            context = context.replace("\nremote:", "\n\nremote:");

            try {
                FileSystemUtil.createAtFile(mainPath, context);
            } catch (IOException e) {
                log.error("YAML 파일 생성 진행 실패.");
                return false;
            }

            return true;
        }

        return false;
    }

    /**
     * YAML 파일을 기반으로 서버 센터 목록을 불러온다.
     * @param
     */
    @Override
    public String[] loadZoneList() {
        Map<String, Object> map = this.loadMapDataAtYAMLFile();
        if(map == null) return new String[0];
        else {
            Map<String, Object> configMap = (Map<String, Object>) map.get("config");
            String zones = (String) configMap.get("zone");
            return zones.split(",");
        }
    }

    /**
     * YAML 파일을 기반으로 서버 종류 목록을 불러온다.
     * @param
     */
    @Override
    public String[] loadKindList() {
        Map<String, Object> map = this.loadMapDataAtYAMLFile();
        if(map == null) return new String[0];
        else {
            Map<String, Object> configMap = (Map<String, Object>) map.get("config");
            String kinds = (String) configMap.get("kind");
            return kinds.split(",");
        }
    }

    /**
     * YAML 파일을 기반으로 서버 설정 목록을 불러온다.
     * @param
     */
    @Override
    public List<ServerCenterInfo> loadDataList() {
        return this.loadDataListAtYAMLFile();
    }

    /**
     * YAML 파일을 기반으로 대시보드를 위한 서버 목록을 형성한다.
     * @param
     */
    @Override
    public Map<String, Object> loadDataMapZoneBase() {
        List<ServerCenterInfo> servers = this.loadDataListAtYAMLFile();
        Map<String, Object> map = new LinkedHashMap<>();
        for(ServerCenterInfo server : servers){
            List<ServerCenterInfo> tmpList = (List<ServerCenterInfo>) map.getOrDefault(server.getZone(), new ArrayList<ServerCenterInfo>());
            tmpList.add(server);
            map.put(server.getZone(), tmpList);
        }
        return map;
    }

    /**
     * Local 서버 정보를 가져온다.
     * @param
     */
    @Override
    public ServerCenterInfo loadLocalInfoData() {
        Map<String, Object> map = this.loadMapDataAtYAMLFile();
        return (map != null) ? ServerCenterInfo.convertDTO("local", (Map<String, Object>) map.get("local")) : null;
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
     * YAML 파일을 기반으로 서버 설정을 추가(INSERT) 및 수정(UPDATE) 한다.
     * @param model ServerCenterInfoModel
     */
    @Override
    public boolean saveData(ServerCenterInfoModel model) {
        List<ServerCenterInfo> infos = this.loadDataListAtYAMLFile();
        switch(model.getMethod()){
            case "INSERT" :
                infos.add(ServerCenterInfoModel.convertDTO(model));
                return this.saveServerInfosAtYAMLFile(infos);
            case "UPDATE" :
                infos = infos.stream().filter(o -> o != null).map(o -> o.getName().equals(model.getName()) ? ServerCenterInfoModel.convertDTO(model) : o).collect(Collectors.toList());
                return this.saveServerInfosAtYAMLFile(infos);
            default :
                return false;
        }
    }

    /**
     * YAML 파일을 기반으료 서버 설정을 삭제한다.
     * @param key String
     */
    @Override
    public boolean removeDataByKey(String key) {
        if(!this.loadDataHasInFile(key)) return false;
        List<ServerCenterInfo> infos = this.loadDataListAtYAMLFile();
        infos = infos.stream().filter(o -> !o.getKey().equals(key)).collect(Collectors.toList());
        return this.saveServerInfosAtYAMLFile(infos);
    }


    /**
     * YAML 파일을 기반으로 포트 번호 목록을 불러온다. [구현 예정 : Port 값만 보여주는 기능.]
     */
}
