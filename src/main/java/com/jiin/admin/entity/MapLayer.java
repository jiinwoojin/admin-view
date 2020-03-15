package com.jiin.admin.entity;

import lombok.Data;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Date;
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

    @Column(name = "THUMBNAIL", length = 254)
    private String thumbnail;

    @Column(name = "FILE_PATH", length = 254)
    private String filePath;

    @Column(name = "IS_DEFAULT", nullable = false)
    private boolean isDefault;

    @Column(name = "REGISTOR_ID", nullable = false)
    private String registorId;

    @Column(name = "REGISTOR_NAME", nullable = false)
    private String registorName;

    @Column(name = "REGIST_TIME", nullable = false)
    private Date registTime;

    /*@ManyToMany
    @JoinTable(name = "_MAP_LAYER_SOURCE"
            , joinColumns = @JoinColumn(name="LAYER_ID")
            , inverseJoinColumns = @JoinColumn(name="SOURCE_ID")
    )*/
    //private List<MapSource> source;

    @Override
    public boolean isNew() {
        return false;
    }
}
