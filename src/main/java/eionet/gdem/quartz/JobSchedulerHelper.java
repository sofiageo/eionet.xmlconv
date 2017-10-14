package eionet.gdem.quartz;

import eionet.gdem.Properties;
import eionet.gdem.datadict.DDTablesCacheUpdater;
import eionet.gdem.logging.Markers;
import eionet.gdem.web.spring.workqueue.WQCleanerJob;
import org.apache.commons.lang3.tuple.Pair;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 *
 */
@Component
public class JobSchedulerHelper {

    private static Scheduler QUARTZ_SCHEDULER;
    private static Scheduler QUARTZ_HEAVY_SCHEDULER;
    private static Scheduler QUARTZ_LOCAL_SCHEDULER;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSchedulerHelper.class);

    private static Pair<Integer, JobDetail>[] intervalJobs;

    @Autowired
    public JobSchedulerHelper(@Qualifier("jobScheduler") Scheduler jobScheduler,@Qualifier("jobScheduler")  Scheduler heavyJobScheduler,@Qualifier("jobScheduler") Scheduler localJobScheduler) {
        QUARTZ_SCHEDULER = jobScheduler;
        QUARTZ_LOCAL_SCHEDULER = heavyJobScheduler;
        QUARTZ_HEAVY_SCHEDULER = localJobScheduler;
    }

    //    @Autowired
//    public JobScheduler(Scheduler QUARTZ_SCHEDULER, Scheduler QUARTZ_HEAVY_SCHEDULER, Scheduler QUARTZ_LOCAL_SCHEDULER) {
//        this.QUARTZ_SCHEDULER = QUARTZ_SCHEDULER;
//        this.QUARTZ_HEAVY_SCHEDULER = QUARTZ_HEAVY_SCHEDULER;
//        this.QUARTZ_LOCAL_SCHEDULER = QUARTZ_LOCAL_SCHEDULER;
//    }

//    @PostConstruct
//    public void init() {
//        try {
//            QUARTZ_SCHEDULER = scheduler;
//            QUARTZ_SCHEDULER.start();
//
//            QUARTZ_HEAVY_SCHEDULER = scheduler;
//            QUARTZ_HEAVY_SCHEDULER.start();
//
//            QUARTZ_LOCAL_SCHEDULER = scheduler;
//            QUARTZ_LOCAL_SCHEDULER.start();
//        } catch (SchedulerException e) {
//            throw new ExceptionInInitializerError(e);
//        }
//    }

    /**
     * Schedules interval job locally.
     * @param repeatInterval Repeat interval
     * @param jobDetails Job Details
     * @throws SchedulerException If an error occurs.
     * @throws ParseException If an error occurs.
     */
    public static synchronized void scheduleLocalIntervalJob(int repeatInterval, JobDetail jobDetails) throws SchedulerException,
            ParseException {

        SimpleTrigger trigger =
                newTrigger().withIdentity(jobDetails.getKey().getName(), jobDetails.getKey().getGroup()).startNow()
                        .withSchedule(simpleSchedule().withIntervalInSeconds(repeatInterval).repeatForever()).build();

        QUARTZ_LOCAL_SCHEDULER.scheduleJob(jobDetails, trigger);
    }
    /**
     * Schedules cron job.
     * @param cronExpression Cron expression
     * @param jobDetails Job details
     * @throws SchedulerException If an error occurs.
     * @throws ParseException If an error occurs.
     */
    public static synchronized void scheduleCronJob(String cronExpression, JobDetail jobDetails) throws SchedulerException,
            ParseException {

        Trigger trigger =
                newTrigger().withIdentity(jobDetails.getKey().getName(), jobDetails.getKey().getGroup())
                        .withSchedule(cronSchedule(cronExpression)).forJob(jobDetails.getKey()).build();

        QUARTZ_SCHEDULER.scheduleJob(jobDetails, trigger);
    }

    /**
     * Schedules interval job.
     * @param repeatInterval Repeat interval
     * @param jobDetails Job Details
     * @throws SchedulerException If an error occurs.
     * @throws ParseException If an error occurs.
     */
    public static synchronized void scheduleIntervalJob(int repeatInterval, JobDetail jobDetails) throws SchedulerException,
            ParseException {

        SimpleTrigger trigger =
                newTrigger().withIdentity(jobDetails.getKey().getName(), jobDetails.getKey().getGroup()).startNow()
                        .withSchedule(simpleSchedule().withIntervalInSeconds(repeatInterval).repeatForever()).build();

        QUARTZ_SCHEDULER.scheduleJob(jobDetails, trigger);
    }

    public static Scheduler getQuartzScheduler() throws SchedulerException {
        return QUARTZ_SCHEDULER;
    }

    public static Scheduler getQuartzHeavyScheduler() throws SchedulerException {
        return QUARTZ_HEAVY_SCHEDULER;
    }

    @PostConstruct
    public void init() {
        intervalJobs
                = new Pair[]{
                Pair.of(new Integer(Properties.wqCleanInterval),
                        newJob(WQCleanerJob.class).withIdentity(WQCleanerJob.class.getSimpleName(),
                                WQCleanerJob.class.getName()).build())};
        // schedule interval jobs
        for (Pair<Integer, JobDetail> job : intervalJobs) {

            try {
                getQuartzHeavyScheduler(); // initialize the heavy scheduler
                scheduleIntervalJob(job.getLeft(), job.getRight());
                LOGGER.debug(job.getRight().getKey().getName() + " scheduled, interval=" + job.getLeft());
            } catch (Exception e) {
                if (!(e instanceof org.quartz.ObjectAlreadyExistsException))  {
                    LOGGER.error(Markers.FATAL, "Error when scheduling " + job.getRight().getKey().getName(), e);
                }
            }
        }
        try {
            // DDTablesCacheUpdater is scheduled locally
            scheduleLocalIntervalJob(Properties.ddTablesUpdateInterval, newJob(DDTablesCacheUpdater.class).withIdentity(DDTablesCacheUpdater.class.getSimpleName(),
                    DDTablesCacheUpdater.class.getName()).build());
        } catch (Exception e) {
            LOGGER.error(Markers.FATAL, "Error when scheduling DDTablesCacheUpdater", e);
        }
    }

//    public static synchronized void shutdown() {
//        try {
//            QUARTZ_SCHEDULER.shutdown(false);
//            QUARTZ_LOCAL_SCHEDULER.shutdown(false);
//            QUARTZ_HEAVY_SCHEDULER.shutdown(false);
//            Thread.sleep(1000);
//        } catch (SchedulerException e) {
//            LOGGER.error("Failed to shutdown the scheduler", e);
//        } catch (InterruptedException e) {
//            LOGGER.error("Failed to shutdown the scheduler", e);
//        }
//    }
}
