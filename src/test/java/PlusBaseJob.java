import org.quartz.JobExecutionContext;

public class PlusBaseJob extends BaseJob {
    private int count = 0;
    private int based = 1;

    @Override
    protected void doExecute(JobExecutionContext context) {
        this.count += based;
        this.based += 1;
        System.out.println(String.format("[PLUS VALUE BY %s] %d", context.getJobDetail().getJobDataMap().get("jobName").toString(), this.count));
    }
}
