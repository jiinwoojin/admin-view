package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.model.RoleModel;
import com.jiin.admin.website.security.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("role")
public class MVCRoleController {
    @Autowired
    private AccountService accountService;

    @RequestMapping("list")
    public String roleListView(Model model) {
        model.addAttribute("roles", accountService.findAllRoles());
        model.addAttribute("roleModel", new RoleModel());
        return "page/role/list";
    }

    @RequestMapping(value = "add-role", method = RequestMethod.POST)
    public String roleInsertRedirect(Model model, RoleModel role) {
        if (accountService.createRoleWithModel(role)) {
            return "redirect:list";
        } else return "redirect:list?error";
    }

    @RequestMapping(value = "update-role", method = RequestMethod.POST)
    public String roleUpdateRedirect(Model model, RoleModel role) {
        if (accountService.updateRoleWithModel(role)) {
            return "redirect:list";
        } else return "redirect:list?error";
    }

    @RequestMapping("delete-role/{id}")
    public String roleDeleteLink(Model model, @PathVariable long id) {
        if (accountService.deleteRoleById(id)) {
            return "redirect:../list";
        } else return "redirect:../list?error";
    }
}
