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
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
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

    private static final String DEFAULT_PROXY_CACHE_DATA = "world_cache";

    private static final String DEFAULT_CACHE_COVERAGE = "korea";

    @Resource
    private ProxyCacheMapper proxyCacheMapper;

    // Proxy Cache 데이터 중 YAML 파일에 설정된 데이터만 호출한다.
    public List<ProxyCacheDTO> loadProxyCacheListBySelected(){
        return proxyCacheMapper.findBySelected(true);
    }

    // YAML 파일의 MAP 데이터를 param 으로 가져온다.
    private Map<String, Object> convertYamlMapToParamMap(String seedName, Map<String, Object> yamlMap) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("DATA_PATH", dataPath);
        paramMap.put("SEED_NAME", seedName);

        Map<String, Object> seeds = (Map<String, Object>) yamlMap.get("seeds");
        Map<String, Object> basicSeed = (Map<String, Object>) seeds.get("basic_seed");

        List<String> caches = (List<String>) basicSeed.get("caches");
        List<String> coverages = (List<String>) basicSeed.get("coverages");

        String cache = DEFAULT_PROXY_CACHE_DATA;
        String coverage = DEFAULT_CACHE_COVERAGE;

        if (!caches.isEmpty()) {
            cache = caches.get(0);
        }

        if (!coverages.isEmpty()) {
            coverage = coverages.get(0);
        }

        paramMap.put("cache", cache);
        paramMap.put("coverage", coverage);

        Map<String, Object> levels = (Map<String, Object>) basicSeed.get("levels");

        paramMap.put("levelFrom", String.valueOf(levels.get("from")));
        paramMap.put("levelTo", String.valueOf(levels.get("to")));

        Map<String, Object> refreshBefore = (Map<String, Object>) basicSeed.get("refresh_before");

        for (String key : refreshBefore.keySet()) {
            paramMap.put("refreshBeforeType", key);
            paramMap.put("refreshBefore", String.valueOf(refreshBefore.get(key)));
        }

        Map<String, Object> seedCoverages = (Map<String, Object>) yamlMap.get("coverages");
        for (String key : seedCoverages.keySet()) {
            paramMap.put("coverage", coverage);
            Map<String, Object> coverageMap = (Map<String, Object>) seedCoverages.get(key);

            List<Integer> bbox = (List<Integer>) coverageMap.get("bbox");
            String srs = (String) coverageMap.get("srs");

            paramMap.put("xmin", String.valueOf(bbox.get(0)));
            paramMap.put("ymin", String.valueOf(bbox.get(1)));
            paramMap.put("xmax", String.valueOf(bbox.get(2)));
            paramMap.put("ymax", String.valueOf(bbox.get(3)));

            paramMap.put("projection", srs);
        }

        return paramMap;
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
        basic_seed.put("coverages", Arrays.toString(coveragesArr).replace("\"", ""));
        basic_seed.put("levels", levels);
        basic_seed.put("refresh_before", refresh_before);

        seeds.put("basic_seed", basic_seed);
        seed.put("seeds", seeds);

        Map<String, Object> basic_cleanup = new LinkedHashMap<>();

        if (param.keySet().containsAll(Arrays.asList("removeBeforeType", "removeBefore"))) {
            String removeBeforeType = (String) param.get("removeBeforeType");
            Map<String, Object> remove_before = new LinkedHashMap<>();
            remove_before.put(removeBeforeType, Integer.parseInt((String) param.get("removeBefore")));
            basic_cleanup.put("basic_cleanup", new LinkedHashMap<String, Object>() {
                {
                    put("caches", Arrays.toString(caches).replace("\"", ""));
                    put("remove_before", remove_before);
                }
            });
            seed.put("cleanups", basic_cleanup);
        }

        Map<String, Object> coverages = new LinkedHashMap<>();
        Integer[] bbox = new Integer[]{
                Integer.parseInt((String) param.get("xmin")),
                Integer.parseInt((String) param.get("ymin")),
                Integer.parseInt((String) param.get("xmax")),
                Integer.parseInt((String) param.get("ymax"))
        };

        Map<String, Object> coverage = new LinkedHashMap<>();
        coverage.put("bbox", Arrays.toString(bbox).replace("\"", ""));
        coverage.put("srs", param.get("projection"));

        coverages.put((String) param.get("coverage"), coverage);
        seed.put("coverages", coverages);

        //{seeds={basic_seed={caches=[basic], coverages=[korea], levels={from=11, to=14}, refresh_before={hours=1}}}, coverages={korea={bbox=[100, 0, 160, 70], srs=EPSG:4326}}}
        String context = YAMLFileUtil.fetchYAMLStringByMap(seed, "BLOCK");
        context = context.replace("cleanups:", "\ncleanups:");
        context = context.replaceFirst("(?s)(.*)" + "coverages:", "$1" + "\ncoverages:");
        context = context.replace("\'", "");

        String seedpath = dataPath + Constants.PROXY_SETTING_FILE_PATH  + "/seed-" + seedName + ".yaml";
        FileSystemUtil.createAtFile(seedpath, context);
    }

    // Docker RUN 관련 명령어 : 라이브러리에서 자체적으로 제공하지 않아 CMD 로 해결.
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

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'")
                                                    .withZone(DateTimeZone.forID("Asia/Seoul"));

        for (Container container : containers) {
            try {
                JsonObject object = container.inspect();
                String name = object.getString("Name").replace("/", "");
                if (name.startsWith(DOCKER_SEED_NAME_PREFIX)) {
                    JsonObject hostConfig = object.getJsonObject("HostConfig");
                    JsonObject portBindings = hostConfig.getJsonObject("PortBindings");
                    DateTime time = DateTime.parse(object.getString("Created"), fmt);
                    list.add(new SeedContainerInfo(
                        name,
                        object.getString("Id"),
                        container.getString("Image"),
                        portBindings.keySet().stream().collect(Collectors.joining()),
                        object.getJsonObject("State").getString("Status"),
                        new Date(time.toDate().getTime() + 9 * 60 * 60 * 1000L),
                        name.equals(DOCKER_DEFAULT_SEED_NAME)
                    ));
                }
            } catch (IOException e) {
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

        String seedPath = dataPath + Constants.PROXY_SETTING_FILE_PATH + "/seed-" + name + ".yaml";
        try {
            FileSystemUtil.deleteFile(seedPath);
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
            return false;
        }
        return true;
    }

    // 지금 당장 필요 없는 YAML 파일을 제거한다. (실행을 안 하고 있는 Container 를 없앤다.)
    @Override
    public boolean removeSeedYAMLFileNotNeed() {
        File confDir = new File(String.format("%s%s", dataPath, Constants.PROXY_SETTING_FILE_PATH));
        Set<String> seedFiles = new HashSet<>();
        for (File file : confDir.listFiles()) {
            String filename = file.getName();
            if (filename.equalsIgnoreCase(Constants.PROXY_SETTING_FILE_NAME) || filename.equalsIgnoreCase(Constants.PROXY_SEEDING_FILE_NAME)) {
                continue;
            }
            // seed-jimap_seed_~~~.yaml
            seedFiles.add(filename);
        }

        // 아래는 모두 실행 중인 Container
        List<Container> containers = DockerUtil.fetchAllContainers();
        for (Container container : containers) {
            JsonObject object = null;
            try {
                object = container.inspect();
                String cntName = object.getString("Name").replace("/", "");

                // 지금 실행 중인 Container 의 YAML 설정 파일은 삭제를 막는다.
                String tmpFileName = String.format("seed-%s.yaml", cntName);
                if (seedFiles.contains(tmpFileName)) {
                    seedFiles.remove(tmpFileName);
                }
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }

        // 삭제 작업 진행
        return false;
    }

    // SEED 컨테이너를 생성한다.
    @Override
    public SeedContainerInfo createSeedContainer(Map<String, Object> param) {
        long now = new Date().getTime();
        String seedName = (String) param.getOrDefault("SEED_NAME", DOCKER_SEED_NAME_PREFIX + "-" + now);

        param.put("DATA_PATH", dataPath);
        param.put("SEED_NAME", seedName);

        createSeedingFile(param);
        String seedPath = dataPath + Constants.PROXY_SETTING_FILE_PATH  + "/seed-" + seedName + ".yaml";
        String result = execCreateDockerSeedContainer(seedName, seedPath); // docker container 생성 시, container id 가 떡하니 나온다.
        if (!StringUtils.isBlank(result)) {
            return loadSeedContainerInfoList().stream().filter(o -> o.getName().equals(seedName)).findFirst().orElse(new SeedContainerInfo());
        } else {
            return new SeedContainerInfo();
        }
    }

    // Default SEED 를 재시작한다. (필요 없을 가능성이 있다.)
    @Override
    public String resetDefaultSeeding() {
        String seedPath = dataPath + Constants.PROXY_SETTING_FILE_PATH  + "/" + Constants.PROXY_SEEDING_FILE_NAME;
        String result = execCreateDockerSeedContainer(DOCKER_DEFAULT_SEED_NAME, seedPath);

        return result;
    }

    // Container 의 최신 로그를 불러온다.
    @Override
    public Map<String, Object> loadLogTextInContainerByName(String name) {
        List<Container> containers = DockerUtil.fetchAllContainers();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'")
                                                    .withZone(DateTimeZone.forID("Asia/Seoul"));

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

                    // 용량을 가져오는 로직 : MAP 으로 옮기겠음.

                    DateTime time = DateTime.parse(object.getString("Created"), fmt);
                    Date createDate = time.toDate();
                    long runningTime = Math.abs(new Date().getTime() - createDate.getTime() - 9 * 60 * 60 * 1000L);
                    runningTime /= 1000L;

                    long second = runningTime % 60L;
                    long remainMinute = (runningTime - second) / 60L;
                    long minute = remainMinute % 60L;
                    long remainHour = (remainMinute - minute) / 60L;
                    long hour = remainHour % 24L;
                    long remainDay = (remainHour - hour) / 24L;
                    result.put("RunningFor", String.format("%d 일 %d 시간 %d 분 %d 초", remainDay, hour, minute, second));

                    return result;
                }
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }

        return null;
    }

    // Cache SEED 의 Clean Up (소멸) 설정을 진행한다.
    @Override
    public Map<String, Integer> setCacheSeedingCleanUpSetting(Map<String, Object> param) {
        List<Container> containers = DockerUtil.fetchAllContainers();
        Map<String, Integer> countMap = new HashMap<>();
        for (Container container : containers) {
            JsonObject object = null;
            try {
                object = container.inspect();
                String cntName = object.getString("Name").replace("/", "");
                if (cntName.startsWith(DOCKER_SEED_NAME_PREFIX)) {
                    File seedFile = new File(dataPath + Constants.PROXY_SETTING_FILE_PATH + "/" + "/seed-" + cntName + ".yaml");
                    Map<String, Object> seedInfo = YAMLFileUtil.fetchMapByYAMLFile(seedFile);
                    Map<String, Object> paramMap = convertYamlMapToParamMap(cntName, seedInfo);

                    String cache = (String) paramMap.get("cache");

                    if (cache.equals(param.get("cache"))) {
                        paramMap.put("removeBefore", String.valueOf(param.get("removeBefore")));
                        paramMap.put("removeBeforeType", param.get("removeBeforeType"));

                        // 원래 있던 SEED 는 삭제하고 재생성한다. 실행 중인 Seeding 데이터는 일회성이기 때문이다.
                        // 주기적으로 실행되는 친구들에 대해서는 이를 같이 설정해야 한다.
                        removeSeedContainerByName(cntName);
                        createSeedContainer(paramMap);

                        countMap.put("SUCCESS", countMap.getOrDefault("SUCCESS", 0) + 1);
                    }
                }
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }

        return countMap;
    }

    /*
     * SEED 주기 관련 기능 : Truncated.
     * 2020.08.17
     */

}
