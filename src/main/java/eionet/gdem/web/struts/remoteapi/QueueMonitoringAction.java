package eionet.gdem.web.struts.remoteapi;

import com.google.gson.Gson;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.services.QueueJobsService;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
public class QueueMonitoringAction extends Action {

    private final String TIME_SINCE_LATEST_JOB_EXECUTION = "minutes";

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        QueueJobsService queueJobsService = (QueueJobsService) SpringApplicationContext.getBean("queueJobsService");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HashMap<String, String> results = new HashMap<String, String>();

        try {
            Date jobStartingDate = (queueJobsService.getLatestProcessingJobStartTime());

            if (jobStartingDate == null) {
                results.put(TIME_SINCE_LATEST_JOB_EXECUTION, "0");
                out.print(new Gson().toJson(results));
                return null;
            }

            Calendar calendar = Calendar.getInstance();
            long currentTimeInMilliseconds = calendar.getTimeInMillis();
            calendar.setTime(jobStartingDate);
            long latestJobStartTimeInMillis = calendar.getTimeInMillis();
            long diff = currentTimeInMilliseconds - latestJobStartTimeInMillis;
           long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            results.put(TIME_SINCE_LATEST_JOB_EXECUTION, Long.toString(diffInMinutes));
            out.print(new Gson().toJson(results));
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return null;
        }
    }

}
