package com.jiin.admin.converter.gss;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlRootElement(name = "LineLayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssLineLayer {
    @XmlAttribute
    private String type;
    private String Color;
    private Integer Width;
    private String JoinType;
    private String DashCap;
    private String StartCap;
    private String EndCap;
    private String DashOffset;
    private String VerticalType;
    private Integer StartPos;
    private String Interval;
    private String LeftLength;
    private String RightLength;
    private String Picture;
    private String TextureLine;
    private String Transparent;
    private Integer Space;
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

    public Integer getWidth() {
        return Width;
    }

    public void setWidth(Integer width) {
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


    public String getStartCap() {
        return StartCap;
    }

    public String getVerticalType() {
        return VerticalType;
    }

    public void setVerticalType(String verticalType) {
        VerticalType = verticalType;
    }

    public Integer getStartPos() {
        return StartPos;
    }

    public void setStartPos(Integer startPos) {
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

    public Integer getSpace() {
        return Space;
    }

    public void setSpace(Integer space) {
        Space = space;
    }

    public List<Integer> getDashItem() {
        return DashItem;
    }

    /**
     * 넓이값에 따라 수치값 조정
     * @return
     */
    public List<Float> parseDashItem() {
        Integer startPos = getStartPos();
        Float width = Float.valueOf(getWidth());
        List<Float> newvalues = new ArrayList<>();
        if(startPos != null){
            newvalues.add(0f);
            Float valuef = Float.valueOf(startPos) / width;
            newvalues.add(Float.valueOf(String.format("%.1f",valuef)));
        }
        List<Integer> values = getDashItem();
        for(Integer value : values){
            Float valuef = Float.valueOf(value) / width;
            newvalues.add(Float.valueOf(String.format("%.1f",valuef)));
        }
        return newvalues;
    }

    public void setDashItem(List<Integer> dashItem) {
        DashItem = dashItem;
    }


}
