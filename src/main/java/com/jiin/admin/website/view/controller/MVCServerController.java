package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.website.model.ServerCenterInfoModel;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
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
    private SessionService sessionService;

    @RequestMapping("service-info")
    public String pageServiceInfo(Model model){
        return "page/system/service-info";
    }

    @RequestMapping("service-manage")
    public String pageServiceManagement(Model model){
        return "page/system/service-manage";
    }

    @RequestMapping("service-address")
    public String pageServiceAddressConfig(Model model){
        model.addAttribute("connections", serverCenterInfoService.loadDataList());
        model.addAttribute("local", serverCenterInfoService.loadLocalInfoData());
        model.addAttribute("kinds", serverCenterInfoService.loadKindList());
        model.addAttribute("zones", serverCenterInfoService.loadZoneList());
        model.addAttribute("message", sessionService.message());
        return "page/system/service-address";
    }

    @RequestMapping(value = "local-save", method = RequestMethod.POST)
    public String postServiceLocalSave(ServerCenterInfoModel serverCenterInfoModel) {
        boolean result = serverCenterInfoService.saveLocalData(serverCenterInfoModel);
        sessionService.message(String.format("LOCAL SERVER INFO [%s] 저장 %s 하였습니다.", serverCenterInfoModel.getName(), (result ? "성공" : "실패")));
        return "redirect:service-address";
    }

    @RequestMapping(value = "remote-save", method = RequestMethod.POST)
    public String postServiceRemoteSave(ServerCenterInfoModel serverCenterInfoModel) {
        boolean result = serverCenterInfoService.saveRemoteData(serverCenterInfoModel);
        sessionService.message(String.format("REMOTE SERVER INFO [%s] 저장 %s 하였습니다.", serverCenterInfoModel.getName(), (result ? "성공" : "실패")));
        return "redirect:service-address";
    }

    @RequestMapping("remove-server")
    public String linkRemoveServerByName(@RequestParam String key){
        boolean result = serverCenterInfoService.removeDataByKey(key);
        sessionService.message(String.format("REMOTE SERVER INFO [%s] 삭제 %s 하였습니다.", key, (result ? "성공" : "실패")));
        return "redirect:service-address";
    }

    @RequestMapping("log-manage")
    public String pageLogManagement(){
        return "page/system/log-manage";
    }
}
