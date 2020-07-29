package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SymbolPositionModel {
    private String name;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer pixelRatio;
    private Long imageId;

    public SymbolPositionModel() {

    }

    public SymbolPositionModel(String name, Integer x, Integer y, Integer width, Integer height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pixelRatio = 1;
    }
}
