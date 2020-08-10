package com.jiin.admin.website.util;

import com.amihaiemil.docker.*;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.util.StringUtils;

import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
public class DockerUtil {
    /**
     * Unix 기반 Docker 를 가져온다. (Windows 는 TCP 환경 이외에 사용 불가.)
     * @param
     */
    private static Docker fetchDefaultDocker() {
        return new UnixDocker(new File("/var/run/docker.sock"));
    }

    /**
     * Docker 에 있는 모든 Container 를 가져온다. 상태가 어떻게 됐든 간에.
     * @param
     */
    public static List<Container> fetchAllContainers() {
        final Docker docker = fetchDefaultDocker();
        final Iterator<Container> iter = docker.containers().all();

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED), false)
                            .collect(Collectors.toList());
    }

    /**
     * 현재 Docker 가 가지고 있는 Container 들의 정보 중 일부를 반환한다.
     * @param
     */
    public static List<Map<String, JsonObject>> fetchContainerMetaInfoByProperty(String property) {
        List<Container> containers = fetchAllContainers();
        List<Map<String, JsonObject>> list = new ArrayList<>();
        for (final Container container : containers) {
            try {
                JsonObject json = container.inspect();
                String key = json.getString("Name");
                list.add(new HashMap<String, JsonObject>() {{
                    put(key.toLowerCase(), json.getJsonObject(property));
                }});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    /**
     * Docker Container 단일 서비스 상태를 가져온다.
     * @param name String
     */
    public static JsonObject loadContainerByNameAndProperty(String name, String property) throws IOException {
        List<Container> containers = fetchAllContainers();
        for (final Container container : containers) {
            JsonObject json = container.inspect();
            String ctnName = json.getString("Name");
            ctnName = ctnName.replace("/", "");

            if (ctnName.equalsIgnoreCase(name)) {
                return json.getJsonObject(property);
            }
        }

        return null;
    }

    /**
     * 해당 Docker 의 Container 이름으로 서비스 상태를 조정한다.
     * @param name String, method String
     */
    public static void executeContainerByNameAndMethod(String name, String method) throws IOException {
        List<Container> containers = fetchAllContainers();
        for (final Container container : containers) {
            JsonObject json = container.inspect();
            String ctnName = json.getString("Name");
            ctnName = ctnName.replace("/", "");
            if (ctnName.equalsIgnoreCase(name)) {
                switch(method) {
                    case "START" :
                        container.start();
                        return;
                    case "STOP" :
                        container.stop();
                        return;
                    case "RESTART" :
                        container.restart();
                        return;
                    default :
                        log.error("ERROR - " + "Docker 이벤트는 START, STOP, RESTART 기능 중에 가능합니다.");
                        return;
                }
            }
        }
    }

    /**
     * 목록조회 (O)
     * docker ps -a --format "table {{.ID}}\t{{.Image}}"
     * @param filter 필터링
     * @return
     */
    public static List<Map> dockerContainers(String filter) {
        String[] fileds = new String[]{"ID","Image","Command","CreatedAt","RunningFor","Ports","Status","Size","Names","Labels","Mounts","Networks"};
        String separator = "#";
        String command = "docker ps -a ";
        if (!StringUtils.isEmpty(filter)) {
            command += "--filter name=" + filter + " ";
        }
        command += "--format \"table {{.";
        command += String.join("}}"+separator+"{{.",fileds);
        command += "}}\"";
        log.info(command);
        List<String> result = LinuxCommandUtil.fetchResultByLinuxCommonToList(command);
        List<Map> datas = new ArrayList<Map>();
        for (String line:result) {
            String[] dataArr = line.split(separator);
            if (dataArr[0].equalsIgnoreCase("CONTAINER ID")) {
                continue;
            }
            Map dataMap= new HashMap();
            int index = 0;
            for (String data:dataArr) {
                dataMap.put(fileds[index++],data.trim());
            }
            datas.add(dataMap);
        }
        return datas;
    }

    /**
     * Seed Docker 제거 (O)
     * docker rm jimap_seed_123128131232
     * @return
     */
    public static String removeSeed(String seedName, String dataPath) throws IOException {
        String command = "docker rm " + seedName;
        executeContainerByNameAndMethod(seedName, "STOP");
        List<String> resultMap = LinuxCommandUtil.fetchResultByLinuxCommonToList(command);
        String result = resultMap.get(0);
        if(result.equals(seedName)){ //SUCCESS
            String seedpath = dataPath + "/proxy/seed-" + seedName + ".yaml";
            FileSystemUtil.deleteFile(seedpath);
        }
        return result;
    }

    public static String reloadSeed(String seedName, String seedPath, String dataPath) {
        String mapproxypath = dataPath + "/proxy/mapproxy.yaml";
        // run docker
        // docker run -it -d --rm --user 1001:1000 -v /data/jiapp:/data/jiapp -v /etc/localtime:/etc/localtime:ro --name jimap_seed jiinwoojin/jimap_mapproxy mapproxy-seed -f /data/jiapp/data_dir/proxy/mapproxy.yaml -s /data/jiapp/data_dir/proxy/seed.yaml -c 4 --seed ALL
        String command = "docker run -i -d --rm --user [UID]:[GID] -v /data/jiapp:/data/jiapp -v /etc/localtime:/etc/localtime:ro --name [SEED_NAME] jiinwoojin/jimap_mapproxy mapproxy-seed -f [MAPPROXY.YAML] -s [SEED.YAML] -c 4 --seed ALL";
        command = command.replace("[UID]",LinuxCommandUtil.fetchUID());
        command = command.replace("[GID]",LinuxCommandUtil.fetchGID());
        command = command.replace("[SEED_NAME]",seedName);
        command = command.replace("[MAPPROXY.YAML]",mapproxypath);
        command = command.replace("[SEED.YAML]",seedPath);
        log.info(command);
        List<String> result = LinuxCommandUtil.fetchResultByLinuxCommonToList(command);
        if(result.size() == 0){
            return "";
        }else{
            return result.get(0);
        }
    }

    /**
     * Seed Docker 생성 (O)
     * @return
     */
    public static String runSeed(Map param) throws IOException {
        // create seed.yaml
        String dataPath = (String) param.get("DATA_PATH");
        String seedName = (String) param.get("SEED_NAME");
        Map seed = new LinkedHashMap();
        Map seeds = new LinkedHashMap();
        Map basic_seed = new LinkedHashMap();
        String[] caches = new String[]{(String) param.get("cache")};
        String[] coveragesArr = new String[]{(String) param.get("coverage")};
        Map levels = new LinkedHashMap();
        levels.put("from",Integer.parseInt((String) param.get("levelFrom")));
        levels.put("to",Integer.parseInt((String) param.get("levelTo")));
        Map refresh_before = new LinkedHashMap();
        String refreshBeforeType = (String) param.get("refreshBeforeType");
        refresh_before.put(refreshBeforeType,Integer.parseInt((String) param.get("refreshBefore")));
        basic_seed.put("caches",caches);
        basic_seed.put("coverages",coveragesArr);
        basic_seed.put("levels",levels);
        basic_seed.put("refresh_before",refresh_before);
        seeds.put("basic_seed", basic_seed);    // 이부분 수정
        seed.put("seeds",seeds);                // 이부분 수정
        Map coverages = new LinkedHashMap();
        Integer[] bbox = new Integer[]{
                Integer.parseInt((String) param.get("xmin")),
                Integer.parseInt((String) param.get("ymin")),
                Integer.parseInt((String) param.get("xmax")),
                Integer.parseInt((String) param.get("ymax"))
        };
        Map coverage = new LinkedHashMap();
        coverage.put("bbox",bbox);
        coverage.put("srs",param.get("projection"));
        coverages.put(param.get("coverage"),coverage);
        seed.put("coverages",coverages);
        //{seeds={basic_seed={caches=[basic], coverages=[korea], levels={from=11, to=14}, refresh_before={hours=1}}}, coverages={korea={bbox=[100, 0, 160, 70], srs=EPSG:4326}}}
        String context = YAMLFileUtil.fetchYAMLStringByMap(seed, "AUTO");
        String seedpath = dataPath + "/proxy/seed-" + seedName + ".yaml";
        FileSystemUtil.createAtFile(seedName, context);
        // run docker
        return reloadSeed("seed-" + seedName,seedpath,dataPath);
    }

    /**
     * 로그 마지막줄 조회
     * docker logs --tail 1 jimap_seed
     * @param name
     * @return
     */
    public static String logLastline(String name) {
        String command = "docker logs --tail 1 " + name;
        log.info(command);
        List<String> result = LinuxCommandUtil.fetchResultByLinuxCommonToList(command);
        if(result.size() == 0){
            return "";
        }else{
            return result.get(0);
        }
    }
}
