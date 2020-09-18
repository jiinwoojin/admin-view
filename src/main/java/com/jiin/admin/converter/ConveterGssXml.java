package com.jiin.admin.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jiin.admin.converter.gss.*;
import com.jiin.admin.converter.mapbox.*;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

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

    private GssStyle findGssStyle(String styleName, GssContainer object) {
        List<GssStyle> styles = ((GssRootStyle) object).getStyle();
        for(GssStyle style : styles){
            if(style.getName().equals(styleName)){
                return style;
            }
        }
        return null;
    }

    public void convertLayerJsonFile(File styleFile, File layerFile, File outputFile) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        String sourceName = "tegola";
        MapboxRoot mapbox = new MapboxRoot();
        mapbox.setVersion(8);
        mapbox.setId("uzymq5sw3");
        mapbox.setName("G25k_style");
        mapbox.setSprite("http://211.172.246.71:11000/GSymbol/GSSSymbol");
        mapbox.setGlyphs("http://211.172.246.71:11000/fonts/{fontstack}/{range}.pbf");
        MapboxSource source = new MapboxSource();
        source.setType("vector");
        source.setTiles(Arrays.asList(new String[]{"http://211.172.246.71:11000/maps/g25k/{z}/{x}/{y}.pbf"}));
        source.setMinZoom(0);
        source.setMaxZoom(20);
        mapbox.putSources(sourceName,source);
        // add background layer
        MapboxLayer background = new MapboxLayer();
        background.setId("background");
        background.setType("background");
        background.setLayout(new MapboxLayout());
        background.setPaint(new MapboxPaint("rgba(255, 255, 255, 1)"));
        mapbox.addLayers(background);
        // set parse info
        GssContainer styles = parseGssXml(styleFile);
        GssRootLayer result = (GssRootLayer) parseGssXml(layerFile);
        List<GssLayer> layers = result.getLayer();
        int i = 0;
        for(GssLayer layer : layers){
            String shpSource = layer.getSHPSource().toLowerCase();
            String featureType = layer.getGeometryType();
            String labelColumn = layer.getLabelColumn();
            String displayType = layer.getDisplayType();
            List<GssFeature> features = layer.getFeature();
            if(features != null){
                for(GssFeature feature : features){
                    if(displayType.equals("Both") || displayType.equals("Geometry")){
                        // Geometry
                        String styleName = feature.getGeometryStyle();
                        List<GssVVTStyle> vvtStyles = feature.getVVTStyle();
                        GssStyle style = findGssStyle(styleName,styles);
                        if(style == null){
                            System.out.println("not find Style!! - " + shpSource + " - " + styleName);
                            continue;
                        }
                        /** add feature > layer + style **/
                        MapboxLayer mapboxLayer = new MapboxLayer();
                        String layerId = shpSource + "_" + styleName;
                        if(mapbox.existLayerId(layerId)){
                            layerId = layerId + "-" + (++i);
                        }
                        mapboxLayer.setId(layerId);
                        mapboxLayer.setSource(sourceName);
                        mapboxLayer.setSourceLayer(shpSource);
                        mapboxLayer.setFilter(parseFilter(feature.getVVTStyle()));
                        MapboxLayout layout = new MapboxLayout();
                        if(featureType.equals("Polygon")){
                            mapboxLayer.setType("fill");
                            if(style.getPolygonLayer() != null){
                                MapboxPaint paint = new MapboxPaint();
                                GssPolygonLayer gssPaint = style.getPolygonLayer().get(0);
                                if(gssPaint.getType().equals("PICTURE")){
                                    paint.setFillPattern(parsePicture(gssPaint.getPicture()));
                                }else{
                                    if(gssPaint.getColor() != null) paint.setFillColor(parseColor(gssPaint.getColor()));
                                }
                                mapboxLayer.setPaint(paint);
                            }
                        }else if(featureType.equals("Line")){
                            mapboxLayer.setType("line");
                            if(style.getLineLayer() != null){
                                MapboxPaint paint = new MapboxPaint();
                                GssLineLayer gssPaint = style.getLineLayer().get(0);
                                if(gssPaint.getType().equals("PICTURE")){
                                    paint.setFillPattern(parsePicture(gssPaint.getPicture()));
                                }else{
                                    if(gssPaint.getColor() != null) paint.setLineColor(parseColor(gssPaint.getColor()));
                                    if(gssPaint.getWidth() != null) paint.setLineWidth(gssPaint.getWidth());
                                    if(gssPaint.getDashItem() != null) paint.setLineDasharray(gssPaint.getDashItem().toArray(new Float[]{}));
                                }
                                mapboxLayer.setPaint(paint);
                            }
                        }else if(featureType.equals("Point")){
                            mapboxLayer.setType("symbol");
                            if(style.getPointLayer() != null){
                                GssPointLayer gssPaint = style.getPointLayer().get(0);
                                if(gssPaint.getType().equals("PICTURE")){
                                    layout.setIconImage(parsePicture(gssPaint.getPicture()));
                                    layout.setIconAllowOverlap(true);
                                    continue;
                                }else{

                                }
                            }
                        }
                        mapboxLayer.setLayout(layout);
                        mapbox.addLayers(mapboxLayer);
                    }else if(displayType.equals("Both") || displayType.equals("Label")){
                        // Label
                        String labelStyleName = feature.getLabelStyle();
                        if(labelColumn == null){
                            System.out.println("not find labelColumn!! - " + shpSource + " - " + labelStyleName);
                            continue;
                        }
                        GssStyle labelStyle = findGssStyle(labelStyleName,styles);
                        if(labelStyle == null){
                            System.out.println("not find labelStyle!! - " + shpSource + " - " + labelStyleName);
                            continue;
                        }
                        /** add feature > layer + label style **/
                        MapboxLayer mapboxLayer = new MapboxLayer();
                        String layerId = shpSource + "_" + labelStyleName;
                        if(mapbox.existLayerId(layerId)){
                            layerId = layerId + "-" + (++i);
                        }
                        mapboxLayer.setId(layerId);
                        mapboxLayer.setSource(sourceName);
                        mapboxLayer.setSourceLayer(shpSource);
                        mapboxLayer.setFilter(parseFilter(feature.getVVTStyle()));
                        mapboxLayer.setType("symbol");
                        MapboxLayout layout = new MapboxLayout();
                        layout.setTextField("{"+labelColumn.toLowerCase()+"}");
                        if(labelStyle.getFont() != null) layout.setTextFont(new String[]{labelStyle.getFont()});
                        if(labelStyle.getSize() != null) layout.setTextSize(labelStyle.getSize());
                        if(labelStyle.getOffsetX() != null && labelStyle.getOffsetY() != null)
                            layout.setTextOffset(new Float[]{labelStyle.getOffsetX(),labelStyle.getOffsetY()});
                        MapboxPaint paint = new MapboxPaint();
                        if(labelStyle.getColor() != null) paint.setTextColor(parseColor(labelStyle.getColor()));
                        mapboxLayer.setPaint(paint);
                        mapboxLayer.setLayout(layout);
                        mapbox.addLayers(mapboxLayer);
                    }
                }
            }
        }
        new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .writeValue(outputFile,mapbox);
    }

    private List<Object> parseFilter(List<GssVVTStyle> vvtStyles) {
        if(vvtStyles == null){
            return null;
        }
        List<Object> filters = new ArrayList<>();
        filters.add("any");
        for(GssVVTStyle vvtStyle : vvtStyles){
            Iterator<QName> keys = vvtStyle.getValueMap().keySet().iterator();
            while( keys.hasNext() ){
                QName key = keys.next();
                String[] values = ((String) vvtStyle.getValueMap().get(key)).split(",");
                for(String value : values){
                    value = value.trim();
                    String expr = "==";
                    if(value.matches("[=<>].+")){
                        expr = value.replaceAll("[^=<>]*","");
                        value = value.replaceAll("[=<>]*","");
                    }
                    filters.add(new String[]{expr, key.toString().toLowerCase(), value});
                }
            }
        }
        return filters;
    }

    private String parsePicture(String picture) {
        return picture.replaceAll("\\.[a-zA-Z]*","");
    }

    private String parseColor(String colorStr) {
        String[] colors = colorStr.split(",");
        String a = colors[0].trim();
        float opacity = Float.parseFloat(a) / 255f;
        String r = colors[1].trim();
        String g = colors[2].trim();
        String b = colors[3].trim();
        return "rgba("+r+", "+g+", "+b+", "+(opacity == 1 ? 1 : String.format("%.1f",opacity))+")";
    }


    public static void main(String... args) throws IOException, SAXException, ParserConfigurationException, JAXBException {
        String dataPath = "/Users/neutti/Dev/Projects/admin-view/data";
        File stylefile = new File(dataPath + "/gss_style/GSS_STYLE.xml");
        File layerfile = new File(dataPath + "/gss_style/GSS_GROUND_LARGE_SCALE_LAYER.xml");
        File savefile = new File(dataPath + "/g25k_style_generate.json");
        ConveterGssXml parse = new ConveterGssXml();
        parse.convertLayerJsonFile(stylefile,layerfile,savefile);
    }
}
