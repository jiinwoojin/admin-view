package com.jiin.admin.website.view.controller;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("image")
public class ImageController {

    @Value("${project.data-path}")
    private String dataPath;

    @SneakyThrows
    @RequestMapping(value = "{name}.thumbnail.img",produces = MediaType.IMAGE_JPEG_VALUE)
    public void thumbnail(@PathVariable("name") String name, HttpServletResponse response) {
        try {
            File thumbnail = new File(dataPath, "tmp/" + name);
            InputStream tis = new FileInputStream(thumbnail);
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            StreamUtils.copy(tis, response.getOutputStream());
        } catch (IOException e) {
            ClassPathResource img = new ClassPathResource("data/no-image.png");
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            StreamUtils.copy(img.getInputStream(), response.getOutputStream());
        }
    }
}
