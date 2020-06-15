package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "_MAP_VERSION")
@SequenceGenerator(
        name = "MAP_VERSION_SEQ_GEN",
        sequenceName = "MAP_VERSION_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Table(uniqueConstraints={
    @UniqueConstraint(columnNames = { "MAP_ID", "VERSION" })
})
@Getter
@Setter
public class MapVersionEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "MAP_VERSION_SEQ"
    )
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn
    private MapEntity map; // Map Entity

    @Column(name = "VERSION", length = 10)
    private Double version;

    @Column(name = "ZIP_FILE_PATH", length = 255)
    private String zipFilePath;

    @Column(name = "ZIP_FILE_SIZE")
    private Long zipFileSize;

    @Column(name = "UPLOAD_DATE")
    private Date uploadDate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "_MAP_LAYER_VERSION", joinColumns = @JoinColumn(name = "MAP_VERSION_ID"), inverseJoinColumns = @JoinColumn(name = "MAP_LAYER_ID"))
    private List<LayerEntity> layers;
}
