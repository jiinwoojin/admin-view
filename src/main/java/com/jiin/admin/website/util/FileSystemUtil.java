package com.jiin.admin.website.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

// 파일 시스템을 거치는 Utility 메소드 모음.
public class FileSystemUtil {
    /**
     * 운영체제가 윈도우 계열인지 확인하는 메소드
     */
    private static boolean isWindowOS(){
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * 파일 권한 설정 (LINUX, MAC)
     * @param file Path
     * @throws IOException
     */
    public static void setFullFilePermissions(Path file) throws IOException {
        Set<PosixFilePermission> permissionSet = Files.readAttributes(file, PosixFileAttributes.class).permissions();

        permissionSet.add(PosixFilePermission.OWNER_WRITE);
        permissionSet.add(PosixFilePermission.OWNER_READ);
        permissionSet.add(PosixFilePermission.OWNER_EXECUTE);
        permissionSet.add(PosixFilePermission.GROUP_WRITE);
        permissionSet.add(PosixFilePermission.GROUP_READ);
        permissionSet.add(PosixFilePermission.GROUP_EXECUTE);
        permissionSet.add(PosixFilePermission.OTHERS_WRITE);
        permissionSet.add(PosixFilePermission.OTHERS_READ);
        permissionSet.add(PosixFilePermission.OTHERS_EXECUTE);

        Files.setPosixFilePermissions(file, permissionSet);
    }

    /**
     * 파일 새로 저장하기 (삭제 후 새 파일 생성)
     * @param filePath String, context String
     * @throws IOException
     */
    public static void writeContextAtFile(String filePath, String context) throws IOException {
        // 해당 디렉토리에 파일이 이미 있는 경우에는 파일을 삭제하고 내용을 채워 추가한다.
        if (FileUtils.getFile(filePath).isFile()) {
            FileUtils.forceDelete(FileUtils.getFile(filePath));
        }

        File file = new File(filePath);
        FileUtils.write(file, context, "utf-8");
        if(!isWindowOS()) setFullFilePermissions(file.toPath());
    }
}
