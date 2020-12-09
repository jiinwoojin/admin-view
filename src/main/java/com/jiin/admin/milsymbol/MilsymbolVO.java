package com.jiin.admin.milsymbol;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MilsymbolVO {

    @JsonProperty(value = "MIL_CODE")
    private String milCode;
    @JsonProperty(value = "CODE_KOR_NAME")
    private String codeKorName;
    @JsonProperty(value = "CODE_NAME")
    private String codeName;
    @JsonProperty(value = "DESCRIPTION")
    private String description;
    @JsonProperty(value = "DIRECTION_EXPLANATION")
    private String directionExplanation;
    @JsonProperty(value = "PROPERTIES")
    private String properties;
    @JsonProperty(value = "MODIFIER")
    private String modifier;
    @JsonProperty(value = "APPLY_STATE")
    private String applyState;
    @JsonProperty(value = "SYMBOL_STATE")
    private String symbolState;
    @JsonProperty(value = "SYMBOL_TYPE")
    private String symbolType;
    @JsonProperty(value = "SYMBOL_CATEGORY")
    private String symbolCategory;

    public String getMilCode() {
        return milCode;
    }

    public void setMilCode(String milCode) {
        this.milCode = milCode;
    }

    public String getCodeKorName() {
        return codeKorName;
    }

    public void setCodeKorName(String codeKorName) {
        this.codeKorName = codeKorName;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirectionExplanation() {
        return directionExplanation;
    }

    public void setDirectionExplanation(String directionExplanation) {
        this.directionExplanation = directionExplanation;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getApplyState() {
        return applyState;
    }

    public void setApplyState(String applyState) {
        this.applyState = applyState;
    }

    public String getSymbolState() {
        return symbolState;
    }

    public void setSymbolState(String symbolState) {
        this.symbolState = symbolState;
    }

    public String getSymbolType() {
        return symbolType;
    }

    public void setSymbolType(String symbolType) {
        this.symbolType = symbolType;
    }

    public String getSymbolCategory() {
        return symbolCategory;
    }

    public void setSymbolCategory(String symbolCategory) {
        this.symbolCategory = symbolCategory;
    }
}
