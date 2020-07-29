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
    private Long imageId;
    private byte[] pngBytes;

    public SymbolPositionDTO() {

    }

    public SymbolPositionDTO(Long id, String name, Integer height, Integer width, Integer pixelRatio, Integer xPos, Integer yPos, Long imageId, byte[] pngBytes) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.width = width;
        this.pixelRatio = pixelRatio;
        this.xPos = xPos;
        this.yPos = yPos;
        this.imageId = imageId;
        this.pngBytes = pngBytes;
    }
}
