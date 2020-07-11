package com.jiin.admin.website.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("sync")
public class MVCSyncController {
    @RequestMapping("database")
    public String serviceShutdownPage(Model model) {
        return "page/sync/database";
    }

    @RequestMapping("file")
    public String serviceRestartPage(Model model) {
        return "page/sync/file";
    }
}
