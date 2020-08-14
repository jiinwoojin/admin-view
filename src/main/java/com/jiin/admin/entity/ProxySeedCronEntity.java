package com.jiin.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "_PROXY_SEED_CRON")
@SequenceGenerator(
        name = "PROXY_SEED_CRON_SEQ_GEN",
        sequenceName = "PROXY_SEED_CRON_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Getter
@Setter
public class ProxySeedCronEntity {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PROXY_SEED_CRON_SEQ_GEN"
    )
    @Column(name = "ID")
    private Long id;

    @OneToOne
    @JoinColumn
    private ProxyCacheEntity cache;

    @Column(name = "SECOND")
    private String second; // 초

    @Column(name = "MINUTE")
    private String minute; // 분

    @Column(name = "HOUR")
    private String hour; // 시간 (0 ~ 23)

    @Column(name = "DAY")
    private String day; // 일 (1 ~ 31)

    @Column(name = "WEEK")
    private String week; // 주 (월 ~ 일)

    @Column(name = "MONTH")
    private String month; // 월 (1 ~ 12)

    // 연도 범위는 굳이 넣을 필요가 없을 거 같아 안 넣었다. 공휴일 아닌 이상.
}
