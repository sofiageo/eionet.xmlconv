package eionet.gdem.deprecated;

import eionet.acl.AppUser;
import eionet.gdem.Constants;
import eionet.gdem.dcm.business.WorkqueueManager;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Handler of storing methods for the GDEM.
 * @author Unknown
 * @author George Sofianos
 */
@Service
public class SaveHandler {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveHandler.class);
    private WorkqueueManager workqueueManager;
    /*private static final WorkqueueManager workqueueManager = new WorkqueueManager();*/

    @Autowired
    public SaveHandler(WorkqueueManager workqueueManager) {
        this.workqueueManager = workqueueManager;
    }

    /**
     * Handles work queue
     * @param req Servlet request
     * @param action Action
     */
    public void handleWorkqueue(HttpServletRequest req, String action) {
        AppUser user = SecurityUtil.getUser(req, Constants.USER_ATT);
        String user_name = null;
        if (user != null) {
            user_name = user.getUserName();
        }


        if (action.equals(Constants.WQ_DEL_ACTION)) {
            try {
                if (!SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_WQ_PATH, "d")) {
                    req.setAttribute(Constants.ERROR_ATT, "You don't have permissions to delete jobs!");
                    return;
                }
            } catch (Exception e) {
                req.setAttribute(Constants.ERROR_ATT, "Cannot read permissions: " + e.toString());
                return;
            }

            StringBuffer err_buf = new StringBuffer();
            // String del_id = (String)req.getParameter("ID");
            String[] jobs = req.getParameterValues("jobID");

            try {
                workqueueManager.deleteJobs(jobs);
            } catch (Exception e) {
                LOGGER.error("Could not delete jobs!" + e.getMessage());
                err_buf.append("Cannot delete job: " + e.toString());
            }

            if (err_buf.length() > 0) {
                req.setAttribute(Constants.ERROR_ATT, err_buf.toString());
            }
        } else if (action.equals(Constants.WQ_RESTART_ACTION)) {
            try {
                if (!SecurityUtil.hasPerm(user_name, "/" + Constants.ACL_WQ_PATH, "u")) {
                    req.setAttribute(Constants.ERROR_ATT, "You don't have permissions to restart the jobs!");
                    return;
                }
            } catch (Exception e) {
                req.setAttribute(Constants.ERROR_ATT, "Cannot read permissions: " + e.toString());
                return;
            }

            StringBuffer err_buf = new StringBuffer();
            String[] jobs = req.getParameterValues("jobID");

            try {
                workqueueManager.restartJobs(jobs);

            } catch (Exception e) {
                LOGGER.error("Could not restart jobs!" + e.getMessage());
                err_buf.append("Cannot restart jobs: " + e);
            }
            if (err_buf.length() > 0) {
                req.setAttribute(Constants.ERROR_ATT, err_buf.toString());
            }
        }
    }
}
