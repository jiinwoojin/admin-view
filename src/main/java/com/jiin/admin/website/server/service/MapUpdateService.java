package com.jiin.admin.website.server.service;

import com.jiin.admin.website.model.FileDownloadModel;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;

public interface MapUpdateService {
    StreamingResponseBody getFile(FileDownloadModel fileDownloadModel, HttpServletResponse response);
}
