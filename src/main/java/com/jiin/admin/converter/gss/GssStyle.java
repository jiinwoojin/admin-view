package com.jiin.admin.converter.gss;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "Style")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssStyle {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String type;
    private String Font;
    private String Color;
    private Integer Size;
    private String Bold;
    private String Itailc;
    private String Underline;
    private Boolean Outline;
    private String OutlineColor;
    private String Box;
    private String BoxColor;
    private String SeaWaterLevel;
    private String Decimal;
    private String Prefix;
    private String Postfix;
    private Integer Align;
    private Float OffsetX;
    private Float OffsetY;
    private String Picture;

    private List<GssPointLayer> PointLayer;
    private List<GssLineLayer> LineLayer;
    private List<GssPolygonLayer> PolygonLayer;

    public String getFont() {
        return Font;
    }

    public void setFont(String font) {
        Font = font;
    }

    public Integer getSize() {
        return Size;
    }

    public void setSize(Integer size) {
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

    public Float getOffsetX() {
        return OffsetX;
    }

    public void setOffsetX(Float offsetX) {
        OffsetX = offsetX;
    }

    public Float getOffsetY() {
        return OffsetY;
    }

    public void setOffsetY(Float offsetY) {
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

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public Integer getAlign() {
        return Align;
    }

    public void setAlign(Integer align) {
        Align = align;
    }

    public Boolean getOutline() {
        return Outline;
    }

    public void setOutline(Boolean outline) {
        Outline = outline;
    }
}

