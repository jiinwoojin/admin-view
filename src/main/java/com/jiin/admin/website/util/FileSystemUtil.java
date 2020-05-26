package com.jiin.admin.website.util;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
     * 파일 권한 설정 (LINUX, MAC) 755
     * @param file Path
     * @throws IOException exception
     */
    public static void setFullFilePermissions(Path file) throws IOException {
        Set<PosixFilePermission> permissionSet = Files.readAttributes(file, PosixFileAttributes.class).permissions();

        permissionSet.add(PosixFilePermission.OWNER_WRITE);
        permissionSet.add(PosixFilePermission.OWNER_READ);
        permissionSet.add(PosixFilePermission.OWNER_EXECUTE);
        permissionSet.add(PosixFilePermission.GROUP_READ);
        permissionSet.add(PosixFilePermission.GROUP_EXECUTE);
        permissionSet.add(PosixFilePermission.OTHERS_READ);
        permissionSet.add(PosixFilePermission.OTHERS_EXECUTE);

        Files.setPosixFilePermissions(file, permissionSet);
    }

    /**
     * 파일 새로 저장하기 (삭제 후 새 파일 생성)
     * @param filePath String, context String
     * @throws IOException Exception
     */
    public static void writeContextAtFile(String filePath, String context) throws IOException {
        // 해당 디렉토리에 파일이 이미 있는 경우만 내용을 채운다.
        if (FileUtils.getFile(filePath).isFile()) {
            File file = new File(filePath);
            FileUtils.write(file, context, "utf-8");
            if(!isWindowOS()) setFullFilePermissions(file.toPath());
        }
    }

    /**
     * 파일 내용 불러오기
     * @param filePath String
     * @throws IOException
     */
    public static String fetchFileContext(String filePath) throws IOException {
        StringBuilder fileContext = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            fileContext.append(line);
            fileContext.append(System.lineSeparator());
        }

        return fileContext.toString();
    }
}
