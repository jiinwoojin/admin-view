package com.jiin.admin.website.model;

import com.jiin.admin.dto.SymbolPositionDTO;
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

    public static SymbolPositionDTO convertDTO(SymbolPositionModel model) {
        if (model == null) return null;
        else {
            return new SymbolPositionDTO(0L, model.getName(), model.getHeight(), model.getWidth(), model.getPixelRatio(), model.getX(), model.getY(), model.getImageId());
        }
    }
}
