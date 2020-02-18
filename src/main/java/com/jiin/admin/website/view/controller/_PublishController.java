package com.jiin.admin.website.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Admin View UI 작업물 (~ 2020.02.14.)
// 디렉토리 Ref : resources/templates/publish
@Controller
@RequestMapping("publish")
public class _PublishController {
    @RequestMapping(value = "home", method = { RequestMethod.GET })
    public String publishHomepage(Model model){
        return "publish/s00-dash-board";
    }

    @RequestMapping(value = "{page}", method = { RequestMethod.GET })
    public String publishPreview(ModelMap model, @PathVariable String page, HttpServletRequest request, HttpServletResponse response) {
        return String.format("publish/%s", page);
    }
}
