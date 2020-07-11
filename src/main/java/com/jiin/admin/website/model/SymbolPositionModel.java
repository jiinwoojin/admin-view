package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SymbolPositionModel {
    private Long id;
    private String name;
    private Integer height;
    private Integer width;
    private Integer pixelRatio;
    private Integer xPos;
    private Integer yPos;

    public SymbolPositionModel() {

    }

    public SymbolPositionModel(Long id, String name, Integer height, Integer width, Integer pixelRatio, Integer xPos, Integer yPos) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.width = width;
        this.pixelRatio = pixelRatio;
        this.xPos = xPos;
        this.yPos = yPos;
    }
}
