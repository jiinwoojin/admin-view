package com.jiin.admin.website.view.controller;

import com.jiin.admin.converter.ConverterGssXml;
import com.jiin.admin.converter.ConverterVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("style")
public class MVCStyleController {

    @Resource
    private HttpSession session;

    @Value("${project.data-path}")
    private String dataPath;

    @Resource
    private ConverterGssXml converter;

    @RequestMapping("converter")
    public String converter(Model model) {
        model.addAttribute("STYLE_PATH",dataPath + "/styles/GSS_STYLE.xml");
        return "page/style/converter";
    }

    @RequestMapping(value = "to-convert", method = RequestMethod.POST)
    public void toconvert(
            @RequestParam("file") MultipartFile file,
            ConverterVO param,
            Model model, HttpServletResponse response) throws ParserConfigurationException, IOException, SAXException, JAXBException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        if(file == null || file.isEmpty()){
            response.setStatus(HttpServletResponse.SC_OK);
            PrintWriter out = response.getWriter();
            out.write("스타일 파일이 업로드되지 않았습니다.");
            out.flush();
            out.close();
            return;
        }
        File layerfile = File.createTempFile("gss-style",".xml");
        file.transferTo(layerfile);
        File stylefile = new File(dataPath + "/styles/GSS_STYLE.xml");
        response.addHeader("Content-Disposition", "attachment; filename=converted-mapbox-style.json");
        PrintWriter out = response.getWriter();
        converter.convertLayerJson(stylefile,layerfile, out, param);
    }
}
