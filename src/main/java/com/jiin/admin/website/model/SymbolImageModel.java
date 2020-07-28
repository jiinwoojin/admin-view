package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SymbolImageModel {
    private String name;
    private String description;
    private List<MultipartFile> sprites;

    public SymbolImageModel() {
        this.sprites = new ArrayList<>();
    }

    public SymbolImageModel(String name, String description, List<MultipartFile> sprites) {
        this.name = name;
        this.description = description;
        this.sprites = sprites;
    }
}
