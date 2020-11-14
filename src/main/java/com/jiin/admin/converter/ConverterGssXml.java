package com.jiin.admin.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jiin.admin.converter.gss.*;
import com.jiin.admin.converter.mapbox.*;
import org.springframework.beans.BeanUtils;
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
        ConverterVO param = new ConverterVO();
        param.setVersion(8);
        param.setId("uzymq5sw3");
        param.setName("g25k_style");
        param.setMaputnikRenderer("mbgljs");
        param.setScale(ConverterVO.Scale.g25k);
        param.setFont("Gosanja");
        param.setSourceName(param.getScale().name());
        param.setSprite("http://192.168.0.11/GSymbol/GSSSymbol");
        param.setGlyphs("http://192.168.0.11/fonts/{fontstack}/{range}.pbf");
        param.setTiles(new String[]{"http://192.168.0.11/maps/"+param.getScale().name()+"/{z}/{x}/{y}.pbf"});
        ConverterGssXml parse = new ConverterGssXml();
        //
        File savefile = new File(dataPath + "/"+param.getScale().name()+"_style_generate.json");
        FileWriter writer = new FileWriter(savefile);
        //
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
            object = JAXBContext.newInstance(GssMapLayer.class).createUnmarshaller().unmarshal(target);
            List<String> orders = new LinkedList<>();
            NodeList lst = root.getChildNodes();
            for(int i = 0; i < lst.getLength(); ++i) {
                Node node = lst.item(i);
                if (node.getNodeType() == 1) {
                    Element el = (Element)node;
                    if(el.getNodeName().equals("Group")){
                        NodeList subs = node.getChildNodes();
                        for(int j = 0; j < subs.getLength(); ++j) {
                            Node sub = subs.item(j);
                            if (sub.getNodeType() == 1) {
                                Element elsub = (Element)sub;
                                String category = elsub.getAttribute("Category");
                                String name = elsub.getAttribute("Name");
                                orders.add(category + "-" + name);
                            }
                        }
                    }else{
                        String category = el.getAttribute("Category");
                        String name = el.getAttribute("Name");
                        orders.add(category + "-" + name);
                    }
                }
            }
            List<GssGroup> groups = ((GssMapLayer)object).getGroup();
            List<GssLayer> layers = ((GssMapLayer)object).getLayer();
            if(layers == null){
                layers = new LinkedList<>();
            }
            if(groups != null){
                for(GssGroup group : groups){
                    layers.addAll(group.getLayer());
                }
                List<GssLayer> regenLayers = new LinkedList<>();
                for(String key : orders){
                    for(GssLayer layer : layers){
                        String compareKey = layer.getCategory() + "-" + layer.getName();
                        if(key.equals(compareKey)){
                            regenLayers.add(layer);
                            break;
                        }
                    }
                }
                ((GssMapLayer)object).setLayer(regenLayers);
            }
        }else if(root.getNodeName().equals("MapStyle")){
            object = JAXBContext.newInstance(GssMapStyle.class).createUnmarshaller().unmarshal(target);
        }else{
            System.out.println("Opps!!");
            return null;
        }
        return (GssContainer) object;
    }

    private GssStyle findGssStyle(String styleName, GssContainer object) {
        List<GssStyle> styles = ((GssMapStyle) object).getStyle();
        for(GssStyle style : styles){
            if(style.getName().equals(styleName)){
                return style;
            }
        }
        return null;
    }

    public void convertLayerJson(File styleFile, File layerFile, Writer output, ConverterVO param) throws ParserConfigurationException, JAXBException, IOException, SAXException {
        if(param.getScaleStr() != null){
            param.setScale(ConverterVO.Scale.find(param.getScaleStr()));
        }
        String sourceName = param.getSourceName();
        String font = param.getFont();
        MapboxRoot mapbox = new MapboxRoot();
        mapbox.setVersion(param.getVersion());
        mapbox.setId(param.getId());
        Map meta = new HashMap();
        if(param.getMaputnikRenderer() != null){
            meta.put("maputnik:renderer",param.getMaputnikRenderer());
        }
        if(!meta.isEmpty()){
            mapbox.setMetadata(meta);
        }
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
        GssMapLayer result = (GssMapLayer) parseGssXml(layerFile);
        List<GssLayer> layers = result.getLayer();
        for(GssLayer layer : layers) {
            makeLayer("Polygon", layer, sourceName, font, styles, param, mapbox);
            makeLayer("Line", layer, sourceName, font, styles, param, mapbox);
            makeLayer("Point", layer, sourceName, font, styles, param, mapbox);
        }
        for(GssLayer layer : layers) {
            makeLayer("Label", layer, sourceName, font, styles, param, mapbox);
        }
        mapbox.cleanLayerId();
        new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .writeValue(output,mapbox);
    }
    private void makeLayer(String type, GssLayer layer, String sourceName, String font, GssContainer styles, ConverterVO param, MapboxRoot mapbox) throws JsonProcessingException {
        String mapScale = layer.getMap();
        if(!mapScale.contains(param.getScale().getValue())){
            // 축적불일치 / Map="25K,50K,100K"
            return;
        }
        String shpSource = layer.getSHPSource().toLowerCase();
        String featureType = layer.getGeometryType();
        String labelColumn = layer.getLabelColumn();
        String angleColumn = layer.getAngleColumn();
        if(angleColumn != null){
            angleColumn = angleColumn.toLowerCase();
        }
        String displayType = layer.getDisplayType();
        List<GssFeature> features = layer.getFeature();
        if(features != null){
            for(GssFeature feature : features) {
                if (displayType.equals("Both") || displayType.equals("Geometry")) {
                    // Geometry
                    String styleName = feature.getGeometryStyle();
                    GssStyle style = findGssStyle(styleName, styles);
                    if (style == null) {
                        System.out.println("not find Style!! - " + shpSource + " - " + styleName);
                        continue;
                    }
                    /** add feature > layer + style **/
                    if (featureType.equals("Polygon")) {
                        if (style.getPolygonLayer() != null) {
                            if(type.equals("Polygon")){
                                for (GssPolygonLayer styleLayer : style.getPolygonLayer()) {
                                    if(styleLayer.getTextureFill() == null || styleLayer.getTextureFill().equals(true)){
                                        makeStyleLayer(featureType, layer, feature, style, styleLayer, shpSource, styleName, sourceName, feature.getVVTStyle(), angleColumn, mapbox);
                                    }
                                    //line
                                    if (styleLayer.getLineLayer() != null) {
                                        for (GssLineLayer linestyle : styleLayer.getLineLayer()) {
                                            if(linestyle.getTextureLine() != null && linestyle.getTextureLine().equals(false)){
                                                //point
                                                makeStyleLayer("Point", layer, feature, style, linestyle, shpSource, styleName, sourceName, feature.getVVTStyle(), angleColumn, mapbox);
                                            }else{
                                                List<GssLineLayer> lineStyles = lineStyleByType(linestyle, featureType, shpSource);
                                                for (GssLineLayer _linestyle : lineStyles) {
                                                    makeStyleLayer("Line", layer, feature, style, _linestyle, shpSource, styleName, sourceName, feature.getVVTStyle(), angleColumn, mapbox);
                                                }
                                            }
                                        }
                                    }
                                    //point
                                    if(styleLayer.getTextureFill() != null && styleLayer.getTextureFill().equals(false)){
                                        makeStyleLayer("Point", layer, feature, style, styleLayer, shpSource, styleName, sourceName, feature.getVVTStyle(), angleColumn, mapbox);
                                    }
                                }
                            }
                        }
                    }else if (featureType.equals("Line")) {
                        if(type.equals("Line")){
                            if (style.getLineLayer() != null) {
                                for (GssLineLayer linestyle : style.getLineLayer()) {
                                    if(linestyle.getTextureLine() != null && linestyle.getTextureLine().equals(false)){
                                        //point
                                        makeStyleLayer("Point", layer, feature, style, linestyle, shpSource, styleName, sourceName, feature.getVVTStyle(), angleColumn, mapbox);
                                    }else{
                                        List<GssLineLayer> lineStyles = lineStyleByType(linestyle, featureType, shpSource);
                                        for (GssLineLayer _linestyle : lineStyles) {
                                            makeStyleLayer(featureType, layer, feature, style, _linestyle, shpSource, styleName, sourceName, feature.getVVTStyle(), angleColumn, mapbox);
                                        }
                                    }
                                }
                            }
                        }
                    }else if (featureType.equals("Point")) {
                        if(type.equals("Point")){
                            if(Arrays.asList(new String[]{"AL015P02","AL015P07","AL100P01","AL200P01","GB065P01"}).contains(style.getName())){
                                // 파괸된 건물, 위치불분명 철도역, 오두막/막사, 폐허, 수상비행기지 > 심볼로 대채하여 표현 / Dragonteeth 심볼
                                GssPointLayer gssPaint = new GssPointLayer("PICTURE");
                                gssPaint.setPicture("Dragonteeth");
                                makeStyleLayer(featureType, layer, feature, style, gssPaint, shpSource, styleName, sourceName, feature.getVVTStyle(), angleColumn, mapbox);
                            }else{
                                if (style.getPointLayer() != null) {
                                    for (GssPointLayer styleLayer : style.getPointLayer()) {
                                        makeStyleLayer(featureType, layer, feature, style, styleLayer, shpSource, styleName, sourceName, feature.getVVTStyle(), angleColumn, mapbox);
                                    }
                                }
                            }
                        }
                    }
                }
                if(type.equals("Label")){
                    if(displayType.equals("Both") || displayType.equals("Label")){
                        // Label
                        String labelStyleName = feature.getLabelStyle();
                        if (labelColumn == null) {
                            System.out.println("not find labelColumn!! - " + shpSource + " - " + labelStyleName);
                            continue;
                        }
                        GssStyle labelStyle = findGssStyle(labelStyleName,styles);
                        if (labelStyle == null) {
                            System.out.println("not find labelStyle!! - " + shpSource + " - " + labelStyleName);
                            continue;
                        }
                        /** add feature > layer + label style **/
                        MapboxLayer mapboxLayer = new MapboxLayer();
                        if(shpSource.equals("해양지명")){
                            shpSource = "geoname";
                        }
                        String layerId = (shpSource + "_" + labelStyleName).toUpperCase();
                        layerId = layerId + "_" + getLayerIdIndex(layerId);
                        mapboxLayer.setId(layerId);
                        mapboxLayer.setSource(sourceName);
                        mapboxLayer.setSourceLayer(shpSource);
                        mapboxLayer.setFilter(parseFilter(feature.getVVTStyle(),shpSource));
                        mapboxLayer.setType("symbol");
                        MapboxLayout layout = new MapboxLayout();
                        layout.setTextAllowOverlap(true);
                        if(shpSource.equalsIgnoreCase("geoname") || shpSource.equalsIgnoreCase("roadname")){
                            layout.setTextRotationAlignment("map");
                        }
                        String labelColumnName;
                        if (labelColumn.equalsIgnoreCase("표기한글")) {
                            labelColumnName = "textstring";
                        } else if (labelColumn.equalsIgnoreCase("도로번호")) {
                            labelColumnName = "road_num";
                        } else if (labelColumn.equalsIgnoreCase("해양지")) {
                            labelColumnName = "ocean_name";
                        } else {
                            labelColumnName = labelColumn.toLowerCase();
                        }
                        layout.setTextField("{" + labelColumnName + "}");
                        if (labelStyle.getFont() != null) layout.setTextFont(new String[]{font});
                        if (labelStyle.getSize() != null) layout.setTextSize(labelStyle.getSize());
                        if (labelStyle.getOffsetX() != null
                                && labelStyle.getOffsetY() != null
                                && (
                                        !Objects.equals(labelStyle.getOffsetX(),0f)
                                        ||
                                        !Objects.equals(labelStyle.getOffsetY(),0f)
                                )) {
                            String x = String.format("%.1f",labelStyle.getOffsetX() / 2.5f);
                            String y = String.format("%.1f",labelStyle.getOffsetY() / 2.5f);
                            labelStyle.setOffsetX(Float.parseFloat(x));
                            labelStyle.setOffsetY(Float.parseFloat(y));
                            //
                            if(shpSource.equalsIgnoreCase("NAVL")){ //항법무선시설 라벨
                                labelStyle.setOffsetX(95f);
                                labelStyle.setOffsetY(-1f);
                            }else if(shpSource.equalsIgnoreCase("PCA030")) { //표고점 라벨
                                labelStyle.setOffsetX(2.5f);
                            }
                            layout.setTextOffset(new Float[]{labelStyle.getOffsetX(),labelStyle.getOffsetY()});
                        }
                        if(labelStyle.getPicture() != null){
                            layout.setTextJustify("left");
                            layout.setTextRotationAlignment("map");
                            layout.setTextKeepUpright(false);
                            layout.setIconImage(parsePicture(labelStyle.getPicture()));
                            layout.setIconAllowOverlap(true);
                            layout.setIconRotationAlignment("map");
                            layout.setIconKeepUpright(false);
                            layout.setIconTextFit("none");
                            if (labelStyle.getImageOffsetX() != null && labelStyle.getImageOffsetY() != null
                                    && (!Objects.equals(labelStyle.getImageOffsetX(),0f) || !Objects.equals(labelStyle.getImageOffsetY(),0f))) {
                                if(shpSource.equalsIgnoreCase("PCA030")) { //표고점 라벨
                                    labelStyle.setImageOffsetX(20f);
                                }
                                layout.setIconOffset(new Float[]{labelStyle.getImageOffsetX(),labelStyle.getImageOffsetY()});
                            }
                            if(labelStyle.getImageAlign() != null){
                                //역변 >> 0 - 왼쪽 상단, 1 - 중앙 상단, 2 - 오른쪽 상단, 3 - 왼쪽 중앙, 4 - 정가운데, 5 - 오른쪽 중앙, 6 - 왼쪽 하단, 7 - 중앙 하단, 8 - 오른쪽 하단
                                if(labelStyle.getImageAlign() == 0) layout.setIconAnchor("bottom-right");
                                if(labelStyle.getImageAlign() == 1) layout.setIconAnchor("bottom");
                                if(labelStyle.getImageAlign() == 2) layout.setIconAnchor("bottom-left");
                                if(labelStyle.getImageAlign() == 3) layout.setIconAnchor("right");
                                if(labelStyle.getImageAlign() == 4) layout.setIconAnchor("center");
                                if(labelStyle.getImageAlign() == 5) layout.setIconAnchor("left");
                                if(labelStyle.getImageAlign() == 6) layout.setIconAnchor("top-right");
                                if(labelStyle.getImageAlign() == 7) layout.setIconAnchor("top");
                                if(labelStyle.getImageAlign() == 8) layout.setIconAnchor("top-left");
                            }
                        }
                        if(labelStyle.getAlign() != null){
                            //역변 >> 0 - 왼쪽 상단, 1 - 중앙 상단, 2 - 오른쪽 상단, 3 - 왼쪽 중앙, 4 - 정가운데, 5 - 오른쪽 중앙, 6 - 왼쪽 하단, 7 - 중앙 하단, 8 - 오른쪽 하단
                            if(labelStyle.getAlign() == 0) layout.setTextAnchor("bottom-right");
                            if(labelStyle.getAlign() == 1) layout.setTextAnchor("bottom");
                            if(labelStyle.getAlign() == 2) layout.setTextAnchor("bottom-left");
                            if(labelStyle.getAlign() == 3) layout.setTextAnchor("right");
                            if(labelStyle.getAlign() == 4) layout.setTextAnchor("center");
                            if(labelStyle.getAlign() == 5) layout.setTextAnchor("left");
                            if(labelStyle.getAlign() == 6) layout.setTextAnchor("top-right");
                            if(labelStyle.getAlign() == 7) layout.setTextAnchor("top");
                            if(labelStyle.getAlign() == 8) layout.setTextAnchor("top-left");
                        }
                        MapboxPaint paint = new MapboxPaint();
                        if (labelStyle.getColor() != null) paint.setTextColor(parseColor(labelStyle.getColor()));
                        if(labelStyle.getOutline() != null && labelStyle.getOutline()){
                            paint.setTextHaloWidth(1);
                            paint.setTextHaloColor(parseColor(labelStyle.getOutlineColor()));
                        }
                        mapboxLayer.setPaint(paint);
                        mapboxLayer.setLayout(layout);
                        mapbox.addLayers(mapboxLayer);
                    }
                }
            }
        }
    }

    private List<GssLineLayer> lineStyleByType(GssLineLayer linestyle, String featureType, String shpSource) {
        List<GssLineLayer> result = new ArrayList<>();
        //
        float convert = 1;
        if(
                shpSource.equalsIgnoreCase("LEA020") // 산울타리
                ||
                shpSource.equalsIgnoreCase("SUAS") // 특수사용공역
        ){
            convert = -1;
        }
        //
        //
        if(linestyle.getType().equals("SIMPLE")){
            // nothing
            result.add(linestyle);
        }else if(linestyle.getType().equals("DASH")){
            // nothing
            // width 0 ignore
            if(linestyle.getWidth() != 0){
                result.add(linestyle);
            }
        }else if(linestyle.getType().equals("PICTURE")){
            // nothing
            result.add(linestyle);
        }else if(linestyle.getType().equals("VERTICAL") && linestyle.getVerticalType() == 0){
            // Both
            Integer dashWidth = linestyle.getWidth();
            Integer left = linestyle.getLeftLength();
            Integer right = linestyle.getRightLength();
            Integer interval = linestyle.getInterval();
            int width = left + right;
            List<Float> dash = new ArrayList<>();
            dash.add((dashWidth / (dashWidth == 1 ? 2f : 1f)));
            dash.add(interval - (dashWidth / (dashWidth == 1 ? 2f : 1f)));
            linestyle.setDashItem(dash);
            linestyle.setWidth(width);
            linestyle.setOffset((float) ((left - right) / 2) * convert);
            result.add(linestyle);
        }else if(linestyle.getType().equals("VERTICAL") && linestyle.getVerticalType() == 1){
            // Left (or Inside)
            Integer dashWidth = linestyle.getWidth();
            Integer left = linestyle.getLeftLength();
            Integer interval = linestyle.getInterval();
            int width = left;
            List<Float> dash = new ArrayList<>();
            dash.add((dashWidth / (dashWidth == 1 ? 2f : 1f)));
            dash.add(interval - (dashWidth / (dashWidth == 1 ? 2f : 1f)));
            linestyle.setDashItem(dash);
            linestyle.setWidth(width);
            linestyle.setOffset((float) +(width / 2) * convert);
            result.add(linestyle);
        }else if(linestyle.getType().equals("VERTICAL") && linestyle.getVerticalType() == 2){
            // Right (or Outside)
            Integer dashWidth = linestyle.getWidth();
            Integer right = linestyle.getRightLength();
            Integer interval = linestyle.getInterval();
            int width = right;
            List<Float> dash = new ArrayList<>();
            dash.add((dashWidth / (dashWidth == 1 ? 2f : 1f)));
            dash.add(interval - (dashWidth / (dashWidth == 1 ? 2f : 1f)));
            linestyle.setDashItem(dash);
            linestyle.setWidth(width);
            linestyle.setOffset((float) -(width / 2) * convert);
            result.add(linestyle);
        }else if(linestyle.getType().equals("VERTICAL") && linestyle.getVerticalType() == 3){
            // Both Circle
            // left
            Integer dashWidth = linestyle.getWidth();
            Integer left = linestyle.getLeftLength();
            Integer interval = linestyle.getInterval();
            List<Float> dash = new ArrayList<>();
            dash.add(dashWidth / 2f);
            dash.add(interval - (dashWidth / 2f));
            linestyle.setDashItem(dash);
            linestyle.setWidth(dashWidth);
            linestyle.setOffset((float) +left * convert);
            result.add(linestyle);
            // right
            GssLineLayer addLinestyle = new GssLineLayer();
            BeanUtils.copyProperties(linestyle, addLinestyle);
            Integer right = addLinestyle.getRightLength();
            List<Float> addDash = new ArrayList<>();
            addDash.add(dashWidth / 2f);
            addDash.add(interval - (dashWidth / 2f));
            addLinestyle.setDashItem(dash);
            addLinestyle.setWidth(dashWidth);
            addLinestyle.setOffset((float) -right * convert);
            result.add(addLinestyle);
        }else if(linestyle.getType().equals("VERTICAL") && linestyle.getVerticalType() == 4){
            // Left Circle
            Integer dashWidth = linestyle.getWidth();
            Integer left = linestyle.getLeftLength();
            Integer interval = linestyle.getInterval();
            List<Float> dash = new ArrayList<>();
            dash.add(dashWidth / 2f);
            dash.add(interval - (dashWidth / 2f));
            linestyle.setDashItem(dash);
            linestyle.setWidth(dashWidth);
            linestyle.setOffset((float) +left * convert);
            result.add(linestyle);
        }else if(linestyle.getType().equals("VERTICAL") && linestyle.getVerticalType() == 5){
            // Right Circle
            Integer dashWidth = linestyle.getWidth();
            Integer right = linestyle.getRightLength();
            Integer interval = linestyle.getInterval();
            List<Float> dash = new ArrayList<>();
            dash.add(dashWidth / 2f);
            dash.add(interval - (dashWidth / 2f));
            linestyle.setDashItem(dash);
            linestyle.setWidth(dashWidth);
            linestyle.setOffset((float) -right * convert);
            result.add(linestyle);
        }else if(linestyle.getType().equals("DOUBLELINE") && linestyle.getSubType() == 0){
            // Simple
            // nothing
            result.add(linestyle);
        }else if(linestyle.getType().equals("DOUBLELINE") && linestyle.getSubType() == 1){
            // End Round
            // dont exist - ignore
        }else if(linestyle.getType().equals("DOUBLELINE") && linestyle.getSubType() == 2){
            // Bridge
            // DRAW by APP
            result.add(linestyle);
        }else if(linestyle.getType().equals("DOUBLELINE") && linestyle.getSubType() == 3){
            // Left Only
            Integer space = linestyle.getSpace();
            linestyle.setOffset(+(Float.valueOf(space) / 2f) * convert);
            linestyle.setSpace(0);
            result.add(linestyle);
        }else if(linestyle.getType().equals("DOUBLELINE") && linestyle.getSubType() == 4){
            // Right Only
            Integer space = linestyle.getSpace();
            linestyle.setOffset(-(Float.valueOf(space) / 2f) * convert);
            linestyle.setSpace(0);
            result.add(linestyle);
        }else if(linestyle.getType().equals("DOUBLELINE") && linestyle.getSubType() == 5){
            // Tunnel
            // DRAW by APP
            result.add(linestyle);
        }else if(linestyle.getType().equals("DOUBLELINE") && linestyle.getSubType() == 6){
            // PipeLine
            // DRAW by APP
            result.add(linestyle);
        }
        return result;
    }

    private void makeStyleLayer(String featureType, GssLayer layer, GssFeature feature, GssStyle style, Object styleLayer, String shpSource, String styleName, String sourceName, List<GssVVTStyle> vvtStyle, String angleColumn,  MapboxRoot mapbox) throws JsonProcessingException {
        MapboxLayer mapboxLayer = new MapboxLayer();
        mapboxLayer.setSource(sourceName);
        mapboxLayer.setSourceLayer(shpSource);
        mapboxLayer.setFilter(parseFilter(vvtStyle,shpSource));
        MapboxPaint paint = new MapboxPaint();
        MapboxLayout layout = new MapboxLayout();
        MapboxMetadata metadata = new MapboxMetadata();
        metadata.setComment(layer.getCategory() + "::" + layer.getName() + "::" + feature.getName() + "::" + feature.getDescription());
        mapboxLayer.setMetadata(metadata);
        if(featureType.equals("Polygon")){
            mapboxLayer.setType("fill");
            GssPolygonLayer gssPaint = (GssPolygonLayer) styleLayer;
            if(gssPaint.getType().equals("PICTURE")){
                paint.setFillPattern(parsePicture(gssPaint.getPicture()));
                if(angleColumn != null){
                    layout.setIconRotate(new String[]{"get", angleColumn});
                    layout.setIconRotationAlignment("map");
                }
            }else{
                if(gssPaint.getType().equals("SIMPLE") && gssPaint.getColor().equals("255, 255, 255, 255")){
                    return; // color white ignore
                }
                if(gssPaint.getColor() != null) paint.setFillColor(parseColor(gssPaint.getColor()));
            }
        }else if(featureType.equals("Line")){
            mapboxLayer.setType("line");
            GssLineLayer gssPaint = (GssLineLayer) styleLayer;
            if(gssPaint.getType().equals("PICTURE")){
                paint.setLinePattern(parsePicture(gssPaint.getPicture()));
                paint.setLineWidth(gssPaint.getWidth());
                paint.setLineTranslateAnchor("viewport");
                if(angleColumn != null){
                    layout.setIconRotate(new String[]{"get", angleColumn});
                    layout.setIconRotationAlignment("map");
                }
            }else{
                if(gssPaint.getType().equals("SIMPLE") && gssPaint.getWidth() == 0){
                    return; // width 0 ignore
                }
                if(gssPaint.getColor() != null) paint.setLineColor(parseColor(gssPaint.getColor()));
                if(gssPaint.getWidth() != null) paint.setLineWidth(gssPaint.getWidth());
                if(gssPaint.getOffset() != null) paint.setLineOffset(gssPaint.getOffset());
                if(gssPaint.getDashItem() != null) {
                    // width, startPos 에 따라 가변
                    paint.setLineDasharray(gssPaint.parseDashItem().toArray(new Float[]{}));
                }
                if(gssPaint.getSpace() != null) paint.setLineGapWidth(gssPaint.getSpace());
                if (gssPaint.getJoinType() != null) {   // TODO 이후 공통으로 이동 해야함
                    switch (gssPaint.getJoinType()) {
                        case "1":
                            break;
                        case "2":
                            layout.setLineJoin("round");
                            break;
                        case "3":
                            break;
                    }
                }
            }
        }else if(featureType.equals("Point")) {
            mapboxLayer.setType("symbol");
            if(styleLayer instanceof GssPointLayer){
                GssPointLayer gssPaint = (GssPointLayer) styleLayer;
                if(gssPaint.getType().equals("SIMPLE")){
                    if(gssPaint.getShape() != null){
                        if(gssPaint.getShape().equals("0")){
                            mapboxLayer.setType("circle");
                            paint.setCircleColor(parseColor(gssPaint.getColor()));
                            paint.setCircleRadius(Float.parseFloat(gssPaint.getSize()) / 2f);
                        }else if(gssPaint.getShape().equals("1")){
                            layout.setIconImage("Rectangle");
                            layout.setIconAllowOverlap(true);
                            layout.setIconRotationAlignment("map");
                            String size = String.format("%.2f",Float.parseFloat(gssPaint.getSize()) * 0.0625f);
                            if(size.equals("0.38")){
                                size = "0.3"; // PAH050 등 제외 / 9건
                            }
                            layout.setIconSize(Float.parseFloat(size));
                        }
                    }
                }else if(gssPaint.getType().equals("PICTURE")){
                    layout.setIconImage(parsePicture(gssPaint.getPicture()));
                    layout.setIconAllowOverlap(true);
                    layout.setIconRotationAlignment("map");
                    if (style.getOffsetX() != null && style.getOffsetY() != null
                        &&
                        (!Objects.equals(style.getOffsetX(),0f) || !Objects.equals(style.getOffsetY(),0f)
                    )) {
                        layout.setIconOffset(new Float[]{style.getOffsetX(),style.getOffsetY()});
                    }
                    if(angleColumn != null){
                        layout.setIconRotate(new String[]{"get", angleColumn});
                        layout.setIconRotationAlignment("map");
                    }
                }
            } else if(styleLayer instanceof GssPolygonLayer){
                GssPolygonLayer gssPaint = (GssPolygonLayer) styleLayer;
                if(gssPaint.getType().equals("PICTURE")){
                    layout.setIconImage(parsePicture(gssPaint.getPicture()));
                    layout.setIconAllowOverlap(true);
                    if(angleColumn != null){
                        layout.setIconRotate(new String[]{"get", angleColumn});
                        layout.setIconRotationAlignment("map");
                    }
                }
            } else if(styleLayer instanceof GssLineLayer){
                GssLineLayer gssPaint = (GssLineLayer) styleLayer;
                if(gssPaint.getType().equals("PICTURE")){
                    if(gssPaint.getPicture() == null){
                        return; // picture null ignore
                    }
                    layout.setIconAllowOverlap(true);
                    layout.setIconImage(parsePicture(gssPaint.getPicture()));
                    layout.setIconAllowOverlap(true);
                    layout.setSymbolPlacement("line");
                    layout.setIconAnchor("center");
                    layout.setSymbolSpacing(gssPaint.getInterval());
                    layout.setIconIgnorePlacement(true);
                    layout.setIconOptional(true);
                    if(angleColumn != null){
                        layout.setIconRotate(new String[]{"get", angleColumn});
                        layout.setIconRotationAlignment("map");
                    }
                }
            }
        }
        String json = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(paint);
        if(!json.equals("{}")){
            mapboxLayer.setPaint(paint);
        }
        // check layer id
        String layerId = (shpSource + "_" + styleName).toUpperCase();
        layerId = layerId + "_" + getLayerIdIndex(layerId);
        mapboxLayer.setId(layerId);
        mapboxLayer.setLayout(layout);
        mapbox.addLayers(mapboxLayer);
    }
    Map<String, Integer> LAYER_ID_INDEX = new HashMap();
    private String getLayerIdIndex(String layerId) {
        if(!LAYER_ID_INDEX.containsKey(layerId)){
            LAYER_ID_INDEX.put(layerId,0);
        }
        int index = LAYER_ID_INDEX.get(layerId) + 1;
        LAYER_ID_INDEX.put(layerId,index);
        return String.format("%02d", index);
    }

    private List<Object> parseFilter(List<GssVVTStyle> vvtStyles, String shpSource) {
        if (vvtStyles == null) {
            return null;
        }

        List<Object> filters = new ArrayList<>();
        filters.add("all");
        for (GssVVTStyle vvtStyle : vvtStyles) {
            Iterator<QName> keys = vvtStyle.getValueMap().keySet().iterator();
            while ( keys.hasNext() ) {
                QName key = keys.next();
                String valueStr = (String) vvtStyle.getValueMap().get(key);
                String keyStr = key.toString().toLowerCase();
                if (keyStr.equalsIgnoreCase("tlm분류")) {
                    keyStr = "tlmkind";
                } else if (keyStr.equalsIgnoreCase("종류")) {
                    keyStr = "tlmkind";
                } else if (keyStr.equalsIgnoreCase("도로등급")) {
                    keyStr = "road_grade";
                }
                String rawvalue = valueStr;
                String[] rawvalues = rawvalue.split(",");
                boolean isGroup = false;
                for (int i = 0; i < rawvalues.length; i++) {
                    if(i > 0 && rawvalues[i].matches("[=<>].+")){
                        isGroup = true;
                    }
                }
                //
                if(isGroup == false){
                    rawvalues = new String[]{rawvalue};
                }
                for (String value : rawvalues) {
                    List<Object> filter = new ArrayList<>();
                    String expr = "==";
                    if (value.matches("[=<>].+")) {
                        expr = value.replaceAll("[^=<>]*","");
                        value = value.replaceAll("[=<>]*","");
                    }
                    if (value.contains(",")) {
                        if (expr.equalsIgnoreCase("<>")) {
                            expr = "!in";
                        } else {
                            expr = "in";
                        }
                    }
                    if (expr.equals("<>")) {
                        expr = "!=";
                    }
                    filter.add(expr);
                    filter.add(keyStr);
                    if (value.contains(",")) {
                        String[] values = value.split(",");
                        for (String v : values) {
                            v = v.trim();
                            if (!shpSource.toUpperCase().startsWith("NAVL") && v.matches("^[0-9]+$")) {
                                filter.add(Integer.parseInt(v));
                            }else if (!shpSource.toUpperCase().startsWith("NAVL") && v.matches("^[0-9.]+$")) {
                                filter.add(Float.parseFloat(v));
                            } else {
                                filter.add(v);
                            }
                        }
                    } else {
                        value = value.trim();
                        if (!shpSource.toUpperCase().startsWith("NAVL") && value.matches("^[0-9]+$")) {
                            filter.add(Integer.parseInt(value));
                        } else if (!shpSource.toUpperCase().startsWith("NAVL") && value.matches("^[0-9.]+$")) {
                            filter.add(Float.parseFloat(value));
                        } else {
                            filter.add(value);
                        }
                    }
                    filters.add(filter);
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



}
