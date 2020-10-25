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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

@Service
public class ConverterGssXml {

    /**
     * 스타일 컨버터 Sample 사용법
     */
    public static void main(String... args) throws IOException, SAXException, ParserConfigurationException, JAXBException {
        // TODO : dataPath 경로변경
        String dataPath = "/Users/neutti/Dev/Projects/admin-view/data";
        //
        File stylefile = new File(dataPath + "/gss_style/GSS_STYLE.xml");
        File layerfile = new File(dataPath + "/gss_style/GSS_GROUND_LARGE_SCALE_LAYER.xml");
        File savefile = new File(dataPath + "/g25k_style_generate.json");
        FileWriter writer = new FileWriter(savefile);
        ConverterVO param = new ConverterVO();
        param.setVersion(8);
        param.setId("uzymq5sw3");
        param.setName("G25k_style");
        param.setSprite("http://211.172.246.71:11000/GSymbol/GSSSymbol");
        param.setGlyphs("http://211.172.246.71:11000/fonts/{fontstack}/{range}.pbf");
        param.setTiles(new String[]{"http://211.172.246.71:11000/maps/g25k/{z}/{x}/{y}.pbf"});
        ConverterGssXml parse = new ConverterGssXml();
        parse.convertLayerJson(stylefile,layerfile,writer,param);
    }


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

    public void convertLayerJson(File styleFile, File layerFile, Writer output, ConverterVO param) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        String sourceName = "tegola";
        MapboxRoot mapbox = new MapboxRoot();
        mapbox.setVersion(param.getVersion());
        mapbox.setId(param.getId());
        mapbox.setName(param.getName());
        mapbox.setSprite(param.getSprite());
        mapbox.setGlyphs(param.getGlyphs());
        MapboxSource source = new MapboxSource();
        source.setType("vector");
        source.setTiles(Arrays.asList(param.getTiles()));
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
                        GssStyle style = findGssStyle(styleName,styles);
                        if(style == null){
                            System.out.println("not find Style!! - " + shpSource + " - " + styleName);
                            continue;
                        }
                        /** add feature > layer + style **/
                        if(featureType.equals("Polygon")){
                            if(style.getPolygonLayer() != null){
                                for(GssPolygonLayer styleLayer : style.getPolygonLayer()){
                                    i = makeStyleLayer(featureType,styleLayer,shpSource,styleName,i,sourceName,feature.getVVTStyle(),mapbox);
                                }
                            }
                        }else if(featureType.equals("Line")){
                            if(style.getLineLayer() != null){
                                for(GssLineLayer styleLayer : style.getLineLayer()){
                                    i = makeStyleLayer(featureType,styleLayer,shpSource,styleName,i,sourceName,feature.getVVTStyle(),mapbox);
                                }
                            }
                        }else if(featureType.equals("Point")){
                            if(style.getPointLayer() != null){
                                for(GssPointLayer styleLayer : style.getPointLayer()){
                                    i = makeStyleLayer(featureType,styleLayer,shpSource,styleName,i,sourceName,feature.getVVTStyle(),mapbox);
                                }
                            }
                        }
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
                            layerId = layerId + "_" + (++i);
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
                .writeValue(output,mapbox);
    }

    private int makeStyleLayer(String featureType, Object styleLayer, String shpSource, String styleName, int i, String sourceName, List<GssVVTStyle> vvtStyle, MapboxRoot mapbox) {
        MapboxLayer mapboxLayer = new MapboxLayer();
        String layerId = shpSource + "_" + styleName;
        if(mapbox.existLayerId(layerId)){
            layerId = layerId + "_" + (++i);
        }
        mapboxLayer.setId(layerId);
        mapboxLayer.setSource(sourceName);
        mapboxLayer.setSourceLayer(shpSource);
        mapboxLayer.setFilter(parseFilter(vvtStyle));
        MapboxPaint paint = new MapboxPaint();
        MapboxLayout layout = new MapboxLayout();
        if(featureType.equals("Polygon")){
            mapboxLayer.setType("fill");
            GssPolygonLayer gssPaint = (GssPolygonLayer) styleLayer;
            if(gssPaint.getType().equals("PICTURE")){
                paint.setFillPattern(parsePicture(gssPaint.getPicture()));
            }else{
                if(gssPaint.getColor() != null) paint.setFillColor(parseColor(gssPaint.getColor()));
            }
        }else if(featureType.equals("Line")){
            mapboxLayer.setType("line");
            GssLineLayer gssPaint = (GssLineLayer) styleLayer;
            if(gssPaint.getType().equals("PICTURE")){
                paint.setLinePattern(parsePicture(gssPaint.getPicture()));
            }else{
                if(gssPaint.getColor() != null) paint.setLineColor(parseColor(gssPaint.getColor()));
                if(gssPaint.getWidth() != null) paint.setLineWidth(gssPaint.getWidth());
                if(gssPaint.getDashItem() != null) paint.setLineDasharray(gssPaint.getDashItem().toArray(new Integer[]{}));
                if(gssPaint.getSpace() != null) paint.setLineGapWidth(gssPaint.getSpace());
            }
        }else if(featureType.equals("Point")) {
            mapboxLayer.setType("symbol");
            GssPointLayer gssPaint = (GssPointLayer) styleLayer;
            if(gssPaint.getType().equals("PICTURE")){
                layout.setIconImage(parsePicture(gssPaint.getPicture()));
                layout.setIconAllowOverlap(true);
            }else{

            }
        }
        mapboxLayer.setPaint(paint);
        mapboxLayer.setLayout(layout);
        mapbox.addLayers(mapboxLayer);
        return i;
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
                String valueStr = (String) vvtStyle.getValueMap().get(key);
                String value = valueStr;
                String expr = "==";
                if(value.matches("[=<>].+")){
                    expr = value.replaceAll("[^=<>]*","");
                    value = value.replaceAll("[=<>]*","");
                }
                if(expr.equals("<>")){
                    expr = "!=";
                }
                filters.add(new String[]{expr, key.toString().toLowerCase(), value});
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



}
