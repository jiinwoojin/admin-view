package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "_MAP")
@SequenceGenerator(
        name = "MAP_SEQ_GEN",
        sequenceName = "MAP_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
public class MapEntity implements Persistable<Long> {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "MAP_SEQ_GEN"
    )
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
     * output file format
     */
    @Column(name = "IMAGE_TYPE", length = 10)
    private String imageType;

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

    /**
     * 수정 날짜
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * 버전 정보
     */
    @Column(name = "VERSION", length = 10)
    private Double version;

    @OneToMany(mappedBy = "map", cascade = CascadeType.ALL)
    private Set<MapLayerRelationEntity> mapLayerRelations = new HashSet<>();

    @Override
    public boolean isNew() {
        return false;
    }

    @PrePersist
    public void prePersist() {
        this.version = this.version == null ? 1.0 : this.version;
    }
}
