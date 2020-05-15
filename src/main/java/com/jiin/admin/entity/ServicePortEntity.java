package com.jiin.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "_SERVICE_PORT")
@SequenceGenerator(
        name = "SERVICE_PORT_SEQ_GEN",
        sequenceName = "SERVICE_PORT_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class ServicePortEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SERVICE_PORT_SEQ_GEN"
    )
    private Long id;

    /**
     * 서버 종류
     */
    @JsonIgnore
    @OneToOne
    @JoinColumn
    private ServerConnectionEntity svr;

    /**
     * PostgreSQL 포트
     */
    @Column(name = "POSTGRE_SQL_PORT", length = 10, nullable = false)
    private String postgreSQLPort = "5432";

    /**
     * Watchdog 포트
     */
    @Column(name = "WATCHDOG_PORT", length = 10, nullable = false)
    private String watchdogPort = "9000";

    /**
     * Watchdog Heart Beat 포트
     */
    @Column(name = "WATCHDOG_HB_PORT", length = 10, nullable = false)
    private String watchdogHbPort = "9694";

    /**
     * PCP Process 포트
     */
    @Column(name = "PCP_PROCESS_PORT", length = 10, nullable = false)
    private String pcpProcessPort = "9898";

    /**
     * PGPool2 포트
     */
    @Column(name = "PG_POOL_2_PORT", length = 10, nullable = false)
    private String pgPool2Port = "9999";

    /**
     * Admin Server 포트
     */
    @Column(name = "ADMIN_SERVER_PORT", length = 10, nullable = false)
    private String adminServerPort = "11110";

    /**
     * Map Proxy 포트
     */
    @Column(name = "MAP_PROXY_PORT", length = 10, nullable = false)
    private String mapProxyPort = "11120";

    /**
     * Map Server 포트
     */
    @Column(name = "MAP_SERVER_PORT", length = 10, nullable = false)
    private String mapServerPort = "11130";

    /**
     * Vector Tile 포트
     */
    @Column(name = "VECTOR_TILE_PORT", length = 10, nullable = false)
    private String vectorTilePort = "11140";

    /**
     * Ji-in Height 포트
     */
    @Column(name = "JIIN_HEIGHT_PORT", length = 10, nullable = false)
    private String jiinHeightPort = "11150";

    /**
     * LOS 포트
     */
    @Column(name = "LOS_PORT", length = 10, nullable = false)
    private String losPort = "11160";

    /**
     * MINIO 포트
     */
    @Column(name = "MINIO_PORT", length = 10, nullable = false)
    private String minioPort = "11170";

    /**
     * Mapnik 포트
     */
    @Column(name = "MAPNIK_PORT", length = 10, nullable = false)
    private String mapnikPort = "11190";

    /**
     * Syncthing TCP 포트
     */
    @Column(name = "SYNCTHING_TCP_PORT", length = 10, nullable = false)
    private String syncthingTcpPort = "22000";

    /**
     * Syncthing UDP 포트
     */
    @Column(name = "SYNCTHING_UDP_PORT", length = 10, nullable = false)
    private String syncthingUdpPort = "21027";
}
