package com.jiin.admin.website.view.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("display")
public class DisplayMainController {
    @RequestMapping("2d-map")
    public String twoDimensionDisplayView(Model model){
        return "page/display/2d-map";
    }
}
