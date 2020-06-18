package com.jiin.admin.website.util;

import com.jiin.admin.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
     * 폴더를 순회하며 모든 권한을 기본 권한 (755) 로 설정하기
     * @Param file Path
     * @throws IOException
     */
    public static void setFileDefaultPermissionsWithFileDirectory(File file) throws IOException {
        if(!file.exists()) return;
        if(file.isFile()){
            setFileDefaultPermissions(file.toPath());
            return;
        } else {
            File[] files = file.listFiles();
            for(File f : files){
                if(f.isDirectory()){
                    setFileDefaultPermissions(f.toPath());
                }
                setFileDefaultPermissionsWithFileDirectory(f);
            }
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
            log.error("ERROR - " + e.getMessage());
        }

        File file = new File(filePath);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }


        try {
            FileUtils.write(file, content, charset);
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }

        if (!isWindowOS()) {
            try {
                setFileDefaultPermissions(file.toPath());
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }
        }
    }

    /**
     * 파일 생성 (삭제 후 파일 생성) charset default utf-8
     * @param filePath filePath
     * @param content  content
     */
    public static void createAtFile(String filePath, String content) {
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
        } else {
            File directory = new File(filePath);
            if(directory.exists()) {
                File[] children = directory.listFiles();
                for (File file : children) {
                    if (file.isFile()) {
                        file.delete();
                    } else {
                        deleteFile(file.getPath());
                    }
                    file.delete();
                }
                directory.delete();
            }
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
            log.error("ERROR - " + e.getMessage());
        }
    }

    /**
     * ZIP 파일 압축 해제
     * @param zipFile File, directory String
     * @throws FileNotFoundException, IOException Exception
     */
    public static void decompressZipFile(File zipFile){
        String directory = zipFile.getParent();
        try(
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(fis);
        ){
            ZipEntry entry;
            while((entry = zis.getNextEntry()) != null){
                String filename = entry.getName();
                File file = new File(directory, filename);
                if(entry.isDirectory()){
                    file.mkdirs();
                } else {
                    saveFileInZipStream(file, zis);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("ERROR - " + e.getMessage());
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }
    }

    /**
     * ZIP 파일 내에 있는 파일 저장 메소드
     * @param file File, zis ZipInputStream
     * @throws FileNotFoundException, IOException Exception
     */
    private static void saveFileInZipStream(File file, ZipInputStream zis) throws IOException {
        File parent = new File(file.getParent());
        if(!parent.exists()) parent.mkdirs();
        try(FileOutputStream fos = new FileOutputStream(file)){
            byte[] buffer = new byte[1024];
            int size = 0;
            while((size = zis.read(buffer)) > 0){
                fos.write(buffer, 0, size);
            }
        } catch (FileNotFoundException e) {
            log.error("ERROR - " + e.getMessage());
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }
    }

    /**
     * 디렉토리를 복사한다.
     * @param from File, to File
     */
    private static void copyDirectory(File from, File to) throws IOException {
        if(from.isFile()) {
            FileUtils.copyFile(from, to);
        } else {
            FileUtils.copyDirectoryToDirectory(from, to);
        }
    }

    /**
     * ZIP 파일 생성 메소드
     * @param dataPath String, zipPath String, filename String, paths List of Strings
     */
    public static File saveZipFileWithPaths(String dataPath, String zipPath, String filename, List<Map<String, String>> paths){
        // 처음에는 ZIP 파일 존재 여부를 확인한다.
        File zipFile = new File(zipPath + "/" + filename);
        if(zipFile.exists()) {
            try {
                deleteFile(zipFile.getPath());
            } catch (IOException e) {
                log.info(filename + " 파일이 없어 생성하는 작업을 진행합니다.");
            }
        }

        for(Map<String, String> path : paths) {
            String cadrgExcludePath = dataPath + path.get("dataFilePath").replace("/RPF/A.TOC", "");
            File file = new File(cadrgExcludePath);
            if (!file.isDirectory()) { // RASTER 파일인 경우
                try {
                    copyDirectory(file, new File(String.format("%s/%s%s", zipPath, filename.replace(".zip", ""), path.get("dataFilePath").replaceFirst(Constants.DATA_PATH, ""))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else { // CADRG 파일인 경우 (Vector 파일의 경우 조치 추가 필요)
                String cadrgHome = dataPath + Constants.DATA_PATH + "/" + path.get("middleFolder");
                try {
                    String[] split = path.get("middleFolder").split("/");
                    String tmpPath = split.length > 0 ? path.get("middleFolder") : "";
                    if(split.length > 0){
                        tmpPath = tmpPath.replaceFirst("(?s)(.*)" + split[split.length - 1], "$1");
                    }
                    copyDirectory(new File(cadrgHome), new File(String.format("%s/%s/%s", zipPath, filename.replace(".zip", ""), tmpPath)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(zipPath + "/" + filename);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            File fileToZip = new File(zipPath + "/" + filename.replace(".zip", ""));

            for(File file : fileToZip.listFiles()) {
                zipFile(file, file.getName(), zipOut);
            }

            zipOut.closeEntry();
            zipOut.close();
            fos.close();

            FileUtils.deleteDirectory(fileToZip); // 모든 파일이 압축 완료된 이후에는 전부 날려버린다.

            return new File(zipPath + "/" + filename);

        } catch (FileNotFoundException e) {
            log.error("ERROR - " + e.getMessage());
            return null;
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
            return null;
        }
    }

    /**
     * ZIP 파일 압축 메소드 : 소스 코드 참조
     * @param fileToZip File, fileName String, zipOut ZipOutputStream
     */
    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
