package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

// SYMBOL IMAGE 안에 있는 작은 데이터
@Getter
@Setter
@Entity(name = "_SYMBOL_POSITION")
@SequenceGenerator(
        name = "SYMBOL_POSITION_SEQ_GEN",
        sequenceName = "SYMBOL_POSITION_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class SymbolPositionEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SYMBOL_POSITION_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    /**
     * Symbol 이미지 코드 이름
     */
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    /**
     * Symbol 이미지 높이
     */
    @Column(name = "HEIGHT", nullable = false)
    private Integer height;

    /**
     * Symbol 이미지 너비
     */
    @Column(name = "WIDTH", nullable = false)
    private Integer width;

    /**
     * Symbol 이미지 픽셀 비율
     */
    @Column(name = "PIXEL_RATIO", nullable = false)
    private Integer pixelRatio;

    /**
     * Symbol 이미지 x축 위치 (전체 스프라이트 이미지 중)
     */
    @Column(name = "X_POS", nullable = false)
    private Integer xPos;

    /**
     * Symbol 이미지 y축 위치 (전체 스프라이트 이미지 중)
     */
    @Column(name = "Y_POS", nullable = false)
    private Integer yPos;



    /**
     * Symbol 에 종속된 Image 개체
     */
    @ManyToOne
    @JoinColumn
    private SymbolImageEntity image;

    @Override
    public boolean isNew() {
        return false;
    }
}
