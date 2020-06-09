package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.vo.ServerCenterInfo;
import com.jiin.admin.website.model.ServerCenterInfoModel;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import com.jiin.admin.website.view.service.ServiceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("server")
public class MVCServerController {
    @Autowired
    private ServerCenterInfoService serverCenterInfoService;

    @Autowired
    private ServiceInfoService serviceInfoService;

    @Autowired
    private SessionService sessionService;

    /**
     * 서비스의 등록 정보를 열람하는 페이지.
     * @param model Model
     */
    @RequestMapping("service-info")
    public String pageServiceInfo(Model model){
        return "page/system/service-info";
    }

    /**
     * 서비스의 목록들을 관리하는 페이지
     * @param model Model
     */
    @RequestMapping("service-manage")
    public String pageServiceManagement(Model model){
        model.addAttribute("local", serverCenterInfoService.loadLocalInfoData());
        model.addAttribute("serviceMap", serviceInfoService.loadGeoServiceMap());
        model.addAttribute("message", sessionService.message());
        return "page/system/service-manage";
    }

    /**
     * Docker Container 실행 (시작, 종료, 재시작) 및 일반 서비스 실행 (재시작) 을 위한 링크
     * @param name String, method String
     */
    @RequestMapping("service-execute")
    public String linkServiceControlByNameAndMethod(@RequestParam String name, @RequestParam String method) {
        sessionService.message(String.format("[%s] 서비스의 [%s] 명령을 시작합니다.", name, method));
        serviceInfoService.executeGeoServiceByNameAndMethod(name, method);
        return "redirect:service-manage";
    }

    /**
     * 서버 로컬, 연동 주소를 관리하는 페이지
     * @param model Model
     */
    @RequestMapping("service-address")
    public String pageServiceAddressConfig(Model model){
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
    public String postServiceLocalSave(ServerCenterInfoModel serverCenterInfoModel) {
        boolean result = serverCenterInfoService.saveLocalData(serverCenterInfoModel);
        sessionService.message(String.format("LOCAL SERVER INFO [%s] 저장 %s 하였습니다.", serverCenterInfoModel.getName(), (result ? "성공" : "실패")));
        return "redirect:service-address";
    }

    /**
     * 연동 주소 정보를 저장하는 POST 요청
     * @param request HttpServletRequest, serverCenterInfoModel ServerCenterInfoModel
     */
    @RequestMapping(value = "remote-save", method = RequestMethod.POST)
    public String postServiceRemoteSave(HttpServletRequest request, ServerCenterInfoModel serverCenterInfoModel) {
        ServerCenterInfo local = serverCenterInfoService.loadLocalInfoData();
        ServerCenterInfo remote = ServerCenterInfoModel.convertDTO(serverCenterInfoModel);
        boolean result = serverCenterInfoService.saveRemoteData(serverCenterInfoModel);
        if(serverCenterInfoModel.getMethod().equals("INSERT")) {
            serverCenterInfoService.sendDuplexRequest(request.getRequestURL().toString().startsWith("http://"), remote, local, "create-duplex");
        }
        if(serverCenterInfoModel.getMethod().equals("UPDATE")){
            List<ServerCenterInfo> sentServers = serverCenterInfoService.sendServerInfoList(request.getRequestURL().toString().startsWith("http://"), remote, "remote-list");
            sentServers.add(local);
            serverCenterInfoService.sendDuplexRequest(request.getRequestURL().toString().startsWith("http://"), sentServers, remote, "update-duplex");
        }
        sessionService.message(String.format("REMOTE SERVER INFO [%s] 저장 %s 하였습니다.", serverCenterInfoModel.getName(), (result ? "성공" : "실패")));
        return "redirect:service-address";
    }

    /**
     * 연동 주소를 삭제하기 위한 링크
     * @param request HttpServletRequest, key String
     */
    @RequestMapping("remove-server")
    public String linkRemoveServerByKey(HttpServletRequest request, @RequestParam String key){
        ServerCenterInfo local = serverCenterInfoService.loadLocalInfoData();
        ServerCenterInfo remote = serverCenterInfoService.loadRemoteInfoDataByKey(key);
        boolean result = serverCenterInfoService.removeDataByKey(key);
        serverCenterInfoService.sendDuplexRequest(request.getRequestURL().toString().startsWith("http://"), remote, local, "delete-duplex");
        sessionService.message(String.format("REMOTE SERVER INFO [%s] 삭제 %s 하였습니다.", key, (result ? "성공" : "실패")));
        return "redirect:service-address";
    }

    /**
     * 연동 주소 목록을 REST API 로 호출한다 : 서버 목록 갱신화 중 Update 기능에 반영하기 위함.
     * @param
     */
    @ResponseBody
    @RequestMapping("remote-list")
    public List<ServerCenterInfo> getRemoteServerList(){
        return serverCenterInfoService.loadRemoteList();
    }

    @ResponseBody
    @RequestMapping(value = "create-duplex", method = RequestMethod.POST)
    public Map<String, Object> postCreateDuplexWithServerVO(@RequestBody ServerCenterInfo serverCenterInfo){
        return new HashMap<String, Object>() {{
            put("result", serverCenterInfoService.saveRemoteData(ServerCenterInfoModel.convertModel("INSERT", serverCenterInfo)));
        }};
    }

    @ResponseBody
    @RequestMapping(value = "update-duplex", method = RequestMethod.POST)
    public Map<String, Object> postUpdateDuplexWithServerVO(@RequestBody ServerCenterInfo serverCenterInfo){
        return new HashMap<String, Object>() {{
            put("result", serverCenterInfoService.saveRemoteData(ServerCenterInfoModel.convertModel("UPDATE", serverCenterInfo)));
        }};
    }

    @ResponseBody
    @RequestMapping(value = "delete-duplex", method = RequestMethod.POST)
    public Map<String, Object> postDeleteDuplexWithServerVO(@RequestBody ServerCenterInfo serverCenterInfo){
        return new HashMap<String, Object>() {{
            put("result", serverCenterInfoService.removeDataByKey(serverCenterInfo.getKey()));
        }};
    }

    /**
     * 로그 정보를 관리하는 페이지
     * @param
     */
    @RequestMapping("log-manage")
    public String pageLogManagement(){
        return "page/system/log-manage";
    }
}
