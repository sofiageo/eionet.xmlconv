package eionet.gdem.qa;

import java.util.Calendar;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.exceptions.DCMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * QA Service workqueue cleaner job. Deletes all jobs with status = (READY, FATAL_ERR) and finished more than 24 hours ago
 * (parameter in gdem.properties).
 *
 */
public class WQCleanerJob implements Job {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WQCleanerJob.class);
    /** Dao for getting job data. */
    private WorkqueueManager workqueueManager;
    /*private WorkqueueManager jobsManager = new WorkqueueManager();*/

    @Autowired
    public WQCleanerJob(WorkqueueManager workqueueManager) {
        this.workqueueManager = workqueueManager;
    }

    /*
         * (non-Javadoc)
         *
         * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
         */
    @Override
    public void execute(JobExecutionContext paramJobExecutionContext) throws JobExecutionException {

        LOGGER.debug("RUN WQCleanerJob.");
        try {
            List<WorkqueueJob> jobs = workqueueManager.getFinishedJobs();

            if (jobs != null) {
                for (WorkqueueJob job : jobs) {
                    if (canDeleteJob(job)) {
                        workqueueManager.endXQJob(job);
                    }
                }
            }
        } catch (DCMException e) {
            LOGGER.error("Error when running work-queue clearner job: ", e);
        }
    }

    /**
     * Check the job's age and return true if it is possible to delete it.
     *
     * @param job
     *            Workqueue job object
     * @return true if job can be deleted.
     */
    public static boolean canDeleteJob(WorkqueueJob job) {
        boolean canDelete = false;
        if (job != null && job.getJobTimestamp() != null && job.getStatus() >= Constants.XQ_READY) {
            Calendar now = Calendar.getInstance();
            int maxAge = Properties.wqJobMaxAge == 0 ? -1 : -Properties.wqJobMaxAge;
            now.add(Calendar.HOUR, maxAge);
            Calendar jobCal = Calendar.getInstance();
            jobCal.setTime(job.getJobTimestamp());
            if (now.after(jobCal)) {
                canDelete = true;
            }
        }
        return canDelete;
    }
}
