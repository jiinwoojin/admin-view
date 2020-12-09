package com.jiin.admin.milsymbol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jiin.admin.converter.mapbox.*;
import com.jiin.admin.milsymbol.model.ICOPSWssl;
import com.jiin.admin.milsymbol.model.ICOPSWsi;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConverterMilsymbol {

    /**
     * 스타일 컨버터 Sample 사용법
     */
    public static void main(String... args) throws IOException, SAXException, ParserConfigurationException, JAXBException {
        // TODO : dataPath 경로변경
        String dataPath = "/Users/neutti/Dev/Projects/admin-view/data";
        //
        File defFile = new File(dataPath + "/icops/ICOPS_WarSym.wssl");
        File propFile = new File(dataPath + "/icops/ICOPS_WarSym.wssl");
        MilsymbolVO param = new MilsymbolVO();
        ConverterMilsymbol parse = new ConverterMilsymbol();
        File savefile = new File(dataPath + "/icops/ICOPS_WarSym.json");
        FileWriter writer = new FileWriter(savefile);
        parse.convertJson(defFile,propFile,writer,param);
    }
    public void convertJson(File defFile, File propFile, Writer output, MilsymbolVO param) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        // set parse info
        ICOPSWssl wssl = parseICOPSXml(defFile);
        List<ICOPSWsi> wsiList = wssl.getWsi();
        Map data = new LinkedHashMap();
        for(ICOPSWsi wsi : wsiList){
            String key = wsi.getHl();
            MilsymbolVO milsymbol = new MilsymbolVO();
            milsymbol.setMilCode(wsi.getCd());
            milsymbol.setCodeKorName(wsi.getNll());
            milsymbol.setCodeName(wsi.getNcl());
            milsymbol.setDescription("0");
            milsymbol.setDirectionExplanation("0");
            milsymbol.setProperties("");
            milsymbol.setModifier("");
            milsymbol.setApplyState(wsi.getAs());
            milsymbol.setSymbolState(wsi.getSs());
            milsymbol.setSymbolType(wsi.getSt());
            milsymbol.setSymbolCategory(wsi.getSc());
            data.put(key,milsymbol);
        }
        new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValue(output,data);
    }

    public ICOPSWssl parseICOPSXml(File target) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(target);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        System.out.println("Target Root element : " + root.getNodeName());
        Object object = JAXBContext.newInstance(ICOPSWssl.class).createUnmarshaller().unmarshal(target);
        return (ICOPSWssl) object;
    }





}
