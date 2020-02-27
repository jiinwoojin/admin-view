package com.jiin.admin.website.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class LayerData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String group;
    private boolean status;
    private String type;
    private String projection;
    private String connectionType;
    private String connection;
    private String data;
    private double minScaleDenominator;
    private double maxScaleDenominator;
    private String filterItem;
    private String filter;
    private String classItem;
    private String labelItem;

    public LayerData(){

    }

    public LayerData(String name, String group, boolean status, String type, String projection, String connectionType, String connection, String data, double minScaleDenominator, double maxScaleDenominator, String filterItem, String filter, String classItem, String labelItem){
        this.name = name;
        this.group = group;
        this.status = status;
        this.type = type;
        this.projection = projection;
        this.connectionType = connectionType;
        this.connection = connection;
        this.data = data;
        this.minScaleDenominator = minScaleDenominator;
        this.maxScaleDenominator = maxScaleDenominator;
        this.filterItem = filterItem;
        this.filter = filter;
        this.classItem = classItem;
        this.labelItem = labelItem;
    }
}
