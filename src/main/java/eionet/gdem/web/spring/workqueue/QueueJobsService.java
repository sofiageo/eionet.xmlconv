package eionet.gdem.web.spring.workqueue;

import java.sql.SQLException;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public interface QueueJobsService {

    public String[] getJobById(String jobId)throws SQLException;
    
    public String getLatestProcessingJobStartTime() throws SQLException;
}
