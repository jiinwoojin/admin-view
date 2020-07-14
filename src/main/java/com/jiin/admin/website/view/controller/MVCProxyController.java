package com.jiin.admin.website.view.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiin.admin.Constants;
import com.jiin.admin.config.SessionService;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.*;
import com.jiin.admin.website.util.DockerUtil;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.util.YAMLFileUtil;
import com.jiin.admin.website.view.service.ProxyCacheService;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("proxy")
public class MVCProxyController {
    @Value("${project.server-port.mapproxy-port}")
    private int MAP_PROXY_PORT;

    @Value("${project.server-port.mapserver-port}")
    private int MAP_SERVER_PORT;

    @Value("${project.docker-name.seed-name-prefix}")
    private String DOCKER_SEED_NAME_PREFIX;

    @Value("${project.docker-name.default-seed-name}")
    private String DOCKER_DEFAULT_SEED_NAME;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${project.data-path}")
    private String dataPath;

    @Autowired
    private ProxyCacheService proxyCacheService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ServerCenterInfoService serverCenterInfoService;

    @RequestMapping("setting")
    public String pageProxySetting(Model model) {
        model.addAttribute("message", sessionService.message());
        model.addAttribute("neighbors", serverCenterInfoService.loadNeighborList());

        model.addAttribute("layers", proxyCacheService.loadDataList("LAYERS"));
        model.addAttribute("sources", proxyCacheService.loadDataList("SOURCES"));
        model.addAttribute("caches", proxyCacheService.loadDataList("CACHES"));
        model.addAttribute("globals", proxyCacheService.loadDataList("GLOBALS"));

        model.addAttribute("mapServerPort", MAP_SERVER_PORT);
        model.addAttribute("mapServerAddress", Constants.MAP_SERVER_WMS_URL);

        model.addAttribute("proxyCacheDirectory", proxyCacheService.loadProxyCacheMainDir());
        model.addAttribute("dataDirectory", proxyCacheService.loadDataDir());
        model.addAttribute("mapServerBinary", proxyCacheService.loadMapServerBinary());

        return "page/proxy/setting";
    }

    @RequestMapping(value = "layer-save", method = RequestMethod.POST)
    public String postLayerSave(ProxyLayerModel proxyLayerModel) {
        boolean result = proxyCacheService.saveProxyLayerByModel(proxyLayerModel, false);
        sessionService.message(String.format("[%s] LAYER 정보 저장에 %s 했습니다.", proxyLayerModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "source-wms-save", method = RequestMethod.POST)
    public String postSourceWMSSave(ProxySourceWMSModel proxySourceWMSModel) {
        boolean result = proxyCacheService.saveProxySourceWMSByModel(proxySourceWMSModel, serverCenterInfoService.loadLocalInfoData(), false);
        sessionService.message(String.format("[%s] SOURCE (MapServer) 정보 저장에 %s 했습니다.", proxySourceWMSModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "source-mapserver-save", method = RequestMethod.POST)
    public String postSourceMapServerSave(ProxySourceMapServerModel proxySourceMapServerModel) {
        boolean result = proxyCacheService.saveProxySourceMapServerByModel(proxySourceMapServerModel, false);
        sessionService.message(String.format("[%s] SOURCE (MapServer) 정보 저장에 %s 했습니다.", proxySourceMapServerModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "cache-save", method = RequestMethod.POST)
    public String postCacheSave(ProxyCacheModel proxyCacheModel) {
        boolean result = proxyCacheService.saveProxyCacheByModel(proxyCacheModel, false);
        sessionService.message(String.format("[%s] CACHE 정보 저장에 %s 했습니다.", proxyCacheModel.getName(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "global-save", method = RequestMethod.POST)
    public String postGlobalSave(@RequestParam String json) {
        ObjectMapper mapper = new ObjectMapper();
        List<ProxyGlobalModel> list = new ArrayList<>();
        try {
            list = mapper.readValue(json, new TypeReference<List<ProxyGlobalModel>>() {});
        } catch (JsonProcessingException e) {
            log.error("ERROR - " + e.getMessage());
            sessionService.message("GLOBAL 변수들을 수정하는 도중 오류가 발생했습니다.");
            return "redirect:setting";
        }

        sessionService.message(String.format("GLOBAL 정보 저장에 %s 했습니다.", proxyCacheService.saveProxyGlobalByModelList(list) ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping("{type}-delete")
    public String linkProxyDataDelete(@PathVariable String type, @RequestParam long id, @RequestParam String name) {
        boolean result = proxyCacheService.removeProxyDataByIdAndType(id, type);
        sessionService.message(String.format("[%s] %s 정보 삭제에 %s 했습니다.", name, type.toUpperCase(), result ? "성공" : "실패"));
        return "redirect:setting";
    }

    @RequestMapping(value = "checking-save", method = RequestMethod.POST)
    public String checkingProxyDataSettings(ProxySelectRequestModel proxySelectModel) {
        boolean result = proxyCacheService.setProxyDataSelectByModel(proxySelectModel);
        sessionService.message(result ? "모든 PROXY 정보들이 수정 되었습니다." : "모든 PROXY 정보들을 설정하는 도중 오류가 발생했습니다.");
        return "redirect:setting";
    }

    @RequestMapping("preview")
    public String proxyLayerPreview(Model model) {
        model.addAttribute("local", serverCenterInfoService.loadLocalInfoData());
        model.addAttribute("port", MAP_PROXY_PORT);
        model.addAttribute("proxyLayers", proxyCacheService.loadDataListBySelected("LAYERS", true));
        model.addAttribute("proxyYAML", proxyCacheService.loadProxyYamlSetting());
        return "page/proxy/preview";
    }

    /**
     * docker run -it -d --rm --user 1001:1000 -v /data/jiapp:/data/jiapp -v /etc/localtime:/etc/localtime:ro --name jimap_seed jiinwoojin/jimap_mapproxy mapproxy-seed -f /data/jiapp/data_dir/proxy/mapproxy.yaml -s /data/jiapp/data_dir/proxy/seed.yaml -c 4 --seed ALL
     * @param model
     * @return
     */
    @RequestMapping("seeding")
    public String proxySeeding(Model model){
        List<Map> seeds = new ArrayList<>();
        List<Map> defaultcontainers = DockerUtil.dockerContainers(DOCKER_DEFAULT_SEED_NAME);
        for(Map container : defaultcontainers){
            if(container.get("Names").toString().equals(DOCKER_DEFAULT_SEED_NAME)){
                container.put("DEFAULT",true);
                seeds.add(container);
            }
        }
        List<Map> containers = DockerUtil.dockerContainers(DOCKER_SEED_NAME_PREFIX);
        for(Map container : containers){
            if(container.get("Names").toString().startsWith(DOCKER_SEED_NAME_PREFIX)){
                container.put("DEFAULT",false);
                seeds.add(container);
            }
        }
        model.addAttribute("seedName", DOCKER_SEED_NAME_PREFIX);
        model.addAttribute("seedContainers", seeds);
        return "page/proxy/seeding";
    }

    /**
     * [14:03:36] 13  75.44% 140.97656, 43.24219, 141.32812, 43.59375 (8593072 tiles)
     * @return
     */
    @ResponseBody
    @RequestMapping("seeding-info")
    public Map proxySeedingInfo(@RequestParam("SEEDNAME") String seedName) throws IOException {
        List<Map> containers = DockerUtil.dockerContainers(seedName);
        Map container = containers.get(0);
        String[] fields = new String[]{"TIME","LEVEL","PER","XMIN","YMIN","XMAX","YMAX","TILES_CNT"};
        String lastlog = DockerUtil.logLastline(seedName);
        // DEV
        if(activeProfile.equals("outside")) lastlog = "[14:03:36] 13  75.44% 140.97656, 43.24219, 141.32812, 43.59375 (8593072 tiles)";
        String[] logs = lastlog.split(" ");
        int idx = 0;
        Map lastlogMap = new HashMap();
        for(String log : logs){
            if(!StringUtils.isEmpty(log)){
                lastlogMap.put(fields[idx++],log);
            }
            if(idx == fields.length)
                break;
        }
        container.put("LOGS",lastlogMap);
        File mapproxy = new File(dataPath + "/proxy/mapproxy.yaml");
        if(mapproxy.exists()){
            Map<String, Object> mapproxyInfo = YAMLFileUtil.fetchMapByYAMLFile(mapproxy);
            container.put("MAPPROXY",mapproxyInfo);
        }
        File seed = new File(dataPath + "/proxy/seed-" + seedName + ".yaml");
        if(seed.exists()){
            Map<String, Object> seedInfo = YAMLFileUtil.fetchMapByYAMLFile(seed);
            container.put("SEED",seedInfo);
        }
        return container;
    }

    /**
     * SEED 생성
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("seeding-create")
    public Map proxySeedingCreate(@RequestParam Map param) throws IOException {
        long now = new Date().getTime();
        String seedName = DOCKER_SEED_NAME_PREFIX + "-" + now;
        param.put("DATA_PATH",dataPath);
        param.put("SEED_NAME",seedName);
        String result = DockerUtil.runSeed(param);
        List<Map> containers = DockerUtil.dockerContainers(seedName);
        if(containers.size() == 0){
            Map info = new HashMap();
            info.put("RESULT",result);
            return info;
        }else{
            containers.get(0).put("RESULT",result);
            return containers.get(0);
        }
    }

    @ResponseBody
    @RequestMapping("seeding-stop")
    public Map proxySeedingStop(@RequestParam("SEEDNAME") String name) throws IOException {
        // *.map IMAGEPATH 삭제
        Map<String, Object> mapproxyInfo = YAMLFileUtil.fetchMapByYAMLFile(new File(dataPath + "/proxy/mapproxy.yaml"));
        LinkedHashMap sources = (LinkedHashMap) mapproxyInfo.get("sources");
        List<String> tmpDirs = new ArrayList();
        Set keys = sources.keySet();
        for(Object key:keys){
            Map<String, Object> source = (Map<String, Object>) sources.get(key);
            Map<String, Object> req = (Map<String, Object>) source.get("req");
            String mapPath = (String) req.get("map");
            if(Files.exists(Paths.get(mapPath))){
                List<String> lines = FileSystemUtil.fetchFileContextToList(mapPath);
                for(String line:lines){
                    if(line.contains("IMAGEPATH")){
                        tmpDirs.add(line.replace("IMAGEPATH","").replaceAll("\"","").trim());
                    }
                }
            }
        }
        for(String tmpDir:tmpDirs){
            if(Files.exists(Paths.get(tmpDir))){
                // TODO: 삭제여부 확인후 주석해
                // FileSystemUtil.deleteFile(tmpDir); 제
            }
        }
        Map info = new HashMap();
        info.put("RESULT",DockerUtil.removeSeed(name,dataPath));
        return info;
    }
}
