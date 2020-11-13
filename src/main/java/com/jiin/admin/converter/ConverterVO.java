package com.jiin.admin.converter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jiin.admin.converter.gss.*;
import com.jiin.admin.converter.mapbox.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

public class ConverterVO {
    private Integer version;
    private String id;
    private String name;
    private String sprite;
    private String glyphs;
    private String[] tiles;
    private String maputnikRenderer;
    private String sourceName;
    private String font;
    private Scale scale;
    private String scaleStr;
    enum Scale {
        g25k("25K"),
        g50k("50K"),
        g100k("100K"),
        a250k("250K"),
        a500k("500K"),
        S1M("1M"),
        S2M("2M"),
        KR1("KR1"),
        KR2("KR2"),
        KR3("KR3"),
        KR4("KR4"),
        KR5("KR5"),
        ;
        private final String value;
        Scale(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
        public static Scale find(String value) {
            for(Scale scale : Scale.values()){
                if(scale.getValue().equals(value)){
                    return scale;
                }
            }
            return null;
        }
    }


    public String getScaleStr() {
        return scaleStr;
    }

    public void setScaleStr(String scaleStr) {
        this.scaleStr = scaleStr;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSprite() {
        return sprite;
    }

    public void setSprite(String sprite) {
        this.sprite = sprite;
    }

    public String getGlyphs() {
        return glyphs;
    }

    public void setGlyphs(String glyphs) {
        this.glyphs = glyphs;
    }

    public String[] getTiles() {
        return tiles;
    }

    public void setTiles(String[] tiles) {
        this.tiles = tiles;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getMaputnikRenderer() {
        return maputnikRenderer;
    }

    public void setMaputnikRenderer(String maputnikRenderer) {
        this.maputnikRenderer = maputnikRenderer;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
