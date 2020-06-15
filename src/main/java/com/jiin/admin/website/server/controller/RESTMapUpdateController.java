package com.jiin.admin.website.server.controller;

import com.jiin.admin.dto.VersionDTO;
import com.jiin.admin.website.model.FileDownloadModel;
import com.jiin.admin.website.server.service.MapUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/mapupdate")
public class RESTMapUpdateController {

    private final MapUpdateService mapUpdateService;

    @Autowired
    public RESTMapUpdateController(MapUpdateService mapUpdateService) {
        this.mapUpdateService = mapUpdateService;
    }

    @GetMapping(value = "download", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> streamingDownload(FileDownloadModel fileDownloadModel,
                                                                   HttpServletResponse response) {
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileDownloadModel.getMap() + ".zip");

        StreamingResponseBody stream = mapUpdateService.getFile(fileDownloadModel, response);

        log.info("steaming response {} ", stream);
        return new ResponseEntity<>(stream, HttpStatus.OK);
    }

    @PostMapping(value = "checkversion")
    public ResponseEntity<List<VersionDTO>> checkVersion(@RequestBody Map<String, List<VersionDTO>> map) {
        if (map.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<VersionDTO> versionDTOS = mapUpdateService.checkVersion(map);

        return new ResponseEntity<>(versionDTOS, HttpStatus.OK);
    }
}
