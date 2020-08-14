package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProxySeedCronDTO {
    private Long id;
    private Long cacheId;
    private String second;
    private String minute;
    private String hour;
    private String day;
    private String week;
    private String month;

    public ProxySeedCronDTO() {

    }

    public ProxySeedCronDTO(Long id, Long cacheId, String second, String minute, String hour, String day, String week, String month) {
        this.id = id;
        this.cacheId = cacheId;
        this.second = second;
        this.minute = minute;
        this.hour = hour;
        this.day = day;
        this.week = week;
        this.month = month;
    }
}
