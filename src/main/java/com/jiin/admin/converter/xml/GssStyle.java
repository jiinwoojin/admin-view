package com.jiin.admin.converter.xml;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "Style")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssStyle {
    private String name;
    private String type;
    private String Font;
    private String Size;
    private String Bold;
    private String Itailc;
    private String Underline;
    private String Outline;
    private String OutlineColor;
    private String Box;
    private String BoxColor;
    private String SeaWaterLevel;
    private String Decimal;
    private String Prefix;
    private String Postfix;
    private String Align;
    private String OffsetX;
    private String OffsetY;

    private List<GssPointLayer> PointLayer;
    private List<GssLineLayer> LineLayer;
    private List<GssPolygonLayer> PolygonLayer;

    public String getFont() {
        return Font;
    }

    public void setFont(String font) {
        Font = font;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getBold() {
        return Bold;
    }

    public void setBold(String bold) {
        Bold = bold;
    }

    public String getItailc() {
        return Itailc;
    }

    public void setItailc(String itailc) {
        Itailc = itailc;
    }

    public String getUnderline() {
        return Underline;
    }

    public void setUnderline(String underline) {
        Underline = underline;
    }

    public String getOutline() {
        return Outline;
    }

    public void setOutline(String outline) {
        Outline = outline;
    }

    public String getOutlineColor() {
        return OutlineColor;
    }

    public void setOutlineColor(String outlineColor) {
        OutlineColor = outlineColor;
    }

    public String getBox() {
        return Box;
    }

    public void setBox(String box) {
        Box = box;
    }

    public String getBoxColor() {
        return BoxColor;
    }

    public void setBoxColor(String boxColor) {
        BoxColor = boxColor;
    }

    public String getSeaWaterLevel() {
        return SeaWaterLevel;
    }

    public void setSeaWaterLevel(String seaWaterLevel) {
        SeaWaterLevel = seaWaterLevel;
    }

    public String getDecimal() {
        return Decimal;
    }

    public void setDecimal(String decimal) {
        Decimal = decimal;
    }

    public String getPrefix() {
        return Prefix;
    }

    public void setPrefix(String prefix) {
        Prefix = prefix;
    }

    public String getPostfix() {
        return Postfix;
    }

    public void setPostfix(String postfix) {
        Postfix = postfix;
    }

    public String getAlign() {
        return Align;
    }

    public void setAlign(String align) {
        Align = align;
    }

    public List<GssPolygonLayer> getPolygonLayer() {
        return PolygonLayer;
    }

    public void setPolygonLayer(List<GssPolygonLayer> polygonLayer) {
        PolygonLayer = polygonLayer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOffsetX() {
        return OffsetX;
    }

    public void setOffsetX(String offsetX) {
        OffsetX = offsetX;
    }

    public String getOffsetY() {
        return OffsetY;
    }

    public void setOffsetY(String offsetY) {
        OffsetY = offsetY;
    }

    public List<GssPointLayer> getPointLayer() {
        return PointLayer;
    }

    public void setPointLayer(List<GssPointLayer> pointLayer) {
        PointLayer = pointLayer;
    }

    public List<GssLineLayer> getLineLayer() {
        return LineLayer;
    }

    public void setLineLayer(List<GssLineLayer> lineLayer) {
        LineLayer = lineLayer;
    }
}

