package com.jiin.admin.entity;

import com.jiin.admin.entity.enumeration.ServerType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "_SERVER_CONNECTION")
@SequenceGenerator(
        name = "SERVER_CONNECTION_SEQ_GEN",
        sequenceName = "SERVER_CONNECTION_SEQ",
        initialValue = 1,
        allocationSize = 1
)
public class ServerConnectionEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SERVER_CONNECTION_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    /**
     * Connection 정보 Key
     */
    @Column(name = "KEY", length = 20, nullable = false, unique = true)
    private String key;

    /**
     * Connection 정보 이름
     */
    @Column(name = "TITLE", nullable = false)
    private String title;

    /**
     * Connection 서버 TYPE
     */
    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private ServerType type;

    /**
     * Connection 정보 IP 주소
     */
    @Column(name = "IP_ADDRESS", nullable = false, unique = true)
    private String ipAddress;

    /**
     * Connection 정보 사용자 이름
     */
    @Column(name = "USERNAME", nullable = false)
    private String username;

    /**
     * Connection 정보 사용자 비밀번호
     */
    @Column(name = "PASSWORD", nullable = false, length = 512)
    private String password;

    /**
     * Connection 정보 포트
     */
    @Column(name = "PORT", length = 10)
    private String port;

    @Override
    public boolean isNew() {
        return false;
    }
}
