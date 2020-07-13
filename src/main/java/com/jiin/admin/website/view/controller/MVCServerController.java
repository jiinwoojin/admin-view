package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.servlet.AdminServerServlet;
import com.jiin.admin.servlet.AdminViewServlet;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.ServerCenterInfoModel;
import com.jiin.admin.website.util.RestClientUtil;
import com.jiin.admin.website.view.service.ContainerInfoService;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("server")
public class MVCServerController {
    @Value("${server.servlet.context-path}")
    private String CONTEXT_PATH;

    @Autowired
    private ServerCenterInfoService serverCenterInfoService;

    @Autowired
    private ContainerInfoService containerInfoService;

    @Autowired
    private SessionService sessionService;

    /**
     * 서비스의 등록 정보를 열람하는 페이지.
     * @param model Model
     */
    @RequestMapping("service-info")
    public String pageServiceInfo(Model model) {
        model.addAttribute("services", containerInfoService.loadGeoContainerInfoList());
        return "page/system/service-info";
    }

    /**
     * 서비스의 목록들을 관리하는 페이지
     * @param model Model
     */
    @RequestMapping("service-manage")
    public String pageServiceManagement(Model model) {
        ServerCenterInfo local = serverCenterInfoService.loadLocalInfoData();
        List<ServerCenterInfo> neighbors = serverCenterInfoService.loadNeighborList();
        neighbors.add(0, local);

        model.addAttribute("local", local);
        model.addAttribute("serviceMap", containerInfoService.loadGeoServiceMap());
        model.addAttribute("message", sessionService.message());
        model.addAttribute("histories", containerInfoService.loadContainerHistoryList());
        model.addAttribute("connections", neighbors);

        return "page/system/service-manage";
    }

    /**
     * Docker Container 실행 (시작, 종료, 재시작) 및 일반 서비스 실행 (재시작) 을 위한 링크
     * @param name String, method String
     */
    @RequestMapping("service-execute")
    public String linkServiceControlByNameAndMethod(@RequestParam String name, @RequestParam String method) {
        sessionService.message(String.format("[%s] 서비스의 [%s] 명령을 시작합니다.", name, method));
        containerInfoService.executeGeoServiceByNameAndMethod(name, method);
        return "redirect:service-manage";
    }

    /**
     * Docker Container 실행 및 일반 서비스 실행을 위한 REST API
     * @param param Map of String, String
     */
    @RequestMapping(value = "service-execute", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> restServiceControlByNameAndMethod(@RequestBody Map<String, Object> param) {
        String name = (String) param.get("name");
        String method = (String) param.get("method");
        containerInfoService.executeGeoServiceByNameAndMethod(name, method);
        return new HashMap<String, Object>(){{
            put("result", true);
        }};
    }

    @RequestMapping(value = "remote-service-execute", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> postDockerCheckByIpAndContainerName(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        String ip = (String) param.get("ip");
        String name = (String) param.get("name");
        String method = (String) param.get("method");
        String path = String.format("%s/%s/server/service-execute", CONTEXT_PATH, AdminViewServlet.CONTEXT_PATH);

        sessionService.message(String.format("[%s] 서비스의 [%s] 명령을 시작합니다.", name, method));
        return RestClientUtil.postREST(request.isSecure(), ip, path, new HashMap<String, String>(){{
            put("name", name);
            put("method", method);
        }});
    }

    /**
     * Container 작동 내역 기록을 전부 삭제하는 링크
     * @param
     */
    @RequestMapping("history-clean")
    public String linkHistoryClean() {
        boolean completed = containerInfoService.removeAllContainerHistoryData();
        if (completed) {
            sessionService.message("현재 시점 이전의 모든 작동 내역 기록들이 삭제 되었습니다.");
        } else {
            sessionService.message("현재 시점 이전의 데이터가 존재하지 않아 삭제 작업을 진행하지 않았습니다.");
        }
        return "redirect:service-manage";
    }

    /**
     * 서버 로컬, 연동 주소를 관리하는 페이지
     * @param model Model
     */
    @RequestMapping("service-address")
    public String pageServiceAddressConfig(Model model) {
        model.addAttribute("connections", serverCenterInfoService.loadRemoteList());
        model.addAttribute("neighbors", serverCenterInfoService.loadNeighborList());
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
    public String postServiceLocalSave(ServerCenterInfoModel serverCenterInfoModel) {
        boolean result = serverCenterInfoService.saveLocalData(serverCenterInfoModel);
        sessionService.message(String.format("LOCAL SERVER INFO [%s] 저장 %s 하였습니다.", serverCenterInfoModel.getName(), (result ? "성공" : "실패")));
        return "redirect:service-address";
    }

    /**
     * 연동 주소 정보를 저장하는 POST 요청
     * @param serverCenterInfoModel ServerCenterInfoModel
     */
    @RequestMapping(value = "remote-save", method = RequestMethod.POST)
    public String postServiceRemoteSave(ServerCenterInfoModel serverCenterInfoModel) {
        boolean result = serverCenterInfoService.saveRemoteData(serverCenterInfoModel);
        sessionService.message(String.format("REMOTE SERVER INFO [%s] 저장 %s 하였습니다.", serverCenterInfoModel.getName(), (result ? "성공" : "실패")));
        return "redirect:service-address";
    }

    /**
     * 연동 주소를 삭제하기 위한 링크
     * @param key String
     */
    @RequestMapping("remove-server")
    public String linkRemoveServerByKey(@RequestParam String key) {
        boolean result = serverCenterInfoService.removeDataByKey(key);
        sessionService.message(String.format("REMOTE SERVER INFO [%s] 삭제 %s 하였습니다.", key, (result ? "성공" : "실패")));
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
