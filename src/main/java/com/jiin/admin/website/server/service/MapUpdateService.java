package com.jiin.admin.website.server.service;

import com.jiin.admin.dto.VersionDTO;
import com.jiin.admin.website.model.FileDownloadModel;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface MapUpdateService {
    StreamingResponseBody getFile(FileDownloadModel fileDownloadModel, HttpServletResponse response);

    List<VersionDTO> checkVersion(String mapdata);
}
