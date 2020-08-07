import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class QuartzExample01 {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        JobDetail job = newJob(PlusJob.class).build();
        Trigger trigger = newTrigger().build();

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
        Thread.sleep(2 * 1000);

        scheduler.shutdown();
    }
}
