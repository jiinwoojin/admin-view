package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SymbolPositionEditModel {
    private Long imageId;
    private String beforeName;
    private String spriteName;
    private MultipartFile spriteImage;

    public SymbolPositionEditModel(){

    }

    public SymbolPositionEditModel(Long imageId, String beforeName, String spriteName, MultipartFile spriteImage) {
        this.imageId = imageId;
        this.beforeName = beforeName;
        this.spriteName = spriteName;
        this.spriteImage = spriteImage;
    }
}
