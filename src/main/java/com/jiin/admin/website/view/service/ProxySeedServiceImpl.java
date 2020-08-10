package com.jiin.admin.website.view.service;

import com.amihaiemil.docker.Container;
import com.amihaiemil.docker.Logs;
import com.jiin.admin.Constants;
import com.jiin.admin.dto.ProxyCacheDTO;
import com.jiin.admin.mapper.data.ProxyCacheMapper;
import com.jiin.admin.vo.SeedContainerInfo;
import com.jiin.admin.website.util.DockerUtil;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.LinuxCommandUtil;
import com.jiin.admin.website.util.YAMLFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProxySeedServiceImpl implements ProxySeedService {
    @Value("${project.docker-name.seed-name-prefix}")
    private String DOCKER_SEED_NAME_PREFIX;

    @Value("${project.docker-name.default-seed-name}")
    private String DOCKER_DEFAULT_SEED_NAME;

    @Value("${project.data-path}")
    private String dataPath;

    @Value("${project.docker-name.mapproxy-name}")
    private String mapProxyContainer;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Resource
    private ProxyCacheMapper proxyCacheMapper;

    // Proxy Cache 데이터 중 YAML 파일에 설정된 데이터만 호출한다.
    public List<ProxyCacheDTO> loadProxyCacheListBySelected(){
        return proxyCacheMapper.findBySelected(true);
    }

    // 설정된 YAML 파일을 통해 Seeding 파일 생성
    private void createSeedingFile(Map<String, Object> param) {
        String dataPath = (String) param.get("DATA_PATH");
        String seedName = (String) param.get("SEED_NAME");

        Map<String, Object> seed = new LinkedHashMap<>();
        Map<String, Object> seeds = new LinkedHashMap<>();
        Map<String, Object> basic_seed = new LinkedHashMap<>();

        String[] caches = new String[]{(String) param.get("cache")};
        String[] coveragesArr = new String[]{(String) param.get("coverage")};

        Map<String, Object> levels = new LinkedHashMap<>();
        levels.put("from", Integer.parseInt((String) param.get("levelFrom")));
        levels.put("to", Integer.parseInt((String) param.get("levelTo")));

        Map<String, Object> refresh_before = new LinkedHashMap<>();
        String refreshBeforeType = (String) param.get("refreshBeforeType");
        refresh_before.put(refreshBeforeType, Integer.parseInt((String) param.get("refreshBefore")));

        basic_seed.put("caches", Arrays.toString(caches).replace("\"", ""));
        basic_seed.put("coverages", coveragesArr);
        basic_seed.put("levels", levels);
        basic_seed.put("refresh_before", refresh_before);

        seeds.put("basic_seed", basic_seed);    // 이부분 수정
        seed.put("seeds", seeds);                // 이부분 수정

        Map<String, Object> basic_cleanup = new LinkedHashMap<>();

        String removeBeforeType = (String) param.get("removeBeforeType");
        Map<String, Object> remove_before = new LinkedHashMap<>();
        remove_before.put(removeBeforeType, Integer.parseInt((String) param.get("removeBefore")));
        basic_cleanup.put("basic_cleanup", new LinkedHashMap<String, Object>(){
            {
                put("caches", Arrays.toString(caches).replace("\"", ""));
                put("remove_before", remove_before);
            }
        });
        seed.put("cleanups", basic_cleanup);

        Map<String, Object> coverages = new LinkedHashMap<>();
        Integer[] bbox = new Integer[]{
                Integer.parseInt((String) param.get("xmin")),
                Integer.parseInt((String) param.get("ymin")),
                Integer.parseInt((String) param.get("xmax")),
                Integer.parseInt((String) param.get("ymax"))
        };

        Map<String, Object> coverage = new LinkedHashMap<>();
        coverage.put("bbox", bbox);
        coverage.put("srs", param.get("projection"));

        coverages.put((String) param.get("coverage"), coverage);
        seed.put("coverages", coverages);

        //{seeds={basic_seed={caches=[basic], coverages=[korea], levels={from=11, to=14}, refresh_before={hours=1}}}, coverages={korea={bbox=[100, 0, 160, 70], srs=EPSG:4326}}}
        String context = YAMLFileUtil.fetchYAMLStringByMap(seed, "AUTO");
        context = context.replace("cleanups:", "\ncleanups:");
        context = context.replaceFirst("(?s)(.*)" + "coverages:", "$1" + "\ncoverages:");
        context = context.replace("\'", "");

        String seedpath = dataPath + Constants.PROXY_SETTING_FILE_PATH  + "/seed-" + seedName + ".yaml";
        FileSystemUtil.createAtFile(seedpath, context);
    }

    // Docker RUN 관련 명령어 : 라이브러리에서 자체적으로 제공하지 않아 CMD 로 1차 해결.
    private String execCreateDockerSeedContainer(String seedName, String seedPath){
        String mapproxypath = dataPath + Constants.PROXY_SETTING_FILE_PATH + "/" + Constants.PROXY_SETTING_FILE_NAME;

        // docker run -it -d --rm --user 1001:1000 -v /data/jiapp:/data/jiapp -v /etc/localtime:/etc/localtime:ro --name jimap_seed jiinwoojin/jimap_mapproxy mapproxy-seed -f /data/jiapp/data_dir/proxy/mapproxy.yaml -s /data/jiapp/data_dir/proxy/seed.yaml -c 4 --seed ALL
        String command = "docker run -i -d --rm --user [UID]:[GID] -v /data/jiapp:/data/jiapp -v /etc/localtime:/etc/localtime:ro --name [SEED_NAME] jiinwoojin/jimap_mapproxy mapproxy-seed -f [MAPPROXY.YAML] -s [SEED.YAML] -c 4 --seed ALL";
        command = command.replace("[UID]", LinuxCommandUtil.fetchUID());
        command = command.replace("[GID]", LinuxCommandUtil.fetchGID());
        command = command.replace("[SEED_NAME]", seedName);
        command = command.replace("[MAPPROXY.YAML]", mapproxypath);
        command = command.replace("[SEED.YAML]", seedPath);

        log.info(command);

        List<String> result = LinuxCommandUtil.fetchResultByLinuxCommonToList(command);
        if (result.size() == 0) {
            return "";
        } else {
            return result.get(0);
        }
    }

    // SEED 와 연관된 Container 의 정보를 호출한다.
    @Override
    public List<SeedContainerInfo> loadSeedContainerInfoList() {
        List<Container> containers = DockerUtil.fetchAllContainers();
        List<SeedContainerInfo> list = new ArrayList<>();

        String utcPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(utcPattern);

        for (Container container : containers) {
            try {
                JsonObject object = container.inspect();
                String name = object.getString("Name").replace("/", "");
                if (name.startsWith(DOCKER_SEED_NAME_PREFIX)) {
                    JsonObject hostConfig = object.getJsonObject("HostConfig");
                    JsonObject portBindings = hostConfig.getJsonObject("PortBindings");
                    list.add(new SeedContainerInfo(
                        name,
                        object.getString("Id"),
                        container.getString("Image"),
                        portBindings.keySet().stream().collect(Collectors.joining()),
                        object.getJsonObject("State").getString("Status"),
                        sdf.parse(object.getString("Created")),
                        name.equals(DOCKER_DEFAULT_SEED_NAME)
                    ));
                }
            } catch (IOException | ParseException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }

        return list;
    }

    // SEED 이름으로 Container 를 삭제한다.
    @Override
    public boolean removeSeedContainerByName(String name) {
        // Container 를 우선 멈추고, Remove 를 들어간다.
        try {
            DockerUtil.executeContainerByNameAndMethod(name, "STOP");
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
            return false;
        }

        String seedpath = dataPath + Constants.PROXY_SETTING_FILE_PATH + "/seed-" + name + ".yaml";
        try {
            FileSystemUtil.deleteFile(seedpath);
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public SeedContainerInfo createSeedContainer(Map<String, Object> param) {
        long now = new Date().getTime();

        String seedName = DOCKER_SEED_NAME_PREFIX + "-" + now;
        param.put("DATA_PATH", dataPath);
        param.put("SEED_NAME", seedName);

        createSeedingFile(param);
        String seedPath = dataPath + Constants.PROXY_SETTING_FILE_PATH  + "/seed-" + seedName + ".yaml";
        String result = execCreateDockerSeedContainer(seedName, seedPath);
        if (!StringUtils.isBlank(result)) {
            // 내일 오전에 새로운 SeedContainerInfo 뿌려줄 것.
            return loadSeedContainerInfoList().stream().filter(o -> o.getName().equals(seedName)).findFirst().orElse(new SeedContainerInfo());
        } else {
            return new SeedContainerInfo();
        }
    }

    @Override
    public String resetDefaultSeeding() {
        String seedPath = dataPath + Constants.PROXY_SETTING_FILE_PATH  + "/" + Constants.PROXY_SEEDING_FILE_NAME;
        String result = execCreateDockerSeedContainer(DOCKER_DEFAULT_SEED_NAME, seedPath);

        return result;
    }

    @Override
    public Map<String, Object> loadLogTextInContainerByName(String name) {
        List<Container> containers = DockerUtil.fetchAllContainers();

        String utcPattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'";
        SimpleDateFormat sdf = new SimpleDateFormat(utcPattern);

        for (Container container : containers) {
            try {
                JsonObject object = container.inspect();
                String cntName = object.getString("Name").replace("/", "");
                if (cntName.equals(name)) {
                    Map<String, Object> result = new HashMap<>();

                    String lastLog = "";
                    String[] fields = new String[]{ "TIME", "LEVEL", "PER", "XMIN", "YMIN", "XMAX", "YMAX", "TILES_CNT" };
                    if (activeProfile.equals("outside") || activeProfile.equals("local")) {
                        lastLog = "[14:03:36] 13  75.44% 140.97656, 43.24219, 141.32812, 43.59375 (8593072 tiles)";
                    }
                    Logs logs = container.logs();
                    String logText = logs.fetch();
                    String[] logSplit = logText.split("\n");
                    lastLog = logSplit[logSplit.length - 1];

                    String[] texts = lastLog.split(" ");
                    int idx = 0;
                    Map<String, Object> lastlogMap = new HashMap<>();
                    for(String log : texts){
                        if(!StringUtils.isEmpty(log)){
                            lastlogMap.put(fields[idx++], log);
                        }
                        if(idx == fields.length) {
                            break;
                        }
                    }
                    result.put("LOGS", lastlogMap);

                    File mapproxy = new File(dataPath + Constants.PROXY_SETTING_FILE_PATH + "/" + Constants.PROXY_SETTING_FILE_NAME);
                    if(mapproxy.exists()){
                        Map<String, Object> mapproxyInfo = YAMLFileUtil.fetchMapByYAMLFile(mapproxy);
                        // 작업 디렉토리 용량 확인
                        LinkedHashMap sources = (LinkedHashMap) mapproxyInfo.get("sources");
                        if(sources != null){
                            List<Path> workPaths = new ArrayList();
                            Set keys = sources.keySet();
                            for(Object key:keys){
                                Map<String, Object> source = (Map<String, Object>) sources.get(key);
                                Map<String, Object> req = (Map<String, Object>) source.get("req");
                                String mapPath = (String) req.get("map");
                                if(Files.exists(Paths.get(mapPath)) && !workPaths.contains(Paths.get(mapPath).getParent())){
                                    workPaths.add(Paths.get(mapPath).getParent());
                                }
                            }
                            long diskSize = 0;
                            for(Path workPath:workPaths){
                                diskSize += FileUtils.sizeOfDirectory(workPath.toFile());
                            }

                            result.put("DIR_SIZE", diskSize);
                        }
                    }

                    Date createDate = sdf.parse(object.getString("Created"));
                    result.put("RunningFor", Math.abs(new Date().getTime() - createDate.getTime()));

                    return result;
                }
            } catch (IOException | ParseException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }

        return null;
    }
}
