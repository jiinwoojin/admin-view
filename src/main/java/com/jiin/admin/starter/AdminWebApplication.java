package com.jiin.admin.starter;


import com.jiin.admin.config.BootingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import javax.annotation.Resource;

@SpringBootApplication(scanBasePackages = {"com.jiin.admin.config", "com.jiin.admin.servlet"})
public class AdminWebApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Resource
    private BootingService bootingService;

    public static void main(String[] args) {
        SpringApplication.run(AdminWebApplication.class, args);
    }

    @Override
    public void run(String... args) {
        bootingService.initializeSymbol();
    }
}