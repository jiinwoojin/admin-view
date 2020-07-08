package com.jiin.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ContainerHistoryDTO {
    private Long id;
    private String name;
    private String command;
    private Boolean succeed;
    private String workedUser;
    private Date workedDate;

    public ContainerHistoryDTO() {

    }

    public ContainerHistoryDTO(Long id, String name, String command, Boolean succeed, String workedUser, Date workedDate) {
        this.id = id;
        this.name = name;
        this.command = command;
        this.succeed = succeed;
        this.workedUser = workedUser;
        this.workedDate = workedDate;
    }
}
