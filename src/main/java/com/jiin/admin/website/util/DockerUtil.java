package com.jiin.admin.website.util;

import com.amihaiemil.docker.Container;
import com.amihaiemil.docker.Containers;
import com.amihaiemil.docker.Docker;
import com.amihaiemil.docker.UnixDocker;
import lombok.extern.slf4j.Slf4j;

import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DockerUtil {
    /**
     * Unix 기반 Docker 를 가져온다. (Windows 는 TCP 환경 이외에 사용 불가.)
     * @param
     */
    private static Docker fetchDefaultDocker(){
        return new UnixDocker(new File("/var/run/docker.sock"));
    }

    /**
     * 현재 Docker 가 가지고 있는 Container 들의 정보 중 일부를 반환한다.
     * @param
     */
    public static List<Map<String, JsonObject>> fetchContainerMetaInfoByProperty(String property){
        final Docker docker = fetchDefaultDocker();
        final Containers containers = docker.containers();

        List<Map<String, JsonObject>> list = new ArrayList<>();
        for(final Container container : containers){
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
        final Docker docker = fetchDefaultDocker();
        final Containers containers = docker.containers();

        for(final Container container : containers){
            JsonObject json = container.inspect();
            String ctnName = json.getString("Name");
            ctnName = ctnName.replace("/", "");

            if(ctnName.equalsIgnoreCase(name)) {
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
        final Docker docker = fetchDefaultDocker();
        final Containers containers = docker.containers();

        for(final Container container : containers){
            JsonObject json = container.inspect();
            String ctnName = json.getString("Name");
            ctnName = ctnName.replace("/", "");
            if(ctnName.equalsIgnoreCase(name)){
                switch(method){
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
                        log.error(name + " - " + "Docker 이벤트는 START, STOP, RESTART 기능 중에 가능합니다.");
                        return;
                }
            }
        }
    }
}
