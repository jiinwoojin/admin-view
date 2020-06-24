package com.jiin.admin.website.util;

import com.jiin.admin.Constants;
import com.jiin.admin.dto.LayerDTO;
import com.jiin.admin.dto.MapDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class GdalUtil {

    private static final String VRT_SCRIPT = "gdalbuildvrt";

    public static void createVrt(String dataPath, MapDTO mapDTO, List<LayerDTO> layers) {
        // tmp로 data 파일 이동
        File tmpDir = Paths.get(dataPath, Constants.TMP_DIR_PATH, mapDTO.getName()).toFile();
        for (LayerDTO layerDTO : layers) {
            File dataFile = Paths.get(dataPath, layerDTO.getDataFilePath()).toFile();
            try {
                FileUtils.copyFileToDirectory(dataFile, tmpDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // vrt 생성
        String vrtFileName = String.format("%s%s", mapDTO.getName(), Constants.VRT_SUFFIX);
        String vrtFilePath = Paths.get(tmpDir.getPath(), vrtFileName).toString();
        String targetDataPath = String.format("%s/*", tmpDir);
        String cmd = String.format("%s %s %s %s", VRT_SCRIPT, "-resolution average -a_srs EPSG:4326 -r nearest -addalpha", vrtFilePath, targetDataPath);
        log.info("GDAL CMD : " + cmd);
        if (LinuxCommandUtil.fetchResultByLinuxCommon(cmd) == 0) {
            log.info("성공");
            // vrt 파일 이동
            String newVrtPath = Paths.get(dataPath, Constants.VRT_FILE_PATH, vrtFileName).toString();

            if (FileUtils.getFile(newVrtPath).exists()) {
                try {
                    FileSystemUtil.deleteFile(newVrtPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileSystemUtil.moveFile(vrtFilePath, newVrtPath);

            try {
                // 폴더 삭제
                FileUtils.deleteDirectory(tmpDir);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else {
            // TODO 실패 로직 추가 필요
            log.info("실패");
        }
    }
}
