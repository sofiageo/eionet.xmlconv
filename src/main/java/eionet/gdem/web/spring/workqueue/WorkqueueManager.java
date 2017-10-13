package eionet.gdem.web.spring.workqueue;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.exceptions.XMLConvException;
import eionet.gdem.qa.QAService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import static eionet.gdem.quartz.JobSchedulerHelper.getQuartzHeavyScheduler;
import static eionet.gdem.quartz.JobSchedulerHelper.getQuartzScheduler;

/**
 * Work Queue Manager.
 *
 */
@Service
public class WorkqueueManager {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkqueueManager.class);
    /** Dao for getting job data. */
    private IXQJobDao jobDao;
    private QAService qaService;
    /*private static IXQJobDao jobDao = GDEMServices.getDaoService().getXQJobDao();*/

    @Autowired
    public WorkqueueManager(IXQJobDao jobDao, QAService qaService) {
        this.jobDao = jobDao;
        this.qaService = qaService;
    }

    /**
     * Get work-queue job data.
     *
     * @param jobId
     *            Job unique ID in DB.
     * @return WorkqueueJob object.
     * @throws DCMException
     *             Database exception occured.
     */
    public WorkqueueJob getWqJob(String jobId) throws DCMException {
        WorkqueueJob job = null;
        try {
            String[] jobData = jobDao.getXQJobData(jobId);
            job = parseJobData(jobData);
        } catch (Exception e) {
            LOGGER.error("Error getting workqueue job", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return job;
    }

    /**
     * Adds a new jobs into the workqueue using script content sent as the method parameter
     *
     * @param user
     *            Logged in user name.
     * @param sourceUrl
     *            Source file URL.
     * @param scriptContent
     *            Script content to be stored in workqueue.
     * @param scriptType
     *            Script title.
     * @return Job ID.
     * @throws DCMException If an error occurs.
     */
    public String addQAScriptToWorkqueue(String user, String sourceUrl, String scriptContent, String scriptType) throws DCMException {

        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "i")) {
                LOGGER.debug("You don't have permissions to add jobs into workqueue!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
            }

        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        try {
            String result = qaService.addJob(sourceUrl, scriptContent, scriptType);
            return result;
        } catch (Exception e) {
            LOGGER.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }

    }

    /**
     * Adds new jobs into the workqueue by the given XML Schema
     *
     * @param user
     *            Loggedin user name.
     * @param sourceUrl
     *            Source URL of XML file.
     * @param schemaUrl
     *            XML Schema URL.
     * @return List of job IDs.
     * @throws DCMException If an error occurs.
     */
    public List<String> addSchemaScriptsToWorkqueue(String user, String sourceUrl, String schemaUrl) throws DCMException {

        List<String> result = new ArrayList<String>();
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_WQ_PATH, "i")) {
                LOGGER.debug("You don't have permissions jobs into workqueue!");
                throw new DCMException(BusinessConstants.EXCEPTION_AUTORIZATION_QASCRIPT_UPDATE);
            }

        } catch (DCMException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        try {
            Hashtable h = new Hashtable();
            Vector files = new Vector();
            files.add(sourceUrl);
            h.put(schemaUrl, files);
            Vector v_result = qaService.analyzeXMLFiles(h);
            if (v_result != null) {
                for (int i = 0; i < v_result.size(); i++) {
                    Vector v = (Vector) v_result.get(i);
                    result.add((String) v.get(0));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error adding job to workqueue", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return result;
    }

    /**
     * Gets finished jobs.
     * @return List of finish jobs
     * @throws DCMException If an error occurs.
     */
    public List<WorkqueueJob> getFinishedJobs() throws DCMException {
        List<WorkqueueJob> jobs = new ArrayList<WorkqueueJob>();
        try {
            String[][] jobsData = jobDao.getXQFinishedJobs();
            if (jobsData != null && jobsData.length > 0) {
                for (String[] jobData : jobsData) {
                    if (jobData != null) {
                        jobs.add(parseJobData(jobData));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error getting finished workqueue jobs", e);
            throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
        }
        return jobs;

    }

    /**
     * Parses Job data
     * @param jobData Job data
     * @return Job
     * @throws ParseException If an error occurs.
     */
    private WorkqueueJob parseJobData(String[] jobData) throws ParseException {
        WorkqueueJob job = null;
        if (jobData != null && jobData.length > 4) {
            job = new WorkqueueJob();
            job.setUrl((jobData[0] == null) ? "" : jobData[0]);
            job.setScriptFile((jobData[1] == null) ? "" : jobData[1]);
            job.setResultFile((jobData[2] == null) ? "" : jobData[2]);
            job.setStatus((jobData[3] == null) ? 0 : new Integer(jobData[3]));
            job.setSrcFile((jobData[4] == null) ? "" : jobData[4]);
            job.setScriptId((jobData[5] == null) ? "" : jobData[5]);
            job.setJobId((jobData[6] == null) ? "" : jobData[6]);
            job.setJobTimestamp(Utils.parseDate(jobData[7], "yyyy-MM-dd HH:mm:ss"));
        }
        return job;
    }

    /**
     * Remove the job from the queue and delete temporary files.
     *
     * @param job Work queue job
     * @throws DCMException If an error occurs.
     */
    public void endXQJob(WorkqueueJob job) throws DCMException {
        // remove the job from the queue / DB when the status won't change= FATAL or READY
        try {
            jobDao.endXQJob(job.getJobId());
            LOGGER.info("Delete expired job: " + job.getJobId());
        } catch (SQLException sqle) {
            throw new DCMException("Error getting XQJob data from DB: " + sqle.toString());
        }
        // delete files only, if debug is not enabled
        if (!LOGGER.isDebugEnabled()) {
            // delete the result from filesystem
            String resultFile = job.getResultFile();
            try {
                Utils.deleteFile(resultFile);
            } catch (Exception e) {
                LOGGER.error("Could not delete job result file: " + resultFile + "." + e.getMessage());
            }
            // delete XQuery file, if it is stored in tmp folder
            String xqFile = job.getScriptFile();
            try {
                // Important!!!: delete only, when the file is stored in tmp folder
                if (xqFile.startsWith(Properties.tmpFolder)) {
                    Utils.deleteFile(xqFile);
                }
            } catch (Exception e) {
                LOGGER.error("Could not delete job result file: " + xqFile + "." + e.getMessage());
            }
        }
    }

    /**
     * Reset active jobs on startup.
     */
    public void resetActiveJobs() {
        try {
            jobDao.changeJobStatusByStatus(Constants.XQ_DOWNLOADING_SRC, Constants.XQ_RECEIVED);
            jobDao.changeJobStatusByStatus(Constants.XQ_PROCESSING, Constants.XQ_RECEIVED);
        } catch (Exception e) {
            LOGGER.error("Error reseting active jobs: " + e.toString());
        }
    }

    /**
     * Restart jobs by id.
     */
    public void restartJobs(String[] jobIds) throws XMLConvException {
        LOGGER.info("Request to restart jobs " + Utils.stringArray2String(jobIds, ","));
        List<String> jobsToRestart = new ArrayList<>();
        try{
            if (jobIds.length > 0) {
                for (String jobId : jobIds) {
                    String[] jobData = jobDao.getXQJobData(jobId);
                    if (jobData == null || jobData.length < 3) {
                        continue;
                    }
                    // check if job is running
                    JobKey qJob = new JobKey(jobId, "XQueryJob");
                    if ("2".equals(jobData[3])) {
                        if (getQuartzScheduler().checkExists(qJob)) {
                            try {
                                if (getQuartzScheduler().checkExists(qJob))
                                    // try to interrupt running job
                                    getQuartzScheduler().interrupt(qJob);
                                else if (getQuartzHeavyScheduler().checkExists(qJob))
                                    // try to interrupt running job
                                    getQuartzHeavyScheduler().interrupt(qJob);
                            } catch (UnableToInterruptJobException e) {
                                LOGGER.info("Job with ID: " + jobId + " is running and cannot be interrupted and thus cannot be restarted");
                                continue;
                            }
                        } else {
                            continue;
                        }
                    }
                    jobsToRestart.add(jobId);
                }
                //
                jobIds = new String[ jobsToRestart.size() ];
                jobsToRestart.toArray(jobIds);
                // Change the jobs' status
                jobDao.changeXQJobsStatuses(jobIds, Constants.XQ_RECEIVED);
                LOGGER.info("Jobs restarted: " + Utils.stringArray2String(jobIds, "," ));
                for (String jobId : jobIds) {
                    // and reschedule each job
                    qaService.rescheduleJob(jobId);
                }
            }
        }
        catch (Exception e) {
            throw new XMLConvException(e.getMessage());
        }
    }

    /**
     * Delete jobs by id.
     */
    public void deleteJobs(String[] jobIds) throws XMLConvException {
        LOGGER.info("Request to deleteJobs jobs " + Utils.stringArray2String(jobIds, "," ) );
        try {
            List<String> jobsToDelete = new ArrayList<>();

            if (jobIds.length > 0) {
                try {
                    for (String jobId : jobIds) {
                        String[] jobData = jobDao.getXQJobData(jobId);
                        if (jobData == null || jobData.length < 3) {
                            continue;
                        }
                        JobKey qJob = new JobKey(jobId, "XQueryJob");
                        if ("2".equals(jobData[3])) {
                            try {
                                if (getQuartzScheduler().checkExists(qJob))
                                // try to interrupt running job
                                    getQuartzScheduler().interrupt(qJob);
                                else if (getQuartzHeavyScheduler().checkExists(qJob))
                                    // try to interrupt running job
                                    getQuartzHeavyScheduler().interrupt(qJob);
                            } catch (UnableToInterruptJobException e) {
                                jobDao.markDeleted(jobId);
                                LOGGER.info("Job with ID: " + jobId + " is running and cannot be interrupted and thus cannot be deleted");
                                continue;
                            }
                        }

                        jobsToDelete.add(jobId);
                        // delete also result files from file system tmp folder
                        String resultFile = jobData[2];
                        try {
                            Utils.deleteFile(resultFile);
                        } catch (Exception e) {
                            LOGGER.error("Could not delete job result file: " + resultFile + "." + e.getMessage());
                        }
                        // delete xquery files, if they are stored in tmp folder
                        String xqFile = jobData[1];
                        try {
                            // Important!!!: delete only, when the file is stored in tmp folder
                            if (xqFile.startsWith(Properties.tmpFolder)) {
                                Utils.deleteFile(xqFile);
                            }
                        } catch (Exception e) {
                            LOGGER.error("Could not delete XQuery script file: " + xqFile + "." + e.getMessage());
                        }
                    }
                }
                catch (Exception e) {
                    LOGGER.error("Could not delete job result files!" + e.getMessage());
                }
                jobIds = new String[ jobsToDelete.size() ];
                jobsToDelete.toArray(jobIds);
                jobDao.endXQJobs(jobIds);
                LOGGER.info("Jobs deleted: " + Utils.stringArray2String(jobIds, ","));
            }

        } catch (Exception e) {
            throw new XMLConvException(e.getMessage());
        }
    }
}
