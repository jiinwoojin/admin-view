package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SymbolImageCreateModel {
    private String name;
    private String description;
    private List<MultipartFile> sprites;

    public SymbolImageCreateModel() {
        this.sprites = new ArrayList<>();
    }

    public SymbolImageCreateModel(String name, String description, List<MultipartFile> sprites) {
        this.name = name;
        this.description = description;
        this.sprites = sprites;
    }
}
