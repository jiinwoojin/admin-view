package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.website.model.SymbolImageCreateModel;
import com.jiin.admin.website.model.SymbolPageModel;
import com.jiin.admin.website.view.service.SymbolImageService;
import com.jiin.admin.website.view.service.SymbolService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping("symbol")
public class MVCSymbolController {
    @Resource
    private SymbolService service;

    @Resource
    private SymbolImageService symbolImageService;

    @Resource
    private SessionService session;

    @RequestMapping("image-list")
    public String pageSymbolImageListView(Model model, SymbolPageModel symbolPageModel) {
        model.addAttribute("qs", symbolPageModel.getQueryString());
        model.addAttribute("resMap", symbolImageService.loadDataListAndCountByPaginationModel(symbolPageModel));
        model.addAttribute("obList", symbolImageService.loadOrderByOptionList());
        model.addAttribute("sbList", symbolImageService.loadSearchByOptionList());
        model.addAttribute("message", session.message());
        return "page/symbol/image-list";
    }

    @RequestMapping("image-create")
    public String pageSymbolImageCreateView(Model model, @ModelAttribute SymbolImageCreateModel symbolImageCreateModel, SymbolPageModel symbolPageModel) {
        model.addAttribute("qs", symbolPageModel.getQueryString());
        return "page/symbol/image-create";
    }

    @RequestMapping(value = "image-create", method = RequestMethod.POST)
    public String postSymbolImageCreateRedirect(@Valid SymbolImageCreateModel symbolImageCreateModel) {
        boolean status = symbolImageService.createImageData(symbolImageCreateModel);
        session.message(String.format("SYMBOL [%s] 그룹 추가 %s 하였습니다.", symbolImageCreateModel.getName(), status ? "성공" : "실패"));
        return "redirect:image-list?pg=1&sz=10";
    }

    @RequestMapping("image-update")
    public String pageSymbolImageUpdateView(Model model, @RequestParam long id, SymbolPageModel symbolPageModel) {
        return "page/symbol/image-update";
    }

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
