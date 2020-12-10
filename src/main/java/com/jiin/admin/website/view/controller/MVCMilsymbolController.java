package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.converter.ConverterGssXml;
import com.jiin.admin.converter.ConverterVO;
import com.jiin.admin.dto.SymbolPositionDTO;
import com.jiin.admin.mapper.data.SymbolPositionMapper;
import com.jiin.admin.milsymbol.ConverterMilsymbol;
import com.jiin.admin.website.model.SymbolImageModel;
import com.jiin.admin.website.model.SymbolImagePageModel;
import com.jiin.admin.website.model.SymbolPositionEditModel;
import com.jiin.admin.website.model.SymbolPositionPageModel;
import com.jiin.admin.website.view.service.SymbolImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("milsymbol")
public class MVCMilsymbolController {

    @Value("${project.data-path}")
    private String dataPath;

    @Resource
    private ConverterMilsymbol converter;

    @RequestMapping("converter")
    public String converter(Model model) {
        model.addAttribute("REF_DIR",dataPath + "/milsymbols");
        return "page/milsymbol/converter";
    }

    @RequestMapping(value = "to-convert", method = RequestMethod.POST)
    public void toconvert(
            @RequestParam("file") MultipartFile file,
            HttpServletResponse response) throws ParserConfigurationException, IOException, SAXException, JAXBException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        if(file == null || file.isEmpty()){
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.write("군대부호 정의 파일이 업로드되지 않았습니다.");
            out.flush();
            out.close();
            return;
        }
        File defFile = File.createTempFile("milsymbol",".xml");
        file.transferTo(defFile);
        File refDir = new File(dataPath + "/milsymbols");
        response.addHeader("Content-Disposition", "attachment; filename=converted-milsymbol.json");
        PrintWriter out = response.getWriter();
        converter.convertJson(defFile,refDir,out);
    }
}
