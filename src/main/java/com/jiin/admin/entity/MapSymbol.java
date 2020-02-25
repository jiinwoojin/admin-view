package com.jiin.admin.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "MAP_SYMBOL")
@Data
public class MapSymbol implements Serializable {
    public MapSymbol(){ }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // now work
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE", length = 20, nullable = false, unique = true)
    private String code;

    @Column(name = "NAME", length = 20, nullable = false)
    private String name;

    /**
     * POINT, POLYGON, POLYLINE, ANNOTATION, IMAGE
     */
    @Column(name = "TYPE", length = 10, nullable = false)
    private String type;

    @Column(name = "FILL_COLOR", length = 7)
    private String fillColor;

    @Column(name = "FILL_OPACITY", precision = 3, scale = 2)
    private Float fillOpacity;

    @Column(name = "STROKE_COLOR", length = 7)
    private String strokeColor;

    @Column(name = "STROKE_WIDTH", length = 3)
    private Integer strokeWidth;

    @Column(name = "STROKE_OPACITY", precision = 3, scale = 2)
    private Float strokeOpacity;


}
