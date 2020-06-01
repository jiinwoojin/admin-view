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
public class SystemMainController {
    @Autowired
    private ServerCenterInfoService serverCenterInfoService;

    @Autowired
    private SessionService sessionService;

    @RequestMapping("service-info")
    public String serviceInfoPage(Model model){
        return "page/system/service-info";
    }

    @RequestMapping("service-manage")
    public String serviceManagePage(Model model){
        return "page/system/service-manage";
    }

    @RequestMapping("service-address")
    public String addressConfigPage(Model model){
        model.addAttribute("connections", serverCenterInfoService.loadDataList());
        model.addAttribute("kinds", serverCenterInfoService.loadKindList());
        model.addAttribute("zones", serverCenterInfoService.loadZoneList());
        model.addAttribute("message", sessionService.message());
        return "page/system/service-address";
    }

    @RequestMapping(value = "connection-save", method = RequestMethod.POST)
    public String addressConfigPageSaveLink(ServerCenterInfoModel serverCenterInfoModel) {
        boolean result = serverCenterInfoService.saveData(serverCenterInfoModel);
        sessionService.message(String.format("SERVER INFO [%s] 저장 %s 하였습니다.", serverCenterInfoModel.getName(), (result ? "성공" : "실패")));
        return "redirect:service-address";
    }

    @RequestMapping("remove-server")
    public String serviceRemoveBySvrId(@RequestParam String name){
        boolean result = serverCenterInfoService.removeDataByName(name);
        sessionService.message(String.format("SERVER INFO [%s] 삭제 %s 하였습니다.", name, (result ? "성공" : "실패")));
        return "redirect:service-address";
    }

    @RequestMapping("log-manage")
    public String logManage(){
        return "page/system/log-manage";
    }
}
