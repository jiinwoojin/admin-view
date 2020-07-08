package com.jiin.admin.website.util;

import com.amihaiemil.docker.Container;
import com.amihaiemil.docker.Docker;
import com.amihaiemil.docker.UnixDocker;
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
    private static List<Container> fetchAllContainers() {
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
     * docker list
     * @param filter 필터링
     * @return
     */
    public static List dockerContainers(String filter) {
        // 실행 / docker ps -a --format "table {{.ID}}\t{{.Image}}"
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
}
