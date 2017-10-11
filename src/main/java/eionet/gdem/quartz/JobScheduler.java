package eionet.gdem.quartz;

import eionet.gdem.Properties;
import eionet.gdem.datadict.DDTablesCacheUpdater;
import eionet.gdem.logging.Markers;
import eionet.gdem.qa.WQCleanerJob;
import org.apache.commons.lang3.tuple.Pair;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.text.ParseException;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * ContextListener for initialising scheduled jobs with quartz.
 *
 */
@SuppressWarnings("unchecked")
@WebListener
public class JobScheduler implements ServletContextListener {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(JobScheduler.class);

    private Scheduler QUARTZ_SCHEDULER;
    private Scheduler QUARTZ_HEAVY_SCHEDULER;
    private Scheduler QUARTZ_LOCAL_SCHEDULER;

    private void init() {
        try {
            SchedulerFactory sf = new SchedulerFactory();
            QUARTZ_SCHEDULER = sf.getScheduler();
            QUARTZ_SCHEDULER.start();

            StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
            stdSchedulerFactory.initialize("quartz-heavy.properties");
            QUARTZ_HEAVY_SCHEDULER = sf.getScheduler();
            QUARTZ_HEAVY_SCHEDULER.start();

            StdSchedulerFactory localsf = new StdSchedulerFactory();
            localsf.initialize("local-quartz.properties");
            QUARTZ_LOCAL_SCHEDULER = sf.getScheduler();
            QUARTZ_LOCAL_SCHEDULER.start();
        } catch (SchedulerException e) {
            throw new ExceptionInInitializerError(e);
        }
    }


    /** holds the clustered quartz scheduler shared amongst instances*/
//    private static class QuartzSchedulerHolder {
//        private static final Scheduler QUARTZ_SCHEDULER;
//        static {
//            try {
//                SchedulerFactory schedFact = new StdSchedulerFactory();
//                QUARTZ_SCHEDULER = schedFact.getScheduler();
//                QUARTZ_SCHEDULER.start();
//            } catch (Exception e) {
//                throw new ExceptionInInitializerError(e);
//            }
//        }
//    }

    /** holds a clustered quartz scheduler shared amongst instances reserved for heavy jobs */
//    private static class QuartzHeavySchedulerHolder {
//        private static final Scheduler QUARTZ_HEAVY_SCHEDULER;
//        private static final String PROPERTIES_PATH = "quartz-heavy.properties";
//        static {
//            try {
//                StdSchedulerFactory sf = new StdSchedulerFactory();
//                sf.initialize(PROPERTIES_PATH);
//                QUARTZ_HEAVY_SCHEDULER = sf.getScheduler();
//                QUARTZ_HEAVY_SCHEDULER.start();
//            } catch (Exception e) {
//                throw new ExceptionInInitializerError(e);
//            }
//        }
//    }
    
    /** holds the in memory scheduled for private scheduling of each instance */
//    private static class QuartzLocalSchedulerHolder {
//        private static final Scheduler QUARTZ_LOCAL_SCHEDULER;
//        private static final String PROPERTIES_PATH = "local-quartz.properties";
//        static {
//            try {
//                StdSchedulerFactory sf = new StdSchedulerFactory();
//                sf.initialize(PROPERTIES_PATH);
//                QUARTZ_LOCAL_SCHEDULER = sf.getScheduler();
//                QUARTZ_LOCAL_SCHEDULER.start();
//            } catch (Exception e) {
//                throw new ExceptionInInitializerError(e);
//            }
//        }
//    }
        
//    public static Scheduler getQuartzScheduler() throws SchedulerException {
//        return QuartzSchedulerHolder.QUARTZ_SCHEDULER;
//    }
//
//    public static Scheduler getQuartzHeavyScheduler() throws SchedulerException {
//        return QuartzHeavySchedulerHolder.QUARTZ_HEAVY_SCHEDULER;
//    }

    private static Pair<Integer, JobDetail>[] intervalJobs;
    /**
     * Schedules interval job locally.
     * @param repeatInterval Repeat interval
     * @param jobDetails Job Details
     * @throws SchedulerException If an error occurs.
     * @throws ParseException If an error occurs.
     */
    public synchronized void scheduleLocalIntervalJob(int repeatInterval, JobDetail jobDetails) throws SchedulerException,
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
    public synchronized void scheduleCronJob(String cronExpression, JobDetail jobDetails) throws SchedulerException,
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
    public synchronized void scheduleIntervalJob(int repeatInterval, JobDetail jobDetails) throws SchedulerException,
    ParseException {

        SimpleTrigger trigger =
            newTrigger().withIdentity(jobDetails.getKey().getName(), jobDetails.getKey().getGroup()).startNow()
            .withSchedule(simpleSchedule().withIntervalInSeconds(repeatInterval).repeatForever()).build();

        QUARTZ_SCHEDULER.scheduleJob(jobDetails, trigger);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
            try {
                QUARTZ_SCHEDULER.shutdown(false);
                QUARTZ_LOCAL_SCHEDULER.shutdown(false);
                QUARTZ_HEAVY_SCHEDULER.shutdown(false);
                Thread.sleep(1000);
            } catch (SchedulerException e) {
                LOGGER.error("Failed to shutdown the scheduler", e);
            } catch (InterruptedException e) {
                LOGGER.error("Failed to shutdown the scheduler", e);
            }
    }

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent) {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        init();
        intervalJobs
                = new Pair[]{
                      Pair.of(new Integer(Properties.wqCleanInterval),
                            newJob(WQCleanerJob.class).withIdentity(WQCleanerJob.class.getSimpleName(),
                            WQCleanerJob.class.getName()).build())};
        // schedule interval jobs
        for (Pair<Integer, JobDetail> job : intervalJobs) {
            try {
                scheduleIntervalJob(job.getLeft(), job.getRight());
                LOGGER.debug(job.getRight().getKey().getName() + " scheduled, interval=" + job.getLeft());
            } catch (Exception e) {
                if (!(e instanceof ObjectAlreadyExistsException))  {
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
}
