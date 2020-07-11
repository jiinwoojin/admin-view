package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "_ROLE")
@SequenceGenerator(
        name = "ROLE_SEQ_GEN",
        sequenceName = "ROLE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class RoleEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ROLE_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    /**
     * 권한 영문
     */
    @Column(name = "TITLE", length = 20, nullable = false, unique = true)
    private String title;

    /**
     * 권한 이름
     */
    @Column(name = "LABEL", length = 20, nullable = false)
    private String label;

    /**
     * 지도 열람 중 투명도, 군대부호 사용 가능 여부
     */
    @Column(name = "MAP_BASIC", nullable = false)
    private Boolean mapBasic;

    /**
     * Map Server MAP 파일 관리 가능 여부
     */
    @Column(name = "MAP_MANAGE", nullable = false)
    private Boolean mapManage;

    /**
     * Map Server LAYER 파일 관리 가능 여부
     */
    @Column(name = "LAYER_MANAGE", nullable = false)
    private Boolean layerManage;

    /**
     * Map Proxy Cache 데이터 관리 가능 여부
     */
    @Column(name = "CACHE_MANAGE", nullable = false)
    private Boolean cacheManage;

    /**
     * 사용자 관리 가능 여부
     */
    @Column(name = "ACCOUNT_MANAGE", nullable = false)
    private Boolean accountManage;

    public RoleEntity() {

    }

    public RoleEntity(Long id, String title, String label, Boolean mapBasic, Boolean mapManage, Boolean layerManage, Boolean cacheManage, Boolean accountManage) {
        this.id = id;
        this.title = title;
        this.label = label;
        this.mapBasic = mapBasic;
        this.mapManage = mapManage;
        this.layerManage = layerManage;
        this.cacheManage = cacheManage;
        this.accountManage = accountManage;
    }
}
