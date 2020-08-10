package com.jiin.admin.website.view.component;

import com.jiin.admin.config.AutowiringSpringBeanJobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class QuartzSchedulerComponent {
//    @Autowired
//    private SchedulerFactoryBean schedulerFactoryBean;
//
//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext) {
//        SchedulerFactoryBean bean = new SchedulerFactoryBean();
//
//        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
//        jobFactory.setApplicationContext(applicationContext);
//        schedulerFactoryBean.setJobFactory(jobFactory);
//        schedulerFactoryBean.setApplicationContext(applicationContext);
//
//        Properties properties = new Properties();
//
//        return bean;
//    }
}
