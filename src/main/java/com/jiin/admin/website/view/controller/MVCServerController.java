package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.website.model.ServerCenterInfoModel;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import com.jiin.admin.website.view.service.ServiceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
        model.addAttribute("serviceMap", serviceInfoService.loadGeoContainerMap());
        return "page/system/service-manage";
    }

    /**
     * 서버 로컬, 연동 주소를 관리하는 페이지
     * @param model Model
     */
    @RequestMapping("service-address")
    public String pageServiceAddressConfig(Model model){
        model.addAttribute("connections", serverCenterInfoService.loadDataList());
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
    public String linkRemoveServerByName(@RequestParam String key){
        boolean result = serverCenterInfoService.removeDataByKey(key);
        sessionService.message(String.format("REMOTE SERVER INFO [%s] 삭제 %s 하였습니다.", key, (result ? "성공" : "실패")));
        return "redirect:service-address";
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
