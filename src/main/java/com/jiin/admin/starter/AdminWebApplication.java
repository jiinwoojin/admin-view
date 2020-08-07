package com.jiin.admin.starter;


import com.jiin.admin.config.BootingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.Resource;

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
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AdminWebApplication.class);
    }

    @Override
    public void run(String... args) {
        bootingService.initializeMapData();
        bootingService.initializeRoles();
        bootingService.initializeAccounts();
    }
}