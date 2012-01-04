package eionet.gdem.services.db.dao;

import java.sql.SQLException;

public interface IXQJobDao extends IDbSchema {

    /**
     * Gets information about the received job in Workqueue.
     *
     * @param String
     *            jobId @ return String[]
     */
    String[] getXQJobData(String jobId) throws SQLException;

    /**
     * Creates a new job in the queue XQ Script is saved earlier in the db.
     *
     * @param String
     *            url, String xqFile, String resultFile
     * @param String
     *            xqID - query id from db
     */
    String startXQJob(String url, String xqFile, String resultFile) throws SQLException;

    String startXQJob(String url, String xqFile, String resultFile, int xqID) throws SQLException;

    /**
     * Changes the status of the job in the table also changes the time_stamp showing when the new task was started.
     */
    void changeJobStatus(String jobId, int status) throws SQLException;

    /**
     * Changes the status of the jobs in the table and sets the downloaded file local src THe jobs should have the sam source url.
     * also changes the time_stamp showing when the new task was started
     */
    void changeFileJobsStatus(String url, String savedFile, int status) throws SQLException;

    /**
     * Returns job IDs in the Workqueue with the given status.
     *
     * @return String[]
     */
    String[] getJobs(int status) throws SQLException;

    /**
     * Returns job IDs in the Workqueue with the given status and limits the rows with the given limit.
     *
     * @return String[]
     */
    String[] getJobsLimit(int status, int max_rows) throws SQLException;

    /**
     * Removes the XQJob No checking performed by this method.
     */
    void endXQJob(String jobId) throws SQLException;

    /**
     * Removes the XQJobs. No checking performed by this method
     */
    void endXQJobs(String[] jobIds) throws SQLException;

    /**
     * changes the job status for several jobs. No checking performed by this method
     */
    void changeXQJobsStatuses(String[] jobIds, int status) throws SQLException;

    /**
     * returns all the job data in the WQ table.
     *
     * @return String[][] containing all fields as HashMap from T_CONVTYPE table
     */

    String[][] getJobData() throws SQLException;

    /**
     * Removes the XQJobs. No checking performed by this method
     */
    void changeJobStatusByStatus(int currentStatus, int newStatus) throws SQLException;

    /**
     * Countr the active jobs (N_STATUS=1 or 2) in DB.
     *
     * @return
     * @throws SQLException
     */
    int countActiveJobs() throws SQLException;

    /**
     * Get all finished jobs from DB.
     *
     * @return Job data.
     * @throws SQLException
     *             DB error occurred.
     */
    String[][] getXQFinishedJobs() throws SQLException;
}
