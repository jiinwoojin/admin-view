package com.jiin.admin.website.view.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SeedInitJob implements Job {
    // 여기서 Spring Bean 을 굳이 사용하지 않더라도 접근하는 방법이 있을까?
    // Seeding 은 Docker 실행 중인 Container 찾고 계속 체크하면 끝이긴 한데...

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("-- SEED INIT JOB EXECUTE --");
    }
}
