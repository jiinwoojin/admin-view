package com.jiin.admin.website.view.controller;

import com.jiin.admin.entity.MapSymbol;
import com.jiin.admin.website.view.service.SymbolService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("symbol")
public class SymbolController {

    @Resource
    private SymbolService service;

    @RequestMapping("list")
    public String list(Model model) {
        List<MapSymbol> list = service.list();
        model.addAttribute("RESULTS",list);
        return "page/symbol/list";
    }
}
