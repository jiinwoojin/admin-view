package com.jiin.admin.website.view.controller;

import com.jiin.admin.servlet.AdminViewServlet;
import com.jiin.admin.vo.SeedContainerInfo;
import com.jiin.admin.website.view.component.DuplexRESTComponent;
import com.jiin.admin.website.view.service.ProxySeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("proxy")
public class MVCSeedController {
    private static final String DUPLEX_INIT_URI = String.format("/%s/proxy/seed/init-duplex", AdminViewServlet.CONTEXT_PATH);
    private static final String DUPLEX_STOP_URI = String.format("/%s/proxy/seed/stop-duplex", AdminViewServlet.CONTEXT_PATH);
    private static final String DUPLEX_CLEAN_UP_URI = String.format("/%s/proxy/seed/clean-up-duplex", AdminViewServlet.CONTEXT_PATH);

    @Value("${project.docker-name.seed-name-prefix}")
    private String DOCKER_SEED_NAME_PREFIX;

    @Resource
    private ProxySeedService proxySeedService;

    @Resource
    private DuplexRESTComponent duplexRESTComponent;

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
    @RequestMapping("seeding-init")
    public Map<String, Object> proxySeedingCreate(HttpServletRequest request, @RequestParam Map<String, Object> param) {
        SeedContainerInfo container = proxySeedService.createSeedContainer(param);
        String seedName = container.getName();
        if (seedName.matches(String.format("%s-[0-9]*", DOCKER_SEED_NAME_PREFIX))) {
            Map<String, String> strParam = new HashMap<>((Map) param);
            Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_INIT_URI, strParam); // Neighbor 정보 저장
            return new HashMap<String, Object>() {
                {
                    put("MESSAGE", String.format("[%s] CACHE 처리 정보 저장에 %s 했습니다. 동기화 진행 결과 : %d 성공 / %d 실패.", seedName, "성공", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
                    put("RESULT", container);
                }
            };
        } else {
            return new HashMap<String, Object>() {
                {
                    put("MESSAGE", String.format("[%s] CACHE 처리 정보 저장에 %s 했습니다.", seedName, "실패"));
                    put("RESULT", null);
                }
            };
        }
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
    public Map<String, Object> proxySeedingStop(HttpServletRequest request, @RequestParam("SEEDNAME") String name) {
        String seedName = name;
        if (seedName.matches(String.format("%s-[0-9]*", DOCKER_SEED_NAME_PREFIX))) {
            if (proxySeedService.removeSeedContainerByName(seedName)) {
                Map<String, String> param = new HashMap<String, String>() {
                    {
                        put("SEED_NAME", seedName);
                    }
                };
                Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_STOP_URI, param); // Neighbor 정보 저장
                return new HashMap<String, Object>() {
                    {
                        put("MESSAGE", String.format("[%s] CACHE 처리 정보 중단에 %s 했습니다. 동기화 진행 결과 : %d 성공 / %d 실패.", seedName, "성공", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
                        put("RESULT", true);
                    }
                };
            } else {
                return new HashMap<String, Object>() {
                    {
                        put("MESSAGE", String.format("[%s] CACHE 처리 정보 중단에 %s 했습니다.", seedName, "실패"));
                        put("RESULT", false);
                    }
                };
            }
        } else {
            return new HashMap<String, Object>() {
                {
                    put("MESSAGE", String.format("[%s] CACHE 처리 정보 중단에 %s 했습니다.", seedName, "실패"));
                    put("RESULT", false);
                }
            };
        }
    }

    /**
     * CACHE SEEDING 소멸 시점 설정
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping("cleanup-setting")
    public Map<String, Object> proxyCleanUpSetting(HttpServletRequest request, @RequestParam Map<String, Object> param) {
        Map<String, Integer> cntMap = proxySeedService.setCacheSeedingCleanUpSetting(param);
        String cache = (String) param.get("cache");
        int success = cntMap.getOrDefault("SUCCESS", 0);
        if (success > 0) {
            Map<String, String> strParam = new HashMap<>((Map) param);
            Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_CLEAN_UP_URI, strParam); // Neighbor 정보 저장
            return new HashMap<String, Object>() {
                {
                    put("MESSAGE", String.format("[%s] CACHE 소멸 시점 설정에 %s 했습니다. 설정 변경된 건 수는 %d 입니다. 동기화 진행 결과 : %d 성공 / %d 실패.", cache, "성공", success, map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
                    put("RESULT", true);
                }
            };
        } else {
            return new HashMap<String, Object>() {
                {
                    put("MESSAGE", String.format("[%s] CACHE 소멸 시점 설정에 %s 했습니다.", cache, "실패"));
                    put("RESULT", false);
                }
            };
        }
    }

    /**
     * SEEDING 생성 이중화
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "seed/init-duplex", method = RequestMethod.POST)
    public Map<String, Object> proxySeedingInitDuplex(@RequestBody Map<String, Object> param) {
        String seedName = (String) param.getOrDefault("SEED_NAME", "UNKNOWN");
        SeedContainerInfo container = proxySeedService.createSeedContainer(param);
        return new HashMap<String, Object>() {
            {
                put("result", seedName.equals(container.getName()));
            }
        };
    }

    /**
     * SEEDING 정지 이중화
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "seed/stop-duplex", method = RequestMethod.POST)
    public Map<String, Object> proxySeedingStopDuplex(@RequestBody Map<String, Object> param) {
        String seedName = (String) param.getOrDefault("SEED_NAME", "UNKNOWN");
        return new HashMap<String, Object>() {
            {
                put("result", seedName.equals("UNKNOWN") ? false : proxySeedService.removeSeedContainerByName(seedName));
            }
        };
    }

    /**
     * SEEDING 소멸 (clean-up) 설정 적용
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "seed/clean-up-duplex", method = RequestMethod.POST)
    public Map<String, Object> proxySeedingCleanUpDuplex(@RequestBody Map<String, Object> param) {
        Map<String, Integer> cntMap = proxySeedService.setCacheSeedingCleanUpSetting(param);
        return new HashMap<String, Object>() {
            {
                put("result", cntMap.getOrDefault("SUCCESS", 0) > 0);
            }
        };
    }

    /**
     * CACHE SEED CRON CYCLE 설정 : Truncated.
     * 2020.08.17
     */
}
