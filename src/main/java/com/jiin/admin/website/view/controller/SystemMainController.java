package com.jiin.admin.website.view.controller;

import com.jiin.admin.entity.ServerConnectionEntity;
import com.jiin.admin.website.model.ServerFormModel;
import com.jiin.admin.website.view.service.ServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("server")
public class SystemMainController {
    @Autowired
    private ServerInfoService serverInfoService;

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
        List<ServerConnectionEntity> ownConnections = serverInfoService.getOwnRelateConnectionsList();
        model.addAttribute("ownConnection", serverInfoService.getOwnServerConnection());
        model.addAttribute("connections", ownConnections);
        model.addAttribute("connectionIds", ownConnections.stream().map(o -> o.getId()).collect(Collectors.toList()));
        model.addAttribute("typeConnections", serverInfoService.getOwnAllConnectionsListSameType());
        return "page/system/service-address";
    }

    @RequestMapping(value = "connection-save", method = RequestMethod.POST)
    public String addressConfigPageSaveLink(ServerFormModel serverFormModel) {
        if(serverInfoService.serverInfoSave(serverFormModel))
            return "redirect:service-address";
        else return "redirect:service-address?error";
    }

    @RequestMapping("remove-server/{svrId}")
    public String serviceRemoveBySvrId(@PathVariable long svrId){
        if(serverInfoService.serverInfoDelete(svrId))
            return "redirect:../service-address";
        else return "redirect:../service-address?error";
    }

    // 관계 획득을 위한 1단계 작업 진행.
    @RequestMapping(value = "change-relation", method = RequestMethod.POST)
    public String changeRelationWithSvrIds(HttpServletRequest request){
        System.out.println(Arrays.toString(request.getParameterValues("subSvrIds")));
        System.out.println(request.getParameter("mainSvrId"));
        return "redirect:service-address";
    }
}
