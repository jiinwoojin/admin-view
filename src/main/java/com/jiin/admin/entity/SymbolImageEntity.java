package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

// SYMBOL 을 관리하는 IMAGE 데이터
@Getter
@Setter
@Entity(name = "_SYMBOL_IMAGE")
@SequenceGenerator(
        name = "SYMBOL_IMAGE_SEQ_GEN",
        sequenceName = "SYMBOL_IMAGE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class SymbolImageEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SYMBOL_IMAGE_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    /**
     * Symbol 이미지 폴더 이름
     */
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    /**
     * Symbol 설명
     */
    @Column(name = "DESCRIPTION", nullable = false, unique = true)
    private String description;

    /**
     * 기본값 여부
     */
    @Column(name = "IS_DEFAULT", nullable = false)
    private boolean isDefault;

    /**
     * 등록 날짜
     */
    @Column(name = "REGIST_TIME", nullable = false)
    private Date registTime;

    /**
     * 등록자 ID
     */
    @Column(name = "REGISTOR_ID")
    private String registorId;

    /**
     * 등록자 이름
     */
    @Column(name = "REGISTOR_NAME")
    private String registorName;

    /**
     * 수정 날짜
     */
    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    /**
     * Symbol 이미지 파일 경로
     */
    @Column(name = "IMAGE_FILE_PATH", length = 254, nullable = false)
    private String imageFilePath;

    /**
     * Symbol 이미지 파일 경로 2x
     */
    @Column(name = "IMAGE_2X_FILE_PATH", length = 254, nullable = false)
    private String image2xFilePath;

    /**
     * Symbol JSON 파일 경로
     */
    @Column(name = "JSON_FILE_PATH", length = 254, nullable = false)
    private String jsonFilePath;

    /**
     * Symbol JSON 파일 경로 2x
     */
    @Column(name = "JSON_2X_FILE_PATH", length = 254, nullable = false)
    private String json2xFilePath;
}
