package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.website.model.LayerPageModel;
import com.jiin.admin.website.model.LayerRowModel;
import com.jiin.admin.website.util.SpreadSheetUtil;
import com.jiin.admin.website.view.service.LayerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("manage")
public class MVCLayerController {
    @Autowired
    private LayerService layerService;

    @Resource
    private SessionService session;

    /**
     * ABC.lay 데이터 목록 조회
     * @param model Model, layerPageModel LayerPageModel
     */
    @RequestMapping("layer-list")
    private String pageLayerList(Model model, LayerPageModel layerPageModel) {
        model.addAttribute("resMap", layerService.loadDataListAndCountByPaginationModel(layerPageModel));
        model.addAttribute("obList", layerService.loadOrderByOptionList());
        model.addAttribute("sbList", layerService.loadSearchByOptionList());
        model.addAttribute("message", session.message());
        return "page/manage/layer-list";
    }

    /**
     * ABC.lay 데이터 추가
     * @Param layerPageModel LayerPageModel, dataFile MultipartFile, layerPageModel LayerPageModel
     */
    @PostMapping("layer-create")
    public String postLayerCreate(LayerDTO layerDTO, @RequestParam("data_file") MultipartFile dataFile, LayerPageModel layerPageModel) {
        boolean result = layerService.createData(layerDTO, dataFile);
        session.message(String.format("LAYER [%s] 추가 %s하였습니다.", layerDTO.getName(), (result ? "성공" : "실패")));
        return String.format("redirect:layer-list?%s", layerPageModel.getQueryString());
    }

    /**
     * ABC.lay 데이터 수정
     * @Param layerPage
     */
    @PostMapping("layer-update")
    public String postLayerUpdate(LayerDTO layerDTO, @RequestParam(value = "data_file", required = false) MultipartFile dataFile, LayerPageModel layerPageModel) {
        boolean result = layerService.setData(layerDTO, dataFile);
        session.message(String.format("LAYER [%s] 수정 %s하였습니다.", layerDTO.getName(), (result ? "성공" : "실패")));
        return String.format("redirect:layer-list?%s", layerPageModel.getQueryString());
    }

    /**
     * ABC.lay 데이터 삭제
     * @Param layerPage
     */
    @ResponseBody
    @PostMapping("layer-delete")
    public boolean restLayerDelete(@RequestParam long layerId) {
        return layerService.removeData(layerId);
    }

    /**
     * Layer Excel 업로드
     * @Param file MultipartFile
     */
    @PostMapping("layer-excel-upload")
    public String postLayerExcelUpload(@RequestParam("excelFile") MultipartFile excelFile, LayerPageModel layerPageModel) {
        List<LayerRowModel> rows = SpreadSheetUtil.loadLayerRowModelListBySpreadSheet(excelFile);
        Map<String, Integer> map = layerService.saveMultipleDataByModelList(rows);
        int success = map.getOrDefault("success", 0);
        int size = rows.size();
        session.message(String.format("EXCEL 파일에 기재된 LAYER 목록 %s 개 중 %s 개 성공, %s 개 실패 했습니다.", size, success, size - success));
        return String.format("redirect:layer-list?%s", layerPageModel.getQueryString());
    }

    /**
     * Layer Excel 샘플 파일
     * @Param
     */
    @GetMapping("excel-sample-download")
    public void linkExcelSampleDownload(HttpServletResponse response) {
        Workbook workbook = SpreadSheetUtil.loadLayerRowModelSampleFile();
        response.setHeader("Content-Disposition", "attachment; filename=\"sample-layer-register.xlsx\";");
        response.setContentType("application/vnd.ms-excel");
        try(BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream())) {
            workbook.write(output);
            output.flush();
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }
    }
}
