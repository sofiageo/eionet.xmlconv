package eionet.gdem.services;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public interface QueueJobsService {

    public String[] getJobById(String jobId)throws SQLException;
    
    public Date getLatestProcessingJobStartTime() throws SQLException, ParseException;
}
