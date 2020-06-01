package com.jiin.admin.website.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// 파일 시스템을 거치는 Utility 메소드 모음.
@Slf4j
public class FileSystemUtil {
    /**
     * 운영체제가 Windows 인지 확인하는 함수
     */
    public static boolean isWindowOS(){
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * 수치를 받으면 이진수 배열로 만들어 주는 함수
     * @Param value int
     */
    public static boolean[] getBinaryNumberArrayByInteger(int value){
        if(value < 0) return null;
        int idx = 2;
        boolean[] array = new boolean[3];
        while(idx >= 0){
            array[idx] = value % 2 == 1;
            value /= 2;
            idx -= 1;
        }
        return array;
    }

    /**
     * 파일 권한 설정 With Code (LINUX, MAC)
     * @Param file Path
     * @throws IOException
     */
    public static void setFilePermissionsByCode(Path file, String code) throws IOException {
        if (code.length() != 3) return;
        char[] cStr = code.toCharArray();
        Map<String, Integer> map = new HashMap<String, Integer>() {{
            put("own", cStr[0] - '0');
            put("group", cStr[1] - '0');
            put("other", cStr[2] - '0');
        }};

        boolean[] arr;
        Set<PosixFilePermission> permissionSet = Files.readAttributes(file, PosixFileAttributes.class).permissions();
        for(String key : map.keySet()){
            arr = getBinaryNumberArrayByInteger(map.get(key));
            switch(key){
                case "own" :
                    if(arr[0]) permissionSet.add(PosixFilePermission.OWNER_READ);
                    if(arr[1]) permissionSet.add(PosixFilePermission.OWNER_WRITE);
                    if(arr[2]) permissionSet.add(PosixFilePermission.OWNER_EXECUTE);
                    break;
                case "group" :
                    if(arr[0]) permissionSet.add(PosixFilePermission.GROUP_READ);
                    if(arr[1]) permissionSet.add(PosixFilePermission.GROUP_WRITE);
                    if(arr[2]) permissionSet.add(PosixFilePermission.GROUP_EXECUTE);
                    break;
                case "other" :
                    if(arr[0]) permissionSet.add(PosixFilePermission.OTHERS_READ);
                    if(arr[1]) permissionSet.add(PosixFilePermission.OTHERS_WRITE);
                    if(arr[2]) permissionSet.add(PosixFilePermission.OTHERS_EXECUTE);
                    break;
            }
        }
        Files.setPosixFilePermissions(file, permissionSet);
    }

    /**
     * 파일 권한 설정 Default (LINUX, MAC)
     * @Param file Path
     * @throws IOException
     */
    public static void setFileDefaultPermissions(Path file) throws IOException {
        setFilePermissionsByCode(file, "755");
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

    /**
     * 파일 생성 (삭제 후 파일 생성)
     * @param filePath filePath
     * @param content  content
     * @param charset  charset
     * @throws IOException exception
     */
    public static void createAtFile(String filePath, String content, String charset) {
        // 파일이 있을 경우 삭제
        try {
            deleteFile(filePath);
        } catch (IOException e) {
            log.error(filePath + " 삭제 불가!");
        }

        File file = new File(filePath);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error(filePath + " 파일 생성 불가");
            }
        }


        try {
            FileUtils.write(file, content, charset);
        } catch (IOException e) {
            log.error(filePath + " 내용 생성 불가");
        }

        if (!isWindowOS()) {
            try {
                setFileDefaultPermissions(file.toPath());
            } catch (IOException e) {
                log.error(filePath + " 권한 설정 불가");
            }
        }
    }

    /**
     * 파일 생성 (삭제 후 파일 생성) charset default utf-8
     * @param filePath filePath
     * @param content  content
     * @throws IOException exception
     */
    public static void createAtFile(String filePath, String content) throws IOException {
        createAtFile(filePath, content, "utf-8");
    }

    /**
     * 파일 삭제
     * @param filePath 파일 경로
     * @throws IOException Exception
     */
    public static void deleteFile(String filePath) throws IOException {
        if (FileUtils.getFile(filePath).isFile()) {
            FileUtils.forceDelete(FileUtils.getFile(filePath));
        }
    }

    /**
     * 파일 이동
     * @param beforePath 파일 경로, newPath 파일 경로
     * @throws IOException Exception
     */
    public static void moveFile(String beforePath, String newPath){
        File bFile = new File(beforePath);
        String fileName = bFile.getName();

        File nPath = new File(newPath.replace("/" + fileName, ""));
        try {
            FileUtils.moveFileToDirectory(bFile, nPath, true);
        } catch (IOException e) {
            log.error("파일 옮기기 실패! " + beforePath + " -> " + newPath);
        }
    }
}
