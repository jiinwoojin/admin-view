package com.jiin.admin.website.server.service;

import com.jiin.admin.dto.VersionDTO;
import com.jiin.admin.mapper.data.LayerMapper;
import com.jiin.admin.mapper.data.MapUpdateMapper;
import com.jiin.admin.website.model.FileDownloadModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class MapUpdateServiceImpl implements MapUpdateService {

    @Value("${project.data-path}")
    private String DATA_PATH;

    @Resource
    private MapUpdateMapper mapUpdateMapper;

    @Override
    public StreamingResponseBody getFile(FileDownloadModel fileDownloadModel, HttpServletResponse response) {

        return out -> {
            File tmpDir = Paths.get(DATA_PATH, "tmp").toFile();
            //final ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());

            try (ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream())) {
                if (tmpDir.exists() && tmpDir.isDirectory()) {
                    try {
                        for (final File file : Objects.requireNonNull(tmpDir.listFiles())) {
                            FileSystemResource fileSystemResource = new FileSystemResource(file);
                            final ZipEntry zipEntry = new ZipEntry(file.getName());
                            zipEntry.setSize(fileSystemResource.contentLength());
                            log.info(String.valueOf(zipEntry.getSize()));
                            zipOutputStream.putNextEntry(zipEntry);
                            /*byte[] bytes = new byte[1024];
                            int length;
                            while ((length = inputStream.read(bytes)) >= 0) {
                                zipOutputStream.write(bytes, 0, length);
                            }
                            inputStream.close();*/
                            StreamUtils.copy(fileSystemResource.getInputStream(), zipOutputStream);
                        }
                        zipOutputStream.finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        };
    }

    @Override
    public List<VersionDTO> checkVersion(String mapdata) {

        List<VersionDTO> versions = new ArrayList<>();

        return versions;
    }
}
