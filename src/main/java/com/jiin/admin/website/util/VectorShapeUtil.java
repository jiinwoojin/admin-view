package com.jiin.admin.website.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class VectorShapeUtil {
    /**
     * VECTOR 파일 중 SHP 파일은 ZIP 압축으로 받되, 아래의 파일 확장자들이 무조건 포함되어 있어야 한다.
     */
    private static final Set<String> shapeFileExtensions = new HashSet<>(
        Arrays.asList("shp", "shx", "dbf")
    );

    /**
     * ZIP 파일을 받은 이후, 맨 상단부에 기재한 확장자 파일들을 모두 가지고 있는지 점검한다.
     * @Param file MultipartFile
     */
    public static boolean confirmIsZipFileContainsSHPFile(MultipartFile file) {
        String zipFilename = file.getOriginalFilename();
        if (FileSystemUtil.loadFileExtensionName(zipFilename).equals("zip")) {
            try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
                ZipEntry entry;
                Set<String> checked = new HashSet<>();
                while((entry = zis.getNextEntry()) != null) {
                    String entryFilename = entry.getName();
                    checked.add(FileSystemUtil.loadFileExtensionName(entryFilename));
                    if (checked.containsAll(shapeFileExtensions)) return true;
                }
                return false;
            } catch(IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
            return false;
        }
        return false;
    }

    /**
     * ZIP 파일 안에 있는 찐 SHP 파일의 이름을 찾아낸다.
     * 단, ZIP 파일에는 어떠한 디렉토리가 있으면 안 된다.
     * @Param file MultipartFile
     */
    public static String loadRealShpFileInZipFile(MultipartFile file) {
        String zipFilename = file.getOriginalFilename();
        if (FileSystemUtil.loadFileExtensionName(zipFilename).equals("zip")) {
            try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
                ZipEntry entry;
                while((entry = zis.getNextEntry()) != null) {
                    String entryFilename = entry.getName();
                    if (FileSystemUtil.loadFileExtensionName(entryFilename).equals("shp")) return entryFilename;
                }
            } catch(IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
            return "NONE";
        }
        return "NONE";
    }
}
