package com.jiin.admin.converter.mapbox;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MapboxLayout {
    @JsonProperty(defaultValue = "visible")
    private String visibility = "visible";
    @JsonProperty(value = "icon-image")
    private String iconImage;
    @JsonProperty(value = "icon-allow-overlap")
    private Boolean iconAllowOverlap;
    @JsonProperty(value = "text-field")
    private String textField;
    @JsonProperty(value = "text-anchor")
    private String textAnchor;
    @JsonProperty(value = "text-font")
    private String[] textFont;
    @JsonProperty(value = "text-size")
    private Integer textSize;
    @JsonProperty(value = "text-offset")
    private Float[] textOffset;
    @JsonProperty(value = "text-allow-overlap")
    private Boolean textAllowOverlap;
    @JsonProperty(value = "text-justify")
    private String textJustify;
    @JsonProperty(value = "text-rotation-alignment")
    private String textRotationAlignment;
    @JsonProperty(value = "text-pitch-alignment")
    private String textPitchAlignment;
    @JsonProperty(value = "text-keep-upright")
    private Boolean textKeepUpright;
    @JsonProperty(value = "line-join")
    private String lineJoin;
    @JsonProperty(value = "icon-size")
    private Float iconSize;
    @JsonProperty(value = "icon-rotate")
    private String[] iconRotate;
    @JsonProperty(value = "icon-rotation-alignment")
    private String iconRotationAlignment;
    @JsonProperty(value = "symbol-placement")
    private String symbolPlacement;
    @JsonProperty(value = "symbol-spacing")
    private Integer symbolSpacing;
    @JsonProperty(value = "icon-ignore-placement")
    private Boolean iconIgnorePlacement;
    @JsonProperty(value = "icon-optional")
    private Boolean iconOptional;
    @JsonProperty(value = "icon-anchor")
    private String iconAnchor;
    @JsonProperty(value = "icon-offset")
    private Float[] iconOffset;
    @JsonProperty(value = "icon-keep-upright")
    private Boolean iconKeepUpright;
    @JsonProperty(value = "icon-text-fit")
    private String iconTextFit;


    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public String[] getTextFont() {
        return textFont;
    }

    public void setTextFont(String[] textFont) {
        this.textFont = textFont;
    }

    public Integer getTextSize() {
        return textSize;
    }

    public void setTextSize(Integer textSize) {
        this.textSize = textSize;
    }

    public Float[] getTextOffset() {
        return textOffset;
    }

    public void setTextOffset(Float[] textOffset) {
        this.textOffset = textOffset;
    }

    public Boolean getTextAllowOverlap() {
        return textAllowOverlap;
    }

    public void setTextAllowOverlap(Boolean textAllowOverlap) {
        this.textAllowOverlap = textAllowOverlap;
    }

    public String getTextJustify() {
        return textJustify;
    }

    public void setTextJustify(String textJustify) {
        this.textJustify = textJustify;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getIconImage() {
        return iconImage;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
    }

    public Boolean getIconAllowOverlap() {
        return iconAllowOverlap;
    }

    public void setIconAllowOverlap(Boolean iconAllowOverlap) {
        this.iconAllowOverlap = iconAllowOverlap;
    }

    public String getLineJoin() {
        return lineJoin;
    }

    public void setLineJoin(String lineJoin) {
        this.lineJoin = lineJoin;
    }

    public Float getIconSize() {
        return iconSize;
    }

    public void setIconSize(Float iconSize) {
        this.iconSize = iconSize;
    }

    public String[] getIconRotate() {
        return iconRotate;
    }

    public void setIconRotate(String[] iconRotate) {
        this.iconRotate = iconRotate;
    }

    public String getIconRotationAlignment() {
        return iconRotationAlignment;
    }

    public void setIconRotationAlignment(String iconRotationAlignment) {
        this.iconRotationAlignment = iconRotationAlignment;
    }

    public String getTextRotationAlignment() {
        return textRotationAlignment;
    }

    public void setTextRotationAlignment(String textRotationAlignment) {
        this.textRotationAlignment = textRotationAlignment;
    }

    public String getSymbolPlacement() {
        return symbolPlacement;
    }

    public void setSymbolPlacement(String symbolPlacement) {
        this.symbolPlacement = symbolPlacement;
    }

    public Integer getSymbolSpacing() {
        return symbolSpacing;
    }

    public void setSymbolSpacing(Integer symbolSpacing) {
        this.symbolSpacing = symbolSpacing;
    }

    public Boolean getIconIgnorePlacement() {
        return iconIgnorePlacement;
    }

    public void setIconIgnorePlacement(Boolean iconIgnorePlacement) {
        this.iconIgnorePlacement = iconIgnorePlacement;
    }

    public Boolean getIconOptional() {
        return iconOptional;
    }

    public void setIconOptional(Boolean iconOptional) {
        this.iconOptional = iconOptional;
    }

    public String getIconAnchor() {
        return iconAnchor;
    }

    public void setIconAnchor(String iconAnchor) {
        this.iconAnchor = iconAnchor;
    }

    public Float[] getIconOffset() {
        return iconOffset;
    }

    public void setIconOffset(Float[] iconOffset) {
        this.iconOffset = iconOffset;
    }

    public Boolean getIconKeepUpright() {
        return iconKeepUpright;
    }

    public void setIconKeepUpright(Boolean iconKeepUpright) {
        this.iconKeepUpright = iconKeepUpright;
    }

    public Boolean getTextKeepUpright() {
        return textKeepUpright;
    }

    public void setTextKeepUpright(Boolean textKeepUpright) {
        this.textKeepUpright = textKeepUpright;
    }

    public String getIconTextFit() {
        return iconTextFit;
    }

    public void setIconTextFit(String iconTextFit) {
        this.iconTextFit = iconTextFit;
    }

    public String getTextAnchor() {
        return textAnchor;
    }

    public void setTextAnchor(String textAnchor) {
        this.textAnchor = textAnchor;
    }

    public String getTextPitchAlignment() {
        return textPitchAlignment;
    }

    public void setTextPitchAlignment(String textPitchAlignment) {
        this.textPitchAlignment = textPitchAlignment;
    }
}
