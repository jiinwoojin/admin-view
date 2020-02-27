package com.jiin.admin.website.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MapData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private boolean status;
    private int width;
    private int height;
    private String projection;
    private String units;
    private double extentMinX;
    private double extentMinY;
    private double extentMaxX;
    private double extentMaxY;
    private List<LayerData> layers;

    public MapData(){
        this.layers = new ArrayList<>();
    }

    public MapData(String name, boolean status, int width, int height, String projection, String units, double extentMinX, double extentMinY, double extentMaxX, double extentMaxY, List<LayerData> layers){
        this.name = name;
        this.status = status;
        this.width = width;
        this.height = height;
        this.projection = projection;
        this.units = units;
        this.extentMinX = extentMinX;
        this.extentMinY = extentMinY;
        this.extentMaxX = extentMaxX;
        this.extentMaxY = extentMaxY;
        this.layers = layers;
    }
}
