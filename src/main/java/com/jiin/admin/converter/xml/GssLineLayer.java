package com.jiin.admin.converter.xml;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "LineLayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssLineLayer {
    private String type;
    private String Color;
    private String Width;
    private String JoinType;
    private String DashCap;
    private String StartCap;
    private String EndCap;
    private String DashOffset;
    private String VerticalType;
    private String StartPos;
    private String Interval;
    private String LeftLength;
    private String RightLength;
    private String Picture;
    private String TextureLine;
    private String Transparent;
    @XmlElementWrapper(name="Dash")
    private List<Integer> DashItem;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public String getWidth() {
        return Width;
    }

    public void setWidth(String width) {
        Width = width;
    }

    public String getJoinType() {
        return JoinType;
    }

    public void setJoinType(String joinType) {
        JoinType = joinType;
    }

    public String getDashCap() {
        return DashCap;
    }

    public void setDashCap(String dashCap) {
        DashCap = dashCap;
    }

    public String getDashOffset() {
        return DashOffset;
    }

    public void setDashOffset(String dashOffset) {
        DashOffset = dashOffset;
    }

    public List<Integer> getDashItem() {
        return DashItem;
    }

    public void setDashItem(List<Integer> dashItem) {
        DashItem = dashItem;
    }

    public String getStartCap() {
        return StartCap;
    }

    public String getVerticalType() {
        return VerticalType;
    }

    public void setVerticalType(String verticalType) {
        VerticalType = verticalType;
    }

    public String getStartPos() {
        return StartPos;
    }

    public void setStartPos(String startPos) {
        StartPos = startPos;
    }

    public String getInterval() {
        return Interval;
    }

    public void setInterval(String interval) {
        Interval = interval;
    }

    public String getLeftLength() {
        return LeftLength;
    }

    public void setLeftLength(String leftLength) {
        LeftLength = leftLength;
    }

    public String getRightLength() {
        return RightLength;
    }

    public void setRightLength(String rightLength) {
        RightLength = rightLength;
    }

    public void setStartCap(String startCap) {
        StartCap = startCap;
    }

    public String getEndCap() {
        return EndCap;
    }

    public void setEndCap(String endCap) {
        EndCap = endCap;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getTextureLine() {
        return TextureLine;
    }

    public void setTextureLine(String textureLine) {
        TextureLine = textureLine;
    }

    public String getTransparent() {
        return Transparent;
    }

    public void setTransparent(String transparent) {
        Transparent = transparent;
    }
}
