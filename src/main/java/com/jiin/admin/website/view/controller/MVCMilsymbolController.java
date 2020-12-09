package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.dto.SymbolPositionDTO;
import com.jiin.admin.mapper.data.SymbolPositionMapper;
import com.jiin.admin.website.model.SymbolImageModel;
import com.jiin.admin.website.model.SymbolImagePageModel;
import com.jiin.admin.website.model.SymbolPositionEditModel;
import com.jiin.admin.website.model.SymbolPositionPageModel;
import com.jiin.admin.website.view.service.SymbolImageService;
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
@RequestMapping("milsymbol")
public class MVCMilsymbolController {
    @Resource
    private SymbolPositionMapper symbolPositionMapper;

    @Resource
    private SymbolImageService symbolImageService;

    @Resource
    private SessionService session;

    @RequestMapping("image-list")
    public String pageSymbolImageListView(Model model, SymbolImagePageModel symbolImagePageModel) {
        model.addAttribute("qs", symbolImagePageModel.getQueryString());
        model.addAttribute("resMap", symbolImageService.loadDataListAndCountByPaginationModel(symbolImagePageModel));
        model.addAttribute("obList", symbolImageService.loadOrderByOptionList());
        model.addAttribute("sbList", symbolImageService.loadSearchByOptionList());
        model.addAttribute("message", session.message());
        return "page/symbol/image-list";
    }

    @RequestMapping("image-create")
    public String pageSymbolImageCreateView(Model model, @ModelAttribute SymbolImageModel symbolImageModel, SymbolImagePageModel symbolImagePageModel) {
        model.addAttribute("qs", symbolImagePageModel.getQueryString());
        return "page/symbol/image-create";
    }

    @RequestMapping(value = "image-create", method = RequestMethod.POST)
    public String postSymbolImageCreateRedirect(@Valid SymbolImageModel symbolImageModel) {
        boolean status = symbolImageService.createImageData(symbolImageModel);
        session.message(String.format("SYMBOL [%s] 그룹 추가 %s 하였습니다.", symbolImageModel.getName(), status ? "성공" : "실패"));
        return "redirect:image-list?pg=1&sz=10";
    }

    @RequestMapping("image-delete")
    public String linkSymbolImageUpdateView(@RequestParam long id, @RequestParam String name) {
        boolean status = symbolImageService.deleteImageData(id);
        session.message(String.format("SYMBOL [%s] 그룹 삭제 %s 하였습니다.", name, status ? "성공" : "실패"));
        return "redirect:image-list?pg=1&sz=10";
    }

    @RequestMapping("image-update")
    public String pageSymbolImageUpdateView(Model model, SymbolImagePageModel symbolImagePageModel, SymbolPositionPageModel symbolPositionPageModel) {
        Map<String, Object> map = symbolImageService.loadImageUpdateDataByPageModel(symbolPositionPageModel);
        model.addAttribute("model", map.get("model"));
        model.addAttribute("data", map.get("data"));
        model.addAttribute("positions", map.get("positions"));
        model.addAttribute("count", map.get("count"));

        model.addAttribute("imageQuery", symbolImagePageModel.getQueryString());
        model.addAttribute("positionQuery", symbolPositionPageModel.getQueryString());
        model.addAttribute("message", session.message());
        model.addAttribute("jsonContext", symbolImageService.loadJSONContextByImageId(symbolPositionPageModel.getImgId()));

        model.addAttribute("obList", symbolImageService.loadPositionOrderByOptionList());

        return "page/symbol/image-update";
    }

    @RequestMapping(value = "image-update", method = RequestMethod.POST)
    public String pageSymbolImageUpdateView(@Valid SymbolImageModel symbolImageModel, SymbolImagePageModel symbolImagePageModel, SymbolPositionPageModel symbolPositionPageModel) {
        boolean status = symbolImageService.setImageData(symbolImageModel);
        session.message(String.format("SYMBOL [%s] 그룹 데이터 수정 %s 하였습니다.", symbolImageModel.getName(), status ? "성공" : "실패"));
        return String.format("redirect:image-update?%s&%s", symbolPositionPageModel.getQueryString(), symbolImagePageModel.getQueryString());
    }

    @RequestMapping(value = "position-update", method = RequestMethod.POST)
    public String postSymbolPositionUpdate(SymbolImagePageModel symbolImagePageModel, SymbolPositionEditModel symbolPositionEditModel, SymbolPositionPageModel symbolPositionPageModel) {
        boolean status = symbolImageService.setPositionData(symbolPositionEditModel);
        session.message(String.format("SYMBOL [%s] 그룹 데이터 수정 %s 하였습니다.", symbolPositionEditModel.getBeforeName(), status ? "성공" : "실패"));
        return String.format("redirect:image-update?%s&%s", symbolPositionPageModel.getQueryString(), symbolImagePageModel.getQueryString());
    }

    @RequestMapping("position-delete")
    public String linkSymbolPositionDelete(@RequestParam List<Long> ids, SymbolImagePageModel symbolImagePageModel, SymbolPositionPageModel symbolPositionPageModel) {
        boolean status = symbolImageService.deletePositionData(symbolPositionPageModel.getImgId(), ids);
        session.message(String.format("선택하신 SYMBOL 데이터들에 대해 각각 삭제 %s 하였습니다.", status ? "성공" : "실패"));
        return String.format("redirect:image-update?%s&%s", symbolPositionPageModel.getQueryString(), symbolImagePageModel.getQueryString());
    }

    @GetMapping(
            value = "data-image",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    @ResponseBody
    public byte[] symbolByteImageAtDatabase(@RequestParam(value = "name") String name, @RequestParam(value = "imageId") long imageId) throws IOException {
        SymbolPositionDTO position = symbolPositionMapper.findByNameAndImageId(name, imageId);
        if(position == null) return null;
        return position.getPngBytes();
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

    @GetMapping(
        value = "position-image-by-id",
        produces = MediaType.IMAGE_PNG_VALUE
    )
    @ResponseBody
    public byte[] symbolPositionImagePart(@RequestParam(value="id") long id) throws IOException {
        return symbolImageService.loadImageByteByPositionId(id);
    }
}
