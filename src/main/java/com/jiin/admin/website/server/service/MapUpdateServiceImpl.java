package com.jiin.admin.website.server.service;

import com.jiin.admin.dto.VersionDTO;
import com.jiin.admin.mapper.data.MapUpdateMapper;
import com.jiin.admin.website.model.FileDownloadModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MapUpdateServiceImpl implements MapUpdateService {

    @Resource
    private MapUpdateMapper mapUpdateMapper;

    @Override
    public VersionDTO getFile(FileDownloadModel fileDownloadModel) {
        return mapUpdateMapper.findFileInfo(fileDownloadModel.getMap(), fileDownloadModel.getCurrentVersion());
    }

    @Override
    public List<VersionDTO> checkVersion(Map<String, List<VersionDTO>> map) {

        List<VersionDTO> mapdata = map.get("mapdata");

        log.info(Arrays.toString(mapdata.toArray()));

        List<VersionDTO> result = new LinkedList<>();

        for (VersionDTO versionDTO : mapdata) {
            List<VersionDTO> mapVersionDTO = mapUpdateMapper.findByNameAndVersion(versionDTO.getMap(), versionDTO.getVersion());

            if (mapVersionDTO != null) {
                result.addAll(mapVersionDTO);
            }
        }

        return result;
    }
}
