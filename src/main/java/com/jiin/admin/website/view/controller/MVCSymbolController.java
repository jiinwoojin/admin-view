package com.jiin.admin.website.view.controller;

import com.jiin.admin.website.view.service.SymbolService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;

@Controller
@RequestMapping("symbol")
public class MVCSymbolController {
    @Resource
    private SymbolService service;

    @RequestMapping("list")
    public String symbolListView(Model model, @RequestParam(value = "pg", defaultValue = "1") int pg, @RequestParam(value = "sz", defaultValue = "8") int sz, @RequestParam(value = "ob", defaultValue = "0") int ob, @RequestParam(value = "st", defaultValue = "") String st) throws IOException {
        model.addAttribute("jsonContext", service.getSymbolJSONContext());
        model.addAttribute("resMap", service.findSymbolPositionsByPagination(pg, sz, ob, st));
        model.addAttribute("obList", service.symbolOrderByOptions());
        return "page/symbol/list";
    }

    @GetMapping(
        value = "set-image",
        produces = MediaType.IMAGE_PNG_VALUE
    )
    @ResponseBody
    public byte[] symbolSetImage() throws IOException {
        return service.getSymbolSetImages();
    }

    @GetMapping(
        value = "image",
        produces = MediaType.IMAGE_PNG_VALUE
    )
    @ResponseBody
    public byte[] symbolImagePart(@RequestParam(value = "xPos", defaultValue = "0") int x, @RequestParam(value = "yPos", defaultValue = "0") int y, @RequestParam(value = "width", defaultValue = "0") int width, @RequestParam(value = "height", defaultValue = "0") int height) throws IOException {
        return service.getSymbolPartsWithPos(x, y, width, height);
    }
}
