package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.entity.AccountEntity;
import com.jiin.admin.servlet.AdminViewServlet;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.ContainerExecuteModel;
import com.jiin.admin.website.model.ServerCenterInfoModel;
import com.jiin.admin.website.util.RestClientUtil;
import com.jiin.admin.website.view.component.DuplexRESTComponent;
import com.jiin.admin.website.view.service.ContainerInfoService;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("server")
public class MVCServerController {
    private static final String DUPLEX_CREATE_URI = String.format("/%s/server/create-duplex", AdminViewServlet.CONTEXT_PATH);
    private static final String DUPLEX_UPDATE_URI = String.format("/%s/server/update-duplex", AdminViewServlet.CONTEXT_PATH);
    private static final String DUPLEX_REMOVE_URI = String.format("/%s/server/remove-duplex", AdminViewServlet.CONTEXT_PATH);

    @Value("${server.servlet.context-path}")
    private String CONTEXT_PATH;

    @Autowired
    private ServerCenterInfoService serverCenterInfoService;

    @Autowired
    private ContainerInfoService containerInfoService;

    @Autowired
    private SessionService sessionService;

    @Resource
    private DuplexRESTComponent duplexRESTComponent;

    /**
     * 서비스의 등록 정보를 열람하는 페이지.
     * @param model Model
     */
    @RequestMapping("service-info")
    public String pageServiceInfo(Model model) {
        return "page/system/service-info";
    }

    /**
     * 서비스의 목록들을 관리하는 페이지
     * @param model Model
     */
    @RequestMapping("service-manage")
    public String pageServiceManagement(Model model, @RequestParam(required = false) String zone) {
        ServerCenterInfo local = serverCenterInfoService.loadLocalInfoData();
        if (zone == null) {
            zone = local != null ? local.getZone() : "UNKNOWN";
        }
        String searchZone = zone;
        List<ServerCenterInfo> remotes = serverCenterInfoService.loadRemoteList().stream().filter(o -> o.getZone().equals(searchZone)).collect(Collectors.toList());
        if (searchZone.equals(local.getZone())) {
            remotes.add(0, local);
        }

        model.addAttribute("serviceMap", containerInfoService.loadGeoServiceMap());
        model.addAttribute("message", sessionService.message());
        model.addAttribute("histories", containerInfoService.loadContainerHistoryList());
        model.addAttribute("connections", remotes);
        model.addAttribute("local", local);
        model.addAttribute("zones", serverCenterInfoService.loadZoneList());

        return "page/system/service-manage";
    }

    /**
     * Docker Container 실행 및 일반 서비스 실행을 위한 REST API
     * @param param Map of String, String
     */
    @RequestMapping(value = "service-execute", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> restServiceControlByNameAndMethod(@RequestBody Map<String, Object> param) {
        ContainerExecuteModel model = ContainerExecuteModel.convertToModel(param);
        containerInfoService.executeGeoServiceByContainerExecuteModel(model);
        return new HashMap<String, Object>() {{
            put("result", model != null);
        }};
    }

    /**
     * Local 에서 Docker Container 실행 및 일반 서비스 실행을 위한 REST API
     * @param param request HttpServletRequest, param Map of String, Object
     */
    @RequestMapping(value = "remote-service-execute", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> postDockerCheckByIpAndContainerName(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String ip = (String) param.get("ip");
        String path = String.format("%s/%s/server/service-execute", CONTEXT_PATH, AdminViewServlet.CONTEXT_PATH);

        String name = (String) param.get("name");
        String method = (String) param.get("method");
        String hostname = (String) param.get("hostname");

        sessionService.message(String.format("[%s] 서버 - [%s] 서비스의 [%s] 명령을 시작합니다.", hostname, name, method));

        AccountEntity user = (AccountEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return RestClientUtil.postREST(request.isSecure(), ip, path, new HashMap<String, String>(){{
            put("service", name);
            put("command", method);
            put("hostname", hostname);
            put("user", user != null ? user.getUsername() : "ANONYMOUS USER");
        }});
    }

    /**
     * 실행 뒤 REDIRECT 를 위한 메소드
     */
    @RequestMapping("execute-redirect")
    public String redirectExecute(){
        return "redirect:service-manage";
    }

    /**
     * 서버 로컬, 연동 주소를 관리하는 페이지
     * @param model Model
     */
    @RequestMapping("service-address")
    public String pageServiceAddressConfig(Model model) {
        model.addAttribute("connections", serverCenterInfoService.loadRemoteList());
        model.addAttribute("local", serverCenterInfoService.loadLocalInfoData());
        model.addAttribute("kinds", serverCenterInfoService.loadKindList());
        model.addAttribute("zones", serverCenterInfoService.loadZoneList());
        model.addAttribute("message", sessionService.message());
        return "page/system/service-address";
    }

    /**
     * 로컬 주소 정보를 저장하는 POST 요청
     * @param serverCenterInfoModel ServerCenterInfoModel
     */
    @RequestMapping(value = "local-save", method = RequestMethod.POST)
    public String postServiceLocalSave(HttpServletRequest request, ServerCenterInfoModel serverCenterInfoModel) {
        boolean result = serverCenterInfoService.saveLocalData(serverCenterInfoModel);
        if (result) {
            Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_UPDATE_URI, ServerCenterInfoModel.convertMap(serverCenterInfoModel)); // Neighbor 정보 저장
            sessionService.message(String.format("LOCAL SERVER INFO [%s] 저장에 %s 했습니다. 이중화 진행 결과 : %d 성공 / %d 실패.", serverCenterInfoModel.getName(), result ? "성공" : "실패", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
        } else {
            sessionService.message(String.format("LOCAL SERVER INFO [%s] 저장 %s 하였습니다.", serverCenterInfoModel.getName(), "실패"));
        }
        sessionService.message(String.format("LOCAL SERVER INFO [%s] 저장 %s 하였습니다.", serverCenterInfoModel.getName(), (result ? "성공" : "실패")));
        return "redirect:service-address";
    }

    /**
     * 연동 주소 정보를 저장하는 POST 요청
     * @param serverCenterInfoModel ServerCenterInfoModel
     */
    @RequestMapping(value = "remote-save", method = RequestMethod.POST)
    public String postServiceRemoteSave(HttpServletRequest request, ServerCenterInfoModel serverCenterInfoModel) {
        boolean result = serverCenterInfoService.saveRemoteData(serverCenterInfoModel);
        if (result) {
            Map<String, Object> map = new HashMap<>();
            switch (serverCenterInfoModel.getMethod()) {
                case "INSERT":
                    map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_CREATE_URI, ServerCenterInfoModel.convertMap(serverCenterInfoModel)); // Neighbor 정보 저장
                    break;
                case "UPDATE":
                    map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_UPDATE_URI, ServerCenterInfoModel.convertMap(serverCenterInfoModel)); // Neighbor 정보 저장
                    break;
            }
            sessionService.message(String.format("REMOTE SERVER INFO [%s] 저장에 %s 했습니다. 이중화 진행 결과 : %d 성공 / %d 실패.", serverCenterInfoModel.getName(), result ? "성공" : "실패", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
        } else {
            sessionService.message(String.format("REMOTE SERVER INFO [%s] 저장 %s 하였습니다.", serverCenterInfoModel.getName(), "실패"));
        }
        return "redirect:service-address";
    }

    /**
     * 연동 주소를 삭제하기 위한 링크
     * @param key String
     */
    @RequestMapping("remove-server")
    public String linkRemoveServerByKey(HttpServletRequest request, @RequestParam String key) {
        boolean result = serverCenterInfoService.removeDataByKey(key);
        if (result) {
            Map<String, Object> map = duplexRESTComponent.sendDuplexRESTWithData(request, DUPLEX_REMOVE_URI, new HashMap<String, String>() {{
                put("key", key);
            }}); // Neighbor 정보 저장
            sessionService.message(String.format("REMOTE SERVER INFO [%s] 삭제 %s 했습니다. 이중화 진행 결과 : %d 성공 / %d 실패.", key, result ? "성공" : "실패", map.getOrDefault("success", 0), map.getOrDefault("failure", 0)));
        } else {
            sessionService.message(String.format("REMOTE SERVER INFO [%s] 삭제 %s 하였습니다.", key, "실패"));
        }
        return "redirect:service-address";
    }

    /**
     * 연동 주소 목록을 REST API 로 호출한다 : 서버 목록 갱신화 중 Update 기능에 반영하기 위함.
     * @param
     */
    @ResponseBody
    @RequestMapping("remote-list")
    public List<ServerCenterInfo> getRemoteServerList() {
        List<ServerCenterInfo> list = serverCenterInfoService.loadRemoteList();
        list.add(0, serverCenterInfoService.loadLocalInfoData());
        return list;
    }

    /**
     * 서버 추가 이중화 작업
     * @param serverCenterInfo ServerCenterInfo
     */
    @ResponseBody
    @RequestMapping(value = "create-duplex", method = RequestMethod.POST)
    public Map<String, Object> postCreateDuplexWithServerVO(@RequestBody ServerCenterInfo serverCenterInfo) {
        return new HashMap<String, Object>() {{
            put("result", serverCenterInfoService.saveRemoteData(ServerCenterInfoModel.convertModel("INSERT", serverCenterInfo)));
        }};
    }

    /**
     * 서버 수정 이중화 작업
     * @param serverCenterInfo ServerCenterInfo
     */
    @ResponseBody
    @RequestMapping(value = "update-duplex", method = RequestMethod.POST)
    public Map<String, Object> postUpdateDuplexWithServer(@RequestBody ServerCenterInfo serverCenterInfo) {
        ServerCenterInfo local = serverCenterInfoService.loadLocalInfoData();
        return new HashMap<String, Object>() {{
            if (local.getKey().equals(serverCenterInfo.getKey())) {
                put("result", serverCenterInfoService.saveLocalData(ServerCenterInfoModel.convertModel("UPDATE", serverCenterInfo)));
            } else {
                put("result", serverCenterInfoService.saveRemoteData(ServerCenterInfoModel.convertModel("UPDATE", serverCenterInfo)));
            }
        }};
    }

    /**
     * 서버 삭제 이중화 작업
     * @param map Map
     */
    @ResponseBody
    @RequestMapping(value = "remove-duplex", method = RequestMethod.POST)
    public Map<String, Object> postDeleteDuplexWithServerKey(@RequestBody Map<String, String> map) {
        return new HashMap<String, Object>() {{
            put("result", serverCenterInfoService.removeDataByKey(map.get("key")));
        }};
    }

    /**
     * 로그 정보를 관리하는 페이지
     * @param
     */
    @RequestMapping("log-manage")
    public String pageLogManagement() {
        return "page/system/log-manage";
    }
}
