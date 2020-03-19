package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "_LAYER")
@SequenceGenerator(
        name = "LAYER_SEQ_GEN",
        sequenceName = "LAYER_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
public class LayerEntity implements Persistable<Long> {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "LAYER_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    /**
     * LAYER 이름
     */
    @Column(name = "NAME", length = 20, nullable = false, unique = true)
    private String name;

    /**
     * 기술(설명)
     */
    @Column(name = "DESCRIPTION", length = 254)
    private String description;

    /**
     * 투영
     */
    @Column(name = "PROJECTION", length = 15)
    private String projection;

    /**
     * 중간 폴더 경로
     */
    @Column(name = "middle_folder", length = 50)
    private String middleFolder;

    /**
     * RASTER, VECTOR
     */
    @Column(name = "TYPE", length = 10)
    private String type;

    /**
     * LAYER 파일 경로
     */
    @Column(name = "LAYER_FILE_PATH", length = 254)
    private String layerFilePath;

    /**
     * DATA 파일 경로
     */
    @Column(name = "DATA_FILE_PATH", length = 254)
    private String dataFilePath;

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

    @OneToMany(mappedBy = "layer", cascade = CascadeType.ALL)
    private Set<MapLayerRelationEntity> mapLayerRelations = new HashSet<>();

    @Override
    public boolean isNew() {
        return false;
    }
}
