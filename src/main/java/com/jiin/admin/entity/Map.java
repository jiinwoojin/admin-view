package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "_MAP")
@Getter
@Setter
public class Map implements Persistable<Long> {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    /**
     * MAP 이름
     */
    @Column(name = "NAME", length = 50, nullable = false, unique = true)
    private String name;

    /**
     * MAP 파일 경로
     */
    @Column(name = "MAP_FILE_PATH", length = 254, nullable = false)
    private String mapFilePath;

    @Column(name = "MIN_X", length = 10)
    private String minX;

    @Column(name = "MAX_X", length = 10)
    private String maxX;

    @Column(name = "MIN_Y", length = 10)
    private String minY;

    @Column(name = "MAX_Y", length = 10)
    private String maxY;

    /**
     * 단위
     */
    @Column(name = "UNITS", length = 15)
    private String units;

    /**
     * 투영
     */
    @Column(name = "PROJECTION", length = 15)
    private String projection;

    /**
     * 기술(설명)
     */
    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * 기본값 여부
     */
    @Column(name = "IS_DEFAULT", nullable = false)
    private boolean isDefault;

    /**
     * 등록자 ID
     */
    @Column(name = "REGISTOR_ID", nullable = false)
    private String registorId;

    /**
     * 등록자 이름
     */
    @Column(name = "REGISTOR_NAME", nullable = false)
    private String registorName;

    /**
     * 등록 날짜
     */
    @Column(name = "REGIST_TIME", nullable = false)
    private Date registTime;

    @OneToMany
    @JoinTable(name = "_MAP_LAYER_RELATION"
            , joinColumns = @JoinColumn(name = "MAP_ID")
            , inverseJoinColumns = @JoinColumn(name = "LAYER_ID"))
    private List<Layer> layers;

    @Override
    public boolean isNew() {
        return false;
    }
}
