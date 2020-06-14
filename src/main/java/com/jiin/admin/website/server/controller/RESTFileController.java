package com.jiin.admin.website.server.controller;

import com.jiin.admin.website.model.FileDownloadModel;
import com.jiin.admin.website.server.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("api/file")
public class RESTFileController {

    private final FileService fileService;

    @Autowired
    public RESTFileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(value = "download", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StreamingResponseBody> streamingDownload(FileDownloadModel fileDownloadModel,
                                                                   HttpServletResponse response) {
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileDownloadModel.getMap() + ".zip");

        StreamingResponseBody stream = fileService.getFile(fileDownloadModel, response);

        log.info("steaming response {} ", stream);
        return new ResponseEntity<>(stream, HttpStatus.OK);
    }
}
