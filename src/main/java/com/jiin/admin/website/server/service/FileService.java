package com.jiin.admin.website.server.service;

import com.jiin.admin.website.model.FileDownloadModel;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;

public interface FileService {
    StreamingResponseBody getFile(FileDownloadModel fileDownloadModel, HttpServletResponse response);
}
