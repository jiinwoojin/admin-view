package com.jiin.admin.entity;

import lombok.Data;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "_MAP_SOURCE")
@Data
public class MapSource implements Persistable<Long> {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "TYPE", length = 50, nullable = false)
    private String type;

    @Column(name = "MAP_PATH", length = 254, nullable = false)
    private String mapPath;

    @Column(name = "LAYERS", nullable = false)
    private String layers;

    @Column(name = "USE_CACHE", nullable = false)
    private boolean useCache;

    @Column(name = "CACHE_NAME", unique = true)
    private String cacheName;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "IS_DEFAULT", nullable = false)
    private boolean isDefault;

    @Column(name = "REGISTOR_ID", nullable = false)
    private String registorId;

    @Column(name = "REGISTOR_NAME", nullable = false)
    private String registorName;
    
    @Column(name = "REGIST_TIME", nullable = false)
    private Date registTime;

    @ManyToMany
    private List<MapSource> source;

    @Override
    public boolean isNew() {
        return false;
    }
}
