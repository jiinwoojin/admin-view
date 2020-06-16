package com.jiin.admin.website.server.service;

import com.jiin.admin.dto.VersionDTO;
import com.jiin.admin.website.model.FileDownloadModel;

import java.util.List;
import java.util.Map;

public interface MapUpdateService {
    VersionDTO getFile(FileDownloadModel fileDownloadModel);

    List<VersionDTO> checkVersion(Map<String, List<VersionDTO>> map);
}
