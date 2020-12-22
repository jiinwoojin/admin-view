package com.jiin.admin.milsymbol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jiin.admin.milsymbol.model.ICOPSWsi;
import com.jiin.admin.milsymbol.model.ICOPSWssl;
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
import java.util.*;

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
        File refDir = new File(dataPath + "/data_dir/milsymbols");
        File savefile = new File(dataPath + "/icops/ICOPS_WarSym.json");
        FileWriter writer = new FileWriter(savefile);
        ConverterMilsymbol parse = new ConverterMilsymbol();
        parse.convertJson(defFile,refDir,writer);
    }
    public void convertJson(File defFile, File refDir, Writer output) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        // set parse ref
        File[] refFiles = refDir.listFiles();
        Map<String,Map> refMap = new HashMap();
        for(File ref : refFiles){
            Map<String,Map> map = new ObjectMapper().readValue(ref, HashMap.class);
            Set<String> keys = map.keySet();
            for(String key : keys){
                Map data = map.get(key);
                refMap.put(String.valueOf(data.get("MIL_CODE")),data);
            }
        }
        // set parse info
        ICOPSWssl wssl = parseICOPSXml(defFile);
        List<ICOPSWsi> wsiList = wssl.getWsi();
        Map data = new LinkedHashMap();
        for(ICOPSWsi wsi : wsiList){
            String key = wsi.getHl();
            String milCode = wsi.getCd();
            MilsymbolVO milsymbol = new MilsymbolVO();
            milsymbol.setMilCode(wsi.getCd());
            milsymbol.setCodeKorName(wsi.getNll());
            milsymbol.setCodeName(wsi.getNcl());
            milsymbol.setApplyState(wsi.getAs());
            milsymbol.setSymbolState(wsi.getSs());
            milsymbol.setSymbolType(wsi.getSt());
            milsymbol.setSymbolCategory(wsi.getSc());
            Map ref = refMap.get(milCode);
            if(ref != null){
                milsymbol.setProperties(String.valueOf(ref.get("PROPERTIES")));
                milsymbol.setModifier(String.valueOf(ref.get("MODIFIER")));
            }else{
                milsymbol.setProperties("");
                milsymbol.setModifier("");
            }
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
