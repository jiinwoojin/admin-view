package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "_CONTAINER_HISTORY")
@SequenceGenerator(
        name = "CONTAINER_HISTORY_SEQ_GEN",
        sequenceName = "CONTAINER_HISTORY_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
public class ContainerHistoryEntity implements Persistable<Long> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "CONTAINER_HISTORY_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    // Docker Container 이름
    @Column(name = "NAME", length = 20, nullable = false)
    private String name;

    // START, STOP, RESTART 명령어 중 하나
    @Column(name = "COMMAND", length = 10, nullable = false)
    private String command;

    // 명령어 동작 성공 여부
    @Column(name = "SUCCEED", nullable = false)
    private Boolean succeed;

    // 명령어 진행 서버 호스트 이름
    @Column(name = "WORKED_HOSTNAME")
    private String workedHostname;

    // 명령어 진행 회원
    @Column(name = "WORKED_USER", nullable = false, length = 30)
    private String workedUser;

    // 명령어 동작 시각
    @Column(name = "WORKED_DATE", nullable = false)
    private Date workedDate;

    @Override
    public boolean isNew() {
        return false;
    }
}
