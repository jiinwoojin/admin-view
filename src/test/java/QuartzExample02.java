import org.quartz.JobDataMap;
import org.quartz.JobDetail;

import java.util.LinkedList;
import java.util.Queue;

import static org.quartz.JobBuilder.newJob;

public class QuartzExample02 {
    public static void main(String[] args) {
        JobDataMap map1 = new JobDataMap();
        map1.put("jobName", "jobChain1");

        JobDataMap map2 = new JobDataMap();
        map1.put("jobName", "jobChain2");

        JobDataMap map3 = new JobDataMap();
        map1.put("jobName", "jobChain3");

        JobDetail detail1 = newJob(PlusBaseJob.class)
                .usingJobData(map1)
                .build();

        JobDetail detail2 = newJob(PlusBaseJob.class)
                .usingJobData(map2)
                .build();

        JobDetail detail3 = newJob(PlusBaseJob.class)
                .usingJobData(map3)
                .build();

        Queue<JobDetail> queue = new LinkedList<>();
        queue.add(detail1);
        queue.add(detail2);
        queue.add(detail3);

        detail1.getJobDataMap().put("jobQueue", queue);

    }
}
