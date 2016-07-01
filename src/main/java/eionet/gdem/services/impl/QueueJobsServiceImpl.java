package eionet.gdem.services.impl;

import eionet.gdem.dto.WorkqueueJob;
import eionet.gdem.services.QueueJobsService;
import eionet.gdem.services.db.dao.IXQJobDao;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@Service("queueJobsService")
public class QueueJobsServiceImpl implements QueueJobsService {

    private IXQJobDao ixqJobDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueJobsServiceImpl.class);

    @Autowired
    public QueueJobsServiceImpl(@Qualifier("xqJobDao") IXQJobDao ixqJobDao) {
        this.ixqJobDao = ixqJobDao;
    }

    @Override
    public Date getLatestProcessingJobStartTime() throws SQLException, ParseException {
        WorkqueueJob job = ixqJobDao.getMostRecentProcessingJob();
        Date date = null;
        if (job == null) {
            return date;
        }
        return job.getJobTimestamp();
    }

    @Override
    public String[] getJobById(String jobId) throws SQLException {
        return ixqJobDao.getXQJobData(jobId);
    }

}
