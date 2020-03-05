package com.jiin.admin.entity;

import lombok.Data;
import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "MAP_LAYER")
@Data
public class MapLayer implements Persistable<Long> {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "TYPE", length = 20, nullable = false)
    private String type;

    @Column(name = "TITLE", length = 100, nullable = false)
    private String title;

    @Column(name = "SRS", length = 20, nullable = false)
    private String srs;

    @Column(name = "BBOX_E", nullable = false)
    private Float bboxE;

    @Column(name = "BBOX_W", nullable = false)
    private Float bboxW;

    @Column(name = "BBOX_S", nullable = false)
    private Float bboxS;

    @Column(name = "BBOX_N", nullable = false)
    private Float bboxN;

    @Override
    public boolean isNew() {
        return false;
    }
}
