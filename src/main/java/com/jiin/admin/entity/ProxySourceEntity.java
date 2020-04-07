package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Entity(name = "_PROXY_SOURCE")
@SequenceGenerator(
        name = "PROXY_SOURCE_SEQ_GEN",
        sequenceName = "PROXY_SOURCE_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
public class ProxySourceEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PROXY_SOURCE_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    /**
     * Source 이름
     */
    @Column(name = "NAME", length = 20, nullable = false, unique = true)
    private String name;

    /**
     * Source 종류
     */
    @Column(name = "TYPE", nullable = false)
    private String type;

    /**
     * REQUEST MAP
     */
    @Column(name = "REQUEST_MAP", nullable = false)
    private String requestMap;

    /**
     * REQUEST LAYERS
     */
    @Column(name = "REQUEST_LAYERS", nullable = false)
    private String requestLayers;

    /**
     * MapServer Binary
     */
    @Column(name = "MAP_SERVER_BINARY", nullable = false)
    private String mapServerBinary;

    /**
     * MapServer Working Directory
     */
    @Column(name = "MAP_SERVER_WORK_DIR", nullable = false)
    private String mapServerWorkDir;

    public ProxySourceEntity(){

    }

    @Override
    public boolean isNew() {
        return false;
    }
}
