package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SymbolPositionDTO {
    private Long id;
    private String name;
    private Integer height;
    private Integer width;
    private Integer pixelRatio;
    private Integer xPos;
    private Integer yPos;

    public SymbolPositionDTO() {

    }
}
