package com.jiin.admin.starter;


import com.jiin.admin.config.BootingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootApplication(scanBasePackages = {"com.jiin.admin.config", "com.jiin.admin.servlet"})
@EnableJpaRepositories(basePackages = {"com.jiin.admin.website"}, transactionManagerRef = "transactionManager_BASE")
@EntityScan("com.jiin.admin.entity")
public class AdminWebApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Resource
    private BootingService bootingService;

    public static void main(String[] args) {
        SpringApplication.run(AdminWebApplication.class, args);
    }

    @Override
    public void run(String... args) {
        /*bootingService.initializeSymbol();
        try {
            bootingService.initializeLayer();
        } catch (IOException e) {
            System.out.println(">> initializeLayer error > " + e.getMessage());
        }*/
    }
}