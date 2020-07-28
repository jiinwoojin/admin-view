package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.website.model.SymbolImageModel;
import com.jiin.admin.website.model.SymbolPageModel;
import com.jiin.admin.website.view.service.ServerCenterInfoService;
import com.jiin.admin.website.view.service.SymbolImageService;
import com.jiin.admin.website.view.service.SymbolService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    public String pageSymbolImageCreateView(Model model, @ModelAttribute SymbolImageModel symbolImageModel, SymbolPageModel symbolPageModel) {
        model.addAttribute("qs", symbolPageModel.getQueryString());
        return "page/symbol/image-create";
    }

    @RequestMapping(value = "image-create", method = RequestMethod.POST)
    public String postSymbolImageCreateRedirect(@Valid SymbolImageModel symbolImageModel) {
        boolean status = symbolImageService.createImageData(symbolImageModel);
        session.message(String.format("SYMBOL [%s] 그룹 추가 %s 하였습니다.", symbolImageModel.getName(), status ? "성공" : "실패"));
        return "redirect:image-list?pg=1&sz=10";
    }

    @RequestMapping("image-update")
    public String pageSymbolImageUpdateView(Model model, @RequestParam long id, SymbolPageModel symbolPageModel) {
        Map<String, Object> map = symbolImageService.loadImageUpdateData(id);
        model.addAttribute("model", map.get("model"));
        model.addAttribute("data", map.get("data"));
        model.addAttribute("qs", symbolPageModel.getQueryString());
        return "page/symbol/image-update";
    }

    // TODO : IMAGE UPDATE + CODE NAME 변경 적용

    @RequestMapping("image-delete")
    public String linkSymbolImageUpdateView(@RequestParam long id, @RequestParam String name) {
        boolean status = symbolImageService.deleteImageData(id);
        session.message(String.format("SYMBOL [%s] 그룹 삭제 %s 하였습니다.", name, status ? "성공" : "실패"));
        return "redirect:image-list?pg=1&sz=10";
    }

    @RequestMapping("position-delete")
    public String linkSymbolPositionDelete(@RequestParam long id, @RequestParam List<Long> ids, SymbolPageModel symbolPageModel) {
        boolean status = symbolImageService.deletePositionData(id, ids);
        session.message(String.format("선택하신 SYMBOL 데이터들에 대해 각각 삭제 %s 하였습니다.", status ? "성공" : "실패"));
        return String.format("redirect:image-update?id=%d&", id) + symbolPageModel.getQueryString();
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
    public byte[] symbolSetImagePart(@RequestParam(value = "name") String name) throws IOException {
        return symbolImageService.loadImageByteArrayByName(name);
    }

    @GetMapping(
        value = "position-image",
        produces = MediaType.IMAGE_PNG_VALUE
    )
    @ResponseBody
    public byte[] symbolPositionImagePart(@RequestParam(value="name") String name, @RequestParam(value = "xPos", defaultValue = "0") int xPos, @RequestParam(value = "yPos", defaultValue = "0") int yPos, @RequestParam(value = "width", defaultValue = "0") int width, @RequestParam(value = "height", defaultValue = "0") int height) throws IOException {
        return symbolImageService.loadPositionByteArrayByModel(name, xPos, yPos, width, height);
    }
}
