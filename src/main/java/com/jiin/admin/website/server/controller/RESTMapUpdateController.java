package com.jiin.admin.website.server.controller;

import com.jiin.admin.dto.VersionDTO;
import com.jiin.admin.website.model.FileDownloadModel;
import com.jiin.admin.website.server.service.MapUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/mapupdate")
public class RESTMapUpdateController {

    @Value("${project.data-path}")
    private String DATA_PATH;

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

        VersionDTO versionDTO = mapUpdateService.getFile(fileDownloadModel);
        response.setContentLengthLong(versionDTO.getZipFileSize());

        StreamingResponseBody stream = os -> {
            final InputStream inputStream = new FileInputStream(Paths.get(DATA_PATH, versionDTO.getZipFilePath()).toFile());
            byte[] data = new byte[2048];
            int read = 0;
            while ((read = inputStream.read(data)) > 0 ) {
                os.write(data, 0 , read);
            }
            os.flush();
        };

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
