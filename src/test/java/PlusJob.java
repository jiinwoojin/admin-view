import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class PlusJob implements Job {
    private int count = 0;
    private int based = 1;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        this.count += based;
        this.based += 1;
        System.out.println(String.format("[PLUS VALUE] %d", this.count));
    }
}
