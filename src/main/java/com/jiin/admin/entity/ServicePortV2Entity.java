package com.jiin.admin.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

// Port 변경이 진행될 수 있어 아래와 같은 조치를 고려해야 함.
// 포트 별 버전 관리 (ServicePortVerN (N >= 2))
@Getter
@Setter
@Entity(name = "_SERVICE_PORT_V2")
@SequenceGenerator(
        name = "SERVICE_PORT_V2_SEQ_GEN",
        sequenceName = "SERVICE_PORT_V2_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class ServicePortV2Entity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SERVICE_PORT_V2_SEQ_GEN"
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
     * PostgreSQL 포트 - OSM DB
     */
    @Column(name = "SQL_OSM_PORT", length = 10, nullable = false)
    private String sqlOSMPort = "5430";

    /**
     * PostgreSQL 포트 - Basic DB
     */
    @Column(name = "SQL_BASIC_PORT", length = 10, nullable = false)
    private String sqlBasicPort = "5432";

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
    @Column(name = "MAPNIK_PORT", length = 10, nullable = false)
    private String mapnikPort = "11140";

    /**
     * Terrain Server 포트
     */
    @Column(name = "TERRAIN_SERVER_PORT", length = 10, nullable = false)
    private String terrainServerPort = "11150";

    /**
     * LOS 포트 (Ji-in Height)
     */
    @Column(name = "JIIN_HEIGHT_PORT", length = 10, nullable = false)
    private String jiinHeightPort = "11160";

    /**
     * Vector Tile (Tegola) 포트
     */
    @Column(name = "TEGOLA_PORT", length = 10, nullable = false)
    private String tegolaPort = "11170";

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

    /**
     * RabbitMQ 1st 포트
     */
    @Column(name = "RABBITMQ_PORT1", length = 10, nullable = false)
    private String rabbitMQPort1 = "5672";

    /**
     * RabbitMQ 2nd 포트
     */
    @Column(name = "RABBITMQ_PORT2", length = 10, nullable = false)
    private String rabbitMQPort2 = "15672";
}
