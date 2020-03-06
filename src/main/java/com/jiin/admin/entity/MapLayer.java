package com.jiin.admin.entity;

import lombok.Data;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.List;

@Entity(name = "_MAP_LAYER")
@Data
public class MapLayer implements Persistable<Long> {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", length = 20, nullable = false, unique = true)
    private String name;

    @Column(name = "TITLE", length = 100, nullable = false)
    private String title;

    @Column(name = "IS_DEFAULT", nullable = false)
    private boolean isDefault;

    @ManyToMany
    @JoinTable(name = "_MAP_LAYER_SOURCE"
            , joinColumns = @JoinColumn(name="LAYER_ID")
            , inverseJoinColumns = @JoinColumn(name="SOURCE_ID")
    )
    private List<MapSource> source;

    @Override
    public boolean isNew() {
        return false;
    }
}
