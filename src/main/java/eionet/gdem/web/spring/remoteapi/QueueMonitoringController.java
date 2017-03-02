package eionet.gdem.web.spring.remoteapi;

import com.google.gson.Gson;
import eionet.gdem.services.MessageService;
import eionet.gdem.services.QueueJobsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *
 *
 */
@Controller
@RequestMapping("/queuemonitoringapi")
public class QueueMonitoringController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueueMonitoringController.class);
    private MessageService messageService;
    private final String TIME_SINCE_LATEST_JOB_EXECUTION = "minutes";

    private QueueJobsService queueJobsService;

    @Autowired
    public QueueMonitoringController(MessageService messageService, QueueJobsService queueJobsService) {
        this.messageService = messageService;
        this.queueJobsService = queueJobsService;
    }


    public ResponseEntity action(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HashMap<String, String> results = new HashMap<String, String>();

        try {
            String jobDetails = (queueJobsService.getLatestProcessingJobStartTime());

            if (jobDetails == "") {
                results.put(TIME_SINCE_LATEST_JOB_EXECUTION, "0");
                out.print(new Gson().toJson(results));
                return null;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date parsedDate = dateFormat.parse(jobDetails);
            Calendar calendar = Calendar.getInstance();
            long currentTimeInMilliseconds = calendar.getTimeInMillis();
            calendar.setTime(parsedDate);
            long latestJobStartTimeInMillis = calendar.getTimeInMillis();
            long diff = currentTimeInMilliseconds - latestJobStartTimeInMillis;
            long diffInMinutes = ((diff / (1000 * 60)) % 60);
            results.put(TIME_SINCE_LATEST_JOB_EXECUTION, Long.toString(diffInMinutes));
            out.print(new Gson().toJson(results));
        } catch (Exception e) {
            LOGGER.error("Unknown error: ", e);
            /*e.printStackTrace();*/
            /*response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);*/
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
