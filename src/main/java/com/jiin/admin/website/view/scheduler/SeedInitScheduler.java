package com.jiin.admin.website.view.scheduler;

import com.jiin.admin.Constants;
import com.jiin.admin.website.util.YAMLFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class SeedInitScheduler {
    @Value("${project.data-path}")
    private String dataPath;

    private SchedulerFactory schedulerFactory;
    private Scheduler scheduler;

    @PostConstruct
    public void start() throws SchedulerException {
        this.schedulerFactory = new StdSchedulerFactory();
        this.scheduler = this.schedulerFactory.getScheduler();
        scheduler.start();

        String cronScheduleFileDir = String.format("%s%s/%s", dataPath, Constants.SERVER_INFO_FILE_PATH, Constants.CRON_SCHEDULING_MANAGEMENT_FILE);
        File file = new File(cronScheduleFileDir);

        String cronValue = Constants.DEFAULT_CRON_SCHEDULE;
        if (file.exists()) {
            try {
                Map<String, Object> cronMap = YAMLFileUtil.fetchMapByYAMLFile(file);
                cronValue = String.format("%s %s %s %s %s %s", cronMap.get("sec"), cronMap.get("min"), cronMap.get("hr"), cronMap.get("day"), cronMap.get("mon"), cronMap.get("week"));
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            }

            System.out.println(cronValue);

            JobDetail job = JobBuilder.newJob(SeedInitJob.class).build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronValue)).build();

            scheduler.scheduleJob(job, trigger);
        }
    }
}
