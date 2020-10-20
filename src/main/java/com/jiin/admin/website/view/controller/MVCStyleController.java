package com.jiin.admin.website.view.controller;

import com.jiin.admin.config.SessionService;
import com.jiin.admin.converter.ConverterGssXml;
import com.jiin.admin.dto.SymbolPositionDTO;
import com.jiin.admin.mapper.data.SymbolPositionMapper;
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
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("style")
public class MVCStyleController {

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
            @RequestParam("version") String version,
            @RequestParam("id") String id,
            @RequestParam("name") String name,
            @RequestParam("sprite") String sprite,
            @RequestParam("glyphs") String glyphs,
            @RequestParam("tiles") String tiles,
            Model model, HttpServletResponse response) throws ParserConfigurationException, IOException, SAXException, JAXBException {
        File layerfile = File.createTempFile("gss-style",".xml");
        file.transferTo(layerfile);
        File stylefile = new File(dataPath + "/styles/GSS_STYLE.xml");
        PrintWriter out = response.getWriter();
        Map param = new HashMap();
        param.put("mapbox-version",version);
        param.put("mapbox-id",id);
        param.put("mapbox-name",name);
        param.put("mapbox-sprite",sprite);
        param.put("mapbox-glyphs",glyphs);
        param.put("mapbox-tiles",tiles);
        response.addHeader("Content-Disposition", "attachment; filename=converted-mapbox-style.json");
        converter.convertLayerJsonFile(stylefile,layerfile, out, param);
    }
}
