package com.jiin.admin.website.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

// SYMBOL 이미지 작업을 처리하기 위한 작업
@Slf4j
public class ImageSpriteUtil {
    public static void createImageSpriteArrayWithFiles(String dataPath, String imageName, List<MultipartFile> sprites) {
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
            File file = new File(dataPath + String.format("/html/%s.png", imageName));
            if (!file.exists()) {
                file.createNewFile();
            }
            ImageIO.write(bufferedImage, "PNG", file);
        } catch (FileNotFoundException e) {
            log.error("ERROR - " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
