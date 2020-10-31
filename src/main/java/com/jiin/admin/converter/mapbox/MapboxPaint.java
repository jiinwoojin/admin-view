package com.jiin.admin.converter.mapbox;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MapboxPaint {
    public MapboxPaint() {

    }

    public MapboxPaint(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    @JsonProperty(value = "background-color")
    private String backgroundColor;
    @JsonProperty(value = "width")
    private Integer width;
    @JsonProperty(value = "fill-color")
    private String fillColor;
    @JsonProperty(value = "fill-pattern")
    private String fillPattern;
    @JsonProperty(value = "text-color")
    private String textColor;
    @JsonProperty(value = "line-pattern")
    private String linePattern;
    @JsonProperty(value = "line-color")
    private String lineColor;
    @JsonProperty(value = "line-width")
    private Integer lineWidth;
    @JsonProperty(value = "line-dasharray")
    private Float[] lineDasharray;
    @JsonProperty(value = "line-gap-width")
    private Integer lineGapWidth;
    @JsonProperty(value = "line-offset")
    private Integer lineOffset;



    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public String getFillPattern() {
        return fillPattern;
    }

    public void setFillPattern(String fillPattern) {
        this.fillPattern = fillPattern;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getLineColor() {
        return lineColor;
    }

    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Float[] getLineDasharray() {
        return lineDasharray;
    }

    public void setLineDasharray(Float[] lineDasharray) {
        this.lineDasharray = lineDasharray;
    }

    public Integer getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(Integer lineWidth) {
        this.lineWidth = lineWidth;
    }

    public String getLinePattern() {
        return linePattern;
    }

    public void setLinePattern(String linePattern) {
        this.linePattern = linePattern;
    }

    public Integer getLineGapWidth() {
        return lineGapWidth;
    }

    public void setLineGapWidth(Integer lineGapWidth) {
        this.lineGapWidth = lineGapWidth;
    }

    public Integer getLineOffset() {
        return lineOffset;
    }

    public void setLineOffset(Integer lineOffset) {
        this.lineOffset = lineOffset;
    }
}
