package com.jiin.admin.converter.gss;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlRootElement(name = "LineLayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class GssLineLayer {
    @XmlAttribute(name="type")
    private String Type;
    @XmlElement(name="Type")
    private Integer SubType;
    private String Color;
    private Float Width;
    private String JoinType;
    private String DashCap;
    private String StartCap;
    private String EndCap;
    private String DashOffset;
    private Integer VerticalType;
    private Integer StartPos;
    private Integer Interval;
    private Float Offset;
    private Integer LeftLength;
    private Integer RightLength;
    private String Picture;
    private Boolean TextureLine;
    private String Transparent;
    private Integer Space;
    @XmlElementWrapper(name="Dash")
    private List<Float> DashItem;


    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
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


    public Integer getStartPos() {
        return StartPos;
    }

    public void setStartPos(Integer startPos) {
        StartPos = startPos;
    }

    public Integer getInterval() {
        return Interval;
    }

    public void setInterval(Integer interval) {
        Interval = interval;
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



    /**
     * 넓이값에 따라 수치값 조정
     * @return
     */
    public List<Float> parseDashItem() {
        Integer startPos = getStartPos();
        Float width = getWidth();
        List<Float> newvalues = new ArrayList<>();
        List<Float> values = getDashItem();
        if(startPos != null){
            Float _startPos = Float.valueOf(getStartPos());
            newvalues.add(0f);
            newvalues.add(_startPos);
            int idx = 0;
            for(Float value : values){
                if(idx++ == 1){
                    newvalues.add(value - _startPos);
                }else{
                    newvalues.add(value);
                }
            }
        }else{
            newvalues.addAll(values);
        }
        List<Float> returnvalues = new ArrayList<>();
        for(Float value : newvalues){
            Float valuef = value / (width);
            returnvalues.add(Float.valueOf(String.format("%.3f",valuef)));
        }
        return returnvalues;
    }

    public void setDashItem(List<Float> dashItem) {
        DashItem = dashItem;
    }


    public Integer getLeftLength() {
        return LeftLength;
    }

    public void setLeftLength(Integer leftLength) {
        LeftLength = leftLength;
    }

    public Integer getRightLength() {
        return RightLength;
    }

    public void setRightLength(Integer rightLength) {
        RightLength = rightLength;
    }

    public Boolean getTextureLine() {
        return TextureLine;
    }

    public void setTextureLine(Boolean textureLine) {
        TextureLine = textureLine;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public Integer getSubType() {
        return SubType;
    }

    public void setSubType(Integer subType) {
        SubType = subType;
    }

    public Integer getVerticalType() {
        return VerticalType;
    }

    public void setVerticalType(Integer verticalType) {
        VerticalType = verticalType;
    }

    public Float getOffset() {
        return Offset;
    }

    public void setOffset(Float offset) {
        Offset = offset;
    }

    public List<Float> getDashItem() {
        return DashItem;
    }

    public Float getWidth() {
        return Width;
    }

    public void setWidth(Float width) {
        Width = width;
    }
}
