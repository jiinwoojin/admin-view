package com.jiin.admin.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiin.admin.converter.xml.*;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

@Service
public class ConveterGssXml {

    public GssContainer parseGssXml(File target) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(target);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        System.out.println("Target Root element :" + root.getNodeName());
        Object object = null;
        if(root.getNodeName().equals("MapLayer")){
            object = JAXBContext.newInstance(GssRootLayer.class).createUnmarshaller().unmarshal(target);
        }else if(root.getNodeName().equals("MapStyle")){
            object = JAXBContext.newInstance(GssRootStyle.class).createUnmarshaller().unmarshal(target);
        }else{
            System.out.println("Opps!!");
            return null;
        }
        return (GssContainer) object;
    }

    public static void main(String... args) throws IOException, SAXException, ParserConfigurationException, JAXBException {
        String dataPath = "/Users/neutti/Dev/Projects/admin-view/data/gss_style";
        File file = new File(dataPath + "/GSS_GROUND_LARGE_SCALE_LAYER.xml");
        ConveterGssXml parse = new ConveterGssXml();
        GssContainer result = parse.parseGssXml(file);
        System.out.println(result);
        //
        if(result instanceof GssRootLayer){
            for(GssLayer style:((GssRootLayer) result).getLayers()){
                System.out.println(style.getCategory());
            }
        }

        String json = new ObjectMapper().writeValueAsString(result);
        System.out.println(json);

        /*
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        MapboxMapLayer layer = new MapboxMapLayer();
        layer.setVersion("121212");
        marshaller.marshal(layer, System.out);
        */
        //



    }

    private static void visitChildNodes(NodeList nList) {
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println("Node Name = " + node.getNodeName() + "; Value = " + node.getTextContent());
                if (node.hasAttributes()) {
                    NamedNodeMap nodeMap = node.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node tempNode = nodeMap.item(i);
                        System.out.println("Attr name : " + tempNode.getNodeName()+ "; Value = " + tempNode.getNodeValue());
                    }
                    if (node.hasChildNodes()) {
                        visitChildNodes(node.getChildNodes());
                    }
                }
            }
        }
    }
}
