package com.jiin.admin.website.view.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiin.admin.Constants;
import com.jiin.admin.config.SessionService;
import com.jiin.admin.servlet.AdminServerServlet;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.*;
import com.jiin.admin.website.util.FileSystemUtil;
import com.jiin.admin.website.view.component.DuplexRESTComponent;
import com.jiin.admin.website.view.service.ProxyCacheService;
import com.jiin.admin.website.view.service.ProxySeedService;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("proxy")
public class MVCProxyController {
    private static final String DUPLEX_SETTING_URI = String.format("/%s/api/proxy/sync/yaml-save", AdminServerServlet.CONTEXT_PATH);

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

    @Value("${project.docker-name.mapproxy-name}")
    private String mapProxyContainer;

    @Autowired
    private ProxyCacheService proxyCacheService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ServerCenterInfoService serverCenterInfoService;

    @Resource
    private ProxySeedService proxySeedService;

    @Resource
    private DuplexRESTComponent duplexRESTComponent;

    // Proxy Cache 설정 결과 미리 보기
    @RequestMapping("preview")
    public String proxyLayerPreview(Model model) {
        model.addAttribute("local", serverCenterInfoService.loadLocalInfoData());
        model.addAttribute("port", MAP_PROXY_PORT);
        model.addAttribute("proxyLayers", proxyCacheService.loadDataListBySelected("LAYERS", true));
        model.addAttribute("proxyYAML", proxyCacheService.loadProxyYamlSetting());
        model.addAttribute("containerName", mapProxyContainer);
        return "page/proxy/preview";
    }

    // Proxy Cache 설정 결과 미리 보기 : YAML 파일 일부 수정 시. 차후 필요 여부에 따라 반영 예정.
    @RequestMapping(value = "yaml-save", method = RequestMethod.POST)
    public String postProxyYAMLContextDirectSave(Map<String, Object> form){
        String text = (String) form.get("code");
        FileSystemUtil.createAtFile(dataPath + Constants.PROXY_SETTING_FILE_PATH + "/" + Constants.PROXY_SETTING_FILE_NAME, text);
        sessionService.message("YAML 파일의 일부 내용이 수정 되었습니다.");
        return "redirect:preview";
    }

    // Proxy Cache 설정 화면
    @RequestMapping("setting")
    public String pageProxySetting(Model model) {
        model.addAttribute("message", sessionService.message());

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

    // Proxy Cache LAYER 설정
    @RequestMapping(value = "layer-save", method = RequestMethod.POST)
    public String postLayerSave(HttpServletRequest request, ProxyLayerModel proxyLayerModel) {
        boolean result = proxyCacheService.saveProxyLayerByModel(proxyLayerModel);
        if (proxyCacheService.loadDataSelectedByName("LAYER", proxyLayerModel.getName())) {
            proxyCacheService.saveYAMLFileByEachList(serverCenterInfoService.loadLocalInfoData()); // Local 정보 저장
            Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_SETTING_URI, new HashMap<>()); // Neighbor 정보 저장
            sessionService.message(String.format("[%s] LAYER 정보 저장에 %s 했습니다. 동기화 진행 결과 : %d 성공 / %d 실패.", proxyLayerModel.getName(), result ? "성공" : "실패", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
        } else {
            sessionService.message(String.format("[%s] LAYER 정보 저장에 %s 했습니다.", proxyLayerModel.getName(), result ? "성공" : "실패"));
        }
        return "redirect:setting";
    }

    // Proxy Cache SOURCE (WMS) 설정
    @RequestMapping(value = "source-wms-save", method = RequestMethod.POST)
    public String postSourceWMSSave(HttpServletRequest request, ProxySourceWMSModel proxySourceWMSModel) {
        boolean result = proxyCacheService.saveProxySourceWMSByModel(proxySourceWMSModel);
        if (proxyCacheService.loadDataSelectedByName("SOURCE-WMS", proxySourceWMSModel.getName())) {
            proxyCacheService.saveYAMLFileByEachList(serverCenterInfoService.loadLocalInfoData()); // Local 정보 저장
            Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_SETTING_URI, new HashMap<>()); // Neighbor 정보 저장
            sessionService.message(String.format("[%s] SOURCE (WMS) 정보 저장에 %s 했습니다. 동기화 진행 결과 : %d 성공 / %d 실패.", proxySourceWMSModel.getName(), result ? "성공" : "실패", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
        } else {
            sessionService.message(String.format("[%s] SOURCE (WMS) 정보 저장에 %s 했습니다.", proxySourceWMSModel.getName(), result ? "성공" : "실패"));
        }
        return "redirect:setting";
    }

    // Proxy Cache SOURCE (MAPSERVER) 설정
    @RequestMapping(value = "source-mapserver-save", method = RequestMethod.POST)
    public String postSourceMapServerSave(HttpServletRequest request, ProxySourceMapServerModel proxySourceMapServerModel) {
        boolean result = proxyCacheService.saveProxySourceMapServerByModel(proxySourceMapServerModel);
        if (proxyCacheService.loadDataSelectedByName("SOURCE-MAPSERVER", proxySourceMapServerModel.getName())) {
            proxyCacheService.saveYAMLFileByEachList(serverCenterInfoService.loadLocalInfoData()); // Local 정보 저장
            Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_SETTING_URI, new HashMap<>()); // Neighbor 정보 저장
            sessionService.message(String.format("[%s] SOURCE (MapServer) 정보 저장에 %s 했습니다. 동기화 진행 결과 : %d 성공 / %d 실패.", proxySourceMapServerModel.getName(), result ? "성공" : "실패", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
        } else {
            sessionService.message(String.format("[%s] SOURCE (MapServer) 정보 저장에 %s 했습니다.", proxySourceMapServerModel.getName(), result ? "성공" : "실패"));
        }
        return "redirect:setting";
    }

    // Proxy Cache CACHE 설정
    @RequestMapping(value = "cache-save", method = RequestMethod.POST)
    public String postCacheSave(HttpServletRequest request, ProxyCacheModel proxyCacheModel) {
        boolean result = proxyCacheService.saveProxyCacheByModel(proxyCacheModel);
        if (proxyCacheService.loadDataSelectedByName("CACHE", proxyCacheModel.getName())) {
            proxyCacheService.saveYAMLFileByEachList(serverCenterInfoService.loadLocalInfoData()); // Local 정보 저장
            Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_SETTING_URI, new HashMap<>()); // Neighbor 정보 저장
            sessionService.message(String.format("[%s] CACHE 정보 저장에 %s 했습니다. 동기화 진행 결과 : %d 성공 / %d 실패.", proxyCacheModel.getName(), result ? "성공" : "실패", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
        } else {
            sessionService.message(String.format("[%s] CACHE 정보 저장에 %s 했습니다.", proxyCacheModel.getName(), result ? "성공" : "실패"));
        }
        return "redirect:setting";
    }

    // Proxy Cache GLOBAL 설정
    @RequestMapping(value = "global-save", method = RequestMethod.POST)
    public String postGlobalSave(HttpServletRequest request, @RequestParam String json) {
        ObjectMapper mapper = new ObjectMapper();
        List<ProxyGlobalModel> list;
        try {
            list = mapper.readValue(json, new TypeReference<List<ProxyGlobalModel>>() {});
        } catch (JsonProcessingException e) {
            log.error("ERROR - " + e.getMessage());
            sessionService.message("GLOBAL 변수들을 수정하는 도중 오류가 발생했습니다.");
            return "redirect:setting";
        }

        ServerCenterInfo local = serverCenterInfoService.loadLocalInfoData();
        boolean result = proxyCacheService.saveProxyGlobalByModelList(list, local); // Local 정보 저장
        Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_SETTING_URI, new HashMap<>()); // Neighbor 정보 저장
        sessionService.message(String.format("GLOBAL 정보 저장에 %s 했습니다. 동기화 진행 결과 : %d 성공 / %d 실패.", result ? "성공" : "실패", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
        return "redirect:setting";
    }

    // Proxy Cache 데이터 삭제
    @RequestMapping("{type}-delete")
    public String linkProxyDataDelete(HttpServletRequest request, @PathVariable String type, @RequestParam long id, @RequestParam String name) {
        boolean selected = proxyCacheService.loadDataSelectedById(type.toUpperCase(), id);
        boolean result = proxyCacheService.removeProxyDataByIdAndType(id, type);
        if (selected) {
            proxyCacheService.saveYAMLFileByEachList(serverCenterInfoService.loadLocalInfoData()); // Local 정보 저장
            Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_SETTING_URI, new HashMap<>()); // Neighbor 정보 저장
            sessionService.message(String.format("[%s] %s 정보 삭제에 %s 했습니다. 동기화 진행 결과 : %d 성공 / %d 실패.", name, type.toUpperCase(), result ? "성공" : "실패", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
        } else {
            sessionService.message(String.format("[%s] %s 정보 삭제에 %s 했습니다.", name, type.toUpperCase(), result ? "성공" : "실패"));
        }
        return "redirect:setting";
    }

    // Proxy Cache YAML 설정 저장
    @RequestMapping(value = "checking-save", method = RequestMethod.POST)
    public String checkingProxyDataSettings(HttpServletRequest request, ProxySelectRequestModel proxySelectModel) {
        boolean result = proxyCacheService.setProxyDataSelectByModel(proxySelectModel, serverCenterInfoService.loadLocalInfoData()); // Local 정보 저장
        if (result) {
            Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_SETTING_URI, new HashMap<>()); // Neighbor 정보 저장
            sessionService.message(String.format("모든 PROXY 정보들을 수정하는데 %s 했습니다. 동기화 진행 결과 : %d 성공 / %d 실패.", result ? "성공" : "실패", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
        } else {
            sessionService.message("모든 PROXY 정보들을 설정하는 도중 오류가 발생했습니다.");
        }
        return "redirect:setting";
    }

    // SEEDING 진행 현황 페이지
    /**
     * docker run -it -d --rm --user 1001:1000 -v /data/jiapp:/data/jiapp -v /etc/localtime:/etc/localtime:ro --name jimap_seed jiinwoojin/jimap_mapproxy mapproxy-seed -f /data/jiapp/data_dir/proxy/mapproxy.yaml -s /data/jiapp/data_dir/proxy/seed.yaml -c 4 --seed ALL
     * @param model
     * @return
     */
    @RequestMapping("seeding")
    public String proxySeeding(Model model, @RequestParam(value = "menu", required = false, defaultValue = "SEED_SET") String menu){
        model.addAttribute("proxyCaches", proxySeedService.loadProxyCacheListBySelected());
        model.addAttribute("seedName", DOCKER_SEED_NAME_PREFIX);
        model.addAttribute("seedContainers", proxySeedService.loadSeedContainerInfoList());
        return "page/proxy/seeding";
    }


    // SEEDING 진행 현황 로그 가져오기
    /**
     * [14:03:36] 13  75.44% 140.97656, 43.24219, 141.32812, 43.59375 (8593072 tiles)
     * @return
     */
    @ResponseBody
    @RequestMapping("seeding-info")
    public Map<String, Object> proxySeedingInfo(@RequestParam("SEEDNAME") String seedName) {
        return proxySeedService.loadLogTextInContainerByName(seedName);
    }

    /**
     * SEED 생성
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("seeding-create")
    public Map<String, Object> proxySeedingCreate(@RequestParam Map<String, Object> param) {
        return new HashMap<String, Object>() {
            {
                put("RESULT", proxySeedService.createSeedContainer(param));
            }
        };
    }

    /**
     * 기본 값 Seeding 가져오기 - 사용 여부에 따라 폐기 될 수 있음.
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping("default-seeding-reload")
    public Map<String, Object> proxySeedingReload() {
        return new HashMap<String, Object>() {
            {
                put("RESULT", proxySeedService.resetDefaultSeeding());
            }
        };
    }

    /**
     * SEEDING 중단
     * @param name
     * @return
     */
    @ResponseBody
    @RequestMapping("seeding-stop")
    public Map<String, Object> proxySeedingStop(@RequestParam("SEEDNAME") String name) {
        return new HashMap<String, Object>() {
            {
                put("RESULT", proxySeedService.removeSeedContainerByName(name) ? "SUCCESS" : "FAILURE");
            }
        };
    }

    /**
     * CACHE SEEDING 소멸 시점 설정
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("cleanup-setting")
    public Map<String, Integer> proxyCleanUpSetting(@RequestParam Map<String, Object> param) {
        return proxySeedService.setCacheSeedingCleanUpSetting(param);
    }

    /**
     * CACHE SEED CRON CYCLE 설정 : Truncated.
     * 2020.08.17
     */
}
