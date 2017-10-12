package eionet.gdem.quartz;

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

    private final Scheduler scheduler;
    private static final Logger LOGGER = LoggerFactory.getLogger(JobSchedulerHelper.class);

    @Autowired
    public JobSchedulerHelper(@Qualifier("jobScheduler") Scheduler scheduler) {
        this.scheduler = scheduler;
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
