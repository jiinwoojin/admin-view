package com.jiin.admin.website.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiin.admin.Constants;
import com.jiin.admin.website.model.SymbolPositionModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// SYMBOL 이미지 작업을 처리하기 위한 작업
@Slf4j
public class ImageSpriteUtil {
    // 처음 저장할 때 실행하는 메소드. 반환되는 데이터는 DB 에 저장하게끔 한다.
    public static List<SymbolPositionModel> createImageSpriteArrayWithFiles(String dataPath, String imageName, List<MultipartFile> sprites) {
        int imageWidth = 0;
        int imageHeight = 0;
        int count = sprites.size();
        int sqrt = (int) Math.ceil(Math.sqrt(count));

        // 1 단계. PNG 파일 크기 측정
        for (int r = 0; r < sqrt; r++) {
            int width = 0;
            int height = 0;
            for (int c = 0; c < sqrt; c++) {
                int idx = r * sqrt + c;
                if (idx < count) {
                    MultipartFile sprite = sprites.get(idx);
                    try {
                        BufferedImage image = ImageIO.read(sprite.getInputStream());
                        width += image.getWidth();
                        height = Math.max(height, image.getHeight());
                    } catch (IOException e) {
                        log.error("ERROR - " + e.getMessage());
                    }
                }
            }
            imageWidth = Math.max(imageWidth, width);
            imageHeight += height;
        }

        // 2 단계. 이미지 및 JSON 그려 나가기
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics rect = bufferedImage.getGraphics();

        List<SymbolPositionModel> positions = new ArrayList<>();
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        int y = 0;
        for (int r = 0; r < sqrt; r++) {
            int x = 0;
            int ny = 0;
            for (int c = 0; c < sqrt; c++) {
                int idx = r * sqrt + c;
                if (idx < count) {
                    MultipartFile sprite = sprites.get(idx);
                    try {
                        BufferedImage image = ImageIO.read(sprite.getInputStream());
                        rect.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);

                        String filename = sprite.getOriginalFilename();
                        String suffix = filename.replace(".png", "").replace(".PNG", "");
                        final int saveX = x;
                        final int saveY = y;
                        positions.add(new SymbolPositionModel(suffix, x, y, image.getWidth(), image.getHeight()));
                        jsonMap.put(suffix, new LinkedHashMap<String, Object>() {
                            {
                                put("height", image.getHeight());
                                put("width", image.getWidth());
                                put("pixelRatio", Constants.DEFAULT_SYMBOL_PIXEL_RATIO);
                                put("x", saveX);
                                put("y", saveY);
                            }
                        });

                        x += image.getWidth();
                        ny = Math.max(ny, image.getHeight());
                    } catch (IOException e) {
                        log.error("ERROR - " + e.getMessage());
                    }
                }
            }
            y += ny;
        }

        rect.dispose();

        try {
            File mainDirectory = new File(dataPath + String.format("%s/%s", Constants.SYMBOL_FILE_PATH, imageName));
            if (!mainDirectory.exists()) {
                mainDirectory.mkdirs();
            }

            File png1X = new File(dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.PNG_SUFFIX));
            if (!png1X.exists()) {
                png1X.createNewFile();
            }

            File png2X = new File(dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.PNG_2X_SUFFIX));
            if (!png2X.exists()) {
                png2X.createNewFile();
            }

            ImageIO.write(bufferedImage, "PNG", png1X);
            ImageIO.write(bufferedImage, "PNG", png2X);

            String json1XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.JSON_SUFFIX);
            String json2XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.JSON_2X_SUFFIX);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMap);

            FileSystemUtil.createAtFile(json1XPath, json);
            FileSystemUtil.createAtFile(json2XPath, json);

            if (!FileSystemUtil.isWindowOS()) {
                FileSystemUtil.setFileDefaultPermissionsWithFileDirectory(mainDirectory);
            }
        } catch (FileNotFoundException e) {
            log.error("ERROR - " + e.getMessage());
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }

        return positions;
    }

    public static List<SymbolPositionModel> updateSpriteArrayWithImageArray(Map<String, BufferedImage> remainMap, String dataPath, String imageName) {
        int imageWidth = 0;
        int imageHeight = 0;
        int count = remainMap.size();
        int sqrt = (int) Math.ceil(Math.sqrt(count));

        List<String> keys = new ArrayList<>(remainMap.keySet());
        // 1 단계. PNG 파일 크기 측정
        for (int r = 0; r < sqrt; r++) {
            int width = 0;
            int height = 0;
            for (int c = 0; c < sqrt; c++) {
                int idx = r * sqrt + c;
                if (idx < count) {
                    String key = keys.get(idx);
                    BufferedImage image = remainMap.get(key);
                    width += image.getWidth();
                    height = Math.max(height, image.getHeight());
                }
            }
            imageWidth = Math.max(imageWidth, width);
            imageHeight += height;
        }

        // 2 단계. 이미지 및 JSON 그려 나가기
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics rect = bufferedImage.getGraphics();

        List<SymbolPositionModel> positions = new ArrayList<>();
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        int y = 0;
        for (int r = 0; r < sqrt; r++) {
            int x = 0;
            int ny = 0;
            for (int c = 0; c < sqrt; c++) {
                int idx = r * sqrt + c;
                if (idx < count) {
                    String key = keys.get(idx);
                    BufferedImage image = remainMap.get(key);
                    rect.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);

                    final int saveX = x;
                    final int saveY = y;
                    positions.add(new SymbolPositionModel(key, x, y, image.getWidth(), image.getHeight()));
                    jsonMap.put(key, new LinkedHashMap<String, Object>() {
                        {
                            put("height", image.getHeight());
                            put("width", image.getWidth());
                            put("pixelRatio", Constants.DEFAULT_SYMBOL_PIXEL_RATIO);
                            put("x", saveX);
                            put("y", saveY);
                        }
                    });

                    x += image.getWidth();
                    ny = Math.max(ny, image.getHeight());
                }
            }
            y += ny;
        }

        rect.dispose();

        try {
            File png1X = new File(dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.PNG_SUFFIX));
            if (png1X.exists()) {
                png1X.delete();
                png1X.createNewFile();
            }

            File png2X = new File(dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.PNG_2X_SUFFIX));
            if (png2X.exists()) {
                png2X.delete();
                png2X.createNewFile();
            }

            ImageIO.write(bufferedImage, "PNG", png1X);
            ImageIO.write(bufferedImage, "PNG", png2X);

            String json1XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.JSON_SUFFIX);
            String json2XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.JSON_2X_SUFFIX);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMap);

            FileSystemUtil.createAtFile(json1XPath, json);
            FileSystemUtil.createAtFile(json2XPath, json);

            if (!FileSystemUtil.isWindowOS()) {
                File mainDirectory = new File(dataPath + String.format("%s/%s", Constants.SYMBOL_FILE_PATH, imageName));
                FileSystemUtil.setFileDefaultPermissionsWithFileDirectory(mainDirectory);
            }
        } catch (FileNotFoundException e) {
            log.error("ERROR - " + e.getMessage());
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }

        return positions;
    }

    public static List<SymbolPositionModel> updateSpriteArrayWithImageArray(Map<String, BufferedImage> remainMap, String dataPath, String imageName) {
        int imageWidth = 0;
        int imageHeight = 0;
        int count = remainMap.size();
        int sqrt = (int) Math.ceil(Math.sqrt(count));

        List<String> keys = new ArrayList<>(remainMap.keySet());
        // 1 단계. PNG 파일 크기 측정
        for (int r = 0; r < sqrt; r++) {
            int width = 0;
            int height = 0;
            for (int c = 0; c < sqrt; c++) {
                int idx = r * sqrt + c;
                if (idx < count) {
                    String key = keys.get(idx);
                    BufferedImage image = remainMap.get(key);
                    width += image.getWidth();
                    height = Math.max(height, image.getHeight());
                }
            }
            imageWidth = Math.max(imageWidth, width);
            imageHeight += height;
        }

        // 2 단계. 이미지 및 JSON 그려 나가기
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics rect = bufferedImage.getGraphics();

        List<SymbolPositionModel> positions = new ArrayList<>();
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        int y = 0;
        for (int r = 0; r < sqrt; r++) {
            int x = 0;
            int ny = 0;
            for (int c = 0; c < sqrt; c++) {
                int idx = r * sqrt + c;
                if (idx < count) {
                    String key = keys.get(idx);
                    BufferedImage image = remainMap.get(key);
                    rect.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);

                    final int saveX = x;
                    final int saveY = y;
                    positions.add(new SymbolPositionModel(key, x, y, image.getWidth(), image.getHeight()));
                    jsonMap.put(key, new LinkedHashMap<String, Object>() {
                        {
                            put("height", image.getHeight());
                            put("width", image.getWidth());
                            put("pixelRatio", Constants.DEFAULT_SYMBOL_PIXEL_RATIO);
                            put("x", saveX);
                            put("y", saveY);
                        }
                    });

                    x += image.getWidth();
                    ny = Math.max(ny, image.getHeight());
                }
            }
            y += ny;
        }

        rect.dispose();

        try {
            File png1X = new File(dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.PNG_SUFFIX));
            if (png1X.exists()) {
                png1X.delete();
                png1X.createNewFile();
            }

            File png2X = new File(dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.PNG_2X_SUFFIX));
            if (png2X.exists()) {
                png2X.delete();
                png2X.createNewFile();
            }

            ImageIO.write(bufferedImage, "PNG", png1X);
            ImageIO.write(bufferedImage, "PNG", png2X);

            String json1XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.JSON_SUFFIX);
            String json2XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.JSON_2X_SUFFIX);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMap);

            FileSystemUtil.createAtFile(json1XPath, json);
            FileSystemUtil.createAtFile(json2XPath, json);

            if (!FileSystemUtil.isWindowOS()) {
                File mainDirectory = new File(dataPath + String.format("%s/%s", Constants.SYMBOL_FILE_PATH, imageName));
                FileSystemUtil.setFileDefaultPermissions(mainDirectory.toPath());
            }
        } catch (FileNotFoundException e) {
            log.error("ERROR - " + e.getMessage());
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }

        return positions;
    }

    public static List<SymbolPositionModel> updateSpriteArrayWithImageArray(Map<String, BufferedImage> remainMap, String dataPath, String imageName) {
        int imageWidth = 0;
        int imageHeight = 0;
        int count = remainMap.size();
        int sqrt = (int) Math.ceil(Math.sqrt(count));

        List<String> keys = new ArrayList<>(remainMap.keySet());
        // 1 단계. PNG 파일 크기 측정
        for (int r = 0; r < sqrt; r++) {
            int width = 0;
            int height = 0;
            for (int c = 0; c < sqrt; c++) {
                int idx = r * sqrt + c;
                if (idx < count) {
                    String key = keys.get(idx);
                    BufferedImage image = remainMap.get(key);
                    width += image.getWidth();
                    height = Math.max(height, image.getHeight());
                }
            }
            imageWidth = Math.max(imageWidth, width);
            imageHeight += height;
        }

        // 2 단계. 이미지 및 JSON 그려 나가기
        BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics rect = bufferedImage.getGraphics();

        List<SymbolPositionModel> positions = new ArrayList<>();
        Map<String, Object> jsonMap = new LinkedHashMap<>();

        int y = 0;
        for (int r = 0; r < sqrt; r++) {
            int x = 0;
            int ny = 0;
            for (int c = 0; c < sqrt; c++) {
                int idx = r * sqrt + c;
                if (idx < count) {
                    String key = keys.get(idx);
                    BufferedImage image = remainMap.get(key);
                    rect.drawImage(image, x, y, image.getWidth(), image.getHeight(), null);

                    final int saveX = x;
                    final int saveY = y;
                    positions.add(new SymbolPositionModel(key, x, y, image.getWidth(), image.getHeight()));
                    jsonMap.put(key, new LinkedHashMap<String, Object>() {
                        {
                            put("height", image.getHeight());
                            put("width", image.getWidth());
                            put("pixelRatio", Constants.DEFAULT_SYMBOL_PIXEL_RATIO);
                            put("x", saveX);
                            put("y", saveY);
                        }
                    });

                    x += image.getWidth();
                    ny = Math.max(ny, image.getHeight());
                }
            }
            y += ny;
        }

        rect.dispose();

        try {
            File png1X = new File(dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.PNG_SUFFIX));
            if (png1X.exists()) {
                png1X.delete();
                png1X.createNewFile();
            }

            File png2X = new File(dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.PNG_2X_SUFFIX));
            if (png2X.exists()) {
                png2X.delete();
                png2X.createNewFile();
            }

            ImageIO.write(bufferedImage, "PNG", png1X);
            ImageIO.write(bufferedImage, "PNG", png2X);

            String json1XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.JSON_SUFFIX);
            String json2XPath = dataPath + String.format("%s/%s/%s%s", Constants.SYMBOL_FILE_PATH, imageName, imageName, Constants.JSON_2X_SUFFIX);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMap);

            FileSystemUtil.createAtFile(json1XPath, json);
            FileSystemUtil.createAtFile(json2XPath, json);

            if (!FileSystemUtil.isWindowOS()) {
                File mainDirectory = new File(dataPath + String.format("%s/%s", Constants.SYMBOL_FILE_PATH, imageName));
                FileSystemUtil.setFileDefaultPermissions(mainDirectory.toPath());
            }
        } catch (FileNotFoundException e) {
            log.error("ERROR - " + e.getMessage());
        } catch (IOException e) {
            log.error("ERROR - " + e.getMessage());
        }

        return positions;
    }
}
