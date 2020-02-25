package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.gis.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/status")
public class StatusRestController {
    @Autowired
    private StatusService statusService;

    @GetMapping("memory/{center}")
    public Map<String, String> memoryStatusJSON(@PathVariable String center){
        return statusService.centerServerMemorization(center);
    }
}
