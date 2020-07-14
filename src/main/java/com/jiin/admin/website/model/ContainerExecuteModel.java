package com.jiin.admin.website.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Map;

@Getter
@Setter
public class ContainerExecuteModel {
    private String service;
    private String command;
    private String hostname;
    private String user;

    public ContainerExecuteModel(){

    }

    public ContainerExecuteModel(String service, String command, String hostname, String user) {
        this.service = service;
        this.command = command;
        this.hostname = hostname;
        this.user = user;
    }

    public static ContainerExecuteModel convertToModel(Map<String, Object> map){
        if (map == null) {
            return null;
        }
        if (map.keySet().containsAll(Arrays.asList("service", "command", "hostname", "user"))) {
            String service = (String) map.get("service");
            String command = (String) map.get("command");
            String hostname = (String) map.get("hostname");
            String user = (String) map.get("user");
            return new ContainerExecuteModel(service, command, hostname, user);
        } else {
            return null;
        }

    }
}
