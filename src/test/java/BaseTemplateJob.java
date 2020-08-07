import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Queue;

import static org.quartz.TriggerBuilder.newTrigger;

public abstract class BaseTemplateJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        beforeExecute(context);
        doExecute(context);
        afterExecute(context);
        scheduleNextJob(context);
    }

    private void beforeExecute(JobExecutionContext context) {
        System.out.println("[선작업]");
    }

    protected abstract void doExecute(JobExecutionContext context);

    private void afterExecute(JobExecutionContext context) {
        System.out.println("[후작업] QUEUE 작업");
        Object object = context.getJobDetail().getJobDataMap().get("jobQueue");
        Queue<JobDetail> jobQueue = (Queue<JobDetail>) object;
        if (!jobQueue.isEmpty()) {
            jobQueue.poll();
        }
    }

    private void scheduleNextJob(JobExecutionContext context) {
        System.out.println("[다음작업]");
        Object object = context.getJobDetail().getJobDataMap().get("jobQueue");
        Queue<JobDetail> jobQueue = (Queue<JobDetail>) object;

        if (!jobQueue.isEmpty()) {
            JobDetail nextJobDetail = jobQueue.poll();
            nextJobDetail.getJobDataMap().put("jobQueue", jobQueue);
            Trigger current = newTrigger().startNow().build();

            try {
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                scheduler.start();
                scheduler.scheduleJob(nextJobDetail, current);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }
    }
}
