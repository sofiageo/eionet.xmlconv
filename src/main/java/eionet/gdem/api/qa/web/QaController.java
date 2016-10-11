package eionet.gdem.api.qa.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import eionet.gdem.Constants;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.errors.EmptyParameterException;
import eionet.gdem.api.errors.QaServiceException;
import eionet.gdem.api.qa.model.EnvelopeWrapper;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.qa.XQueryService;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@RestController
public class QaController {

    private final QaService qaService;

    private static final Logger LOGGER = LoggerFactory.getLogger(QaController.class);

    @Autowired
    public QaController(QaService qaService) {
        this.qaService = qaService;
    }

    /**
     * Synchronous QA for a single file
     *
     */
    @RequestMapping(value = "/qajobs", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<HashMap<String, String>> performInstantQARequestOnFile(@RequestBody EnvelopeWrapper envelopeWrapper) throws QaServiceException, EmptyParameterException, UnsupportedEncodingException {

        if (envelopeWrapper.getSourceUrl() == null) {
            throw new EmptyParameterException("sourceUrl");
        }
        if (envelopeWrapper.getScriptId() == null) {
            throw new EmptyParameterException("scriptId");
        }

        Vector results = qaService.runQaScript(envelopeWrapper.getSourceUrl(), envelopeWrapper.getScriptId());

        LinkedHashMap<String, String> jsonResults = new LinkedHashMap<String, String>();

        jsonResults.put("feedbackStatus", ConvertByteArrayToString((byte[]) results.get(2)));
        jsonResults.put("feedbackMessage", ConvertByteArrayToString((byte[]) results.get(3)));
        jsonResults.put("feedbackContentType", results.get(0).toString());
        jsonResults.put("feedbackContent", ConvertByteArrayToString((byte[]) results.get(1)));

        return new ResponseEntity<HashMap<String, String>>(jsonResults, HttpStatus.OK);
    }

    /**
     *
     * Asynchronous QA for a Single File
     *
     */
    @RequestMapping(value = "/asynctasks/qajobs")
    public void scheduleQARequestOnFile(@RequestBody EnvelopeWrapper envelopeWrapper) throws QaServiceException, EmptyParameterException, UnsupportedEncodingException {

        throw new UnsupportedOperationException("asynchronous QA for a Single file is not supported yet");
    }

    /**
     * Schedule a Qa Job for an Envelope
     *
     */
    @RequestMapping(value = "/asynctasks/qajobs/batch", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<LinkedHashMap<String, List<QaResultsWrapper>>> scheduleQaRequestOnEnvelope(@RequestBody EnvelopeWrapper envelopeWrapper) throws QaServiceException, EmptyParameterException, JsonProcessingException {

        if (envelopeWrapper.getEnvelopeUrl() == null) {
            throw new EmptyParameterException("envelopeUrl");
        }
        List<QaResultsWrapper> qaResults = qaService.scheduleJobs(envelopeWrapper.getEnvelopeUrl());

        LinkedHashMap<String, List<QaResultsWrapper>> jobsQaResults = new LinkedHashMap<String, List<QaResultsWrapper>>();

        jobsQaResults.put("jobs", qaResults);

        return new ResponseEntity<LinkedHashMap<String, List<QaResultsWrapper>>>(jobsQaResults, HttpStatus.OK);
    }

    /**
     * Authorized Schedule of a Qa Job for an Envelope
     *
     */
    @RequestMapping(value = "/auth/asynctasks/qajobs/batch", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<LinkedHashMap<String, List<QaResultsWrapper>>> SecuredScheduleQaRequestOnEnvelope(@RequestBody EnvelopeWrapper envelopeWrapper) throws QaServiceException, EmptyParameterException {

        if (envelopeWrapper.getEnvelopeUrl() == null) {
            throw new EmptyParameterException("envelopeUrl");
        }
        List<QaResultsWrapper> qaResults = qaService.scheduleJobs(envelopeWrapper.getEnvelopeUrl());

        LinkedHashMap<String, List<QaResultsWrapper>> jobsQaResults = new LinkedHashMap<String, List<QaResultsWrapper>>();

        jobsQaResults.put("jobs", qaResults);

        return new ResponseEntity<LinkedHashMap<String, List<QaResultsWrapper>>>(jobsQaResults, HttpStatus.OK);
    }

    /**
     *Get QA Job Status 
     **/
    @RequestMapping(value = "/asynctasks/qajobs/{jobId}", method = RequestMethod.GET)
    public ResponseEntity<LinkedHashMap<String, String>> getQAResultsForJob(@PathVariable String jobId) throws XMLConvException {

        XQueryService xqueryService = new XQueryService();
        Hashtable<String,String> results = xqueryService.getResult(jobId);
        
        LinkedHashMap<String, String> jsonResults = new LinkedHashMap<String, String>();

        jsonResults.put("executionStatus", results.get(Constants.RESULT_VALUE_PRM));
        jsonResults.put("feedbackStatus",  results.get(Constants.RESULT_FEEDBACKSTATUS_PRM));
        jsonResults.put("feedbackMessage", results.get(Constants.RESULT_FEEDBACKMESSAGE_PRM));
        jsonResults.put("feedbackContentType",   results.get(Constants.RESULT_METATYPE_PRM));
        jsonResults.put("feedbackContent",   results.get(Constants.RESULT_SCRIPTTITLE_PRM));

        return new ResponseEntity<LinkedHashMap<String, String>>(jsonResults, HttpStatus.OK);
    }

    @RequestMapping(value = "/queries", method = RequestMethod.GET)
    public ResponseEntity<Vector> listQeuries(@RequestParam String schema) throws XMLConvException {

        XQueryService xqueryService = new XQueryService();
        Vector results = xqueryService.listQueries(schema);

        return new ResponseEntity<Vector>(results, HttpStatus.OK);
    }

    
        @RequestMapping(value = "/qascripts", method = RequestMethod.GET)
    public ResponseEntity<Vector> listQaScripts(@RequestParam String schema) throws XMLConvException {

        XQueryService xqueryService = new XQueryService();
       
        Vector results = xqueryService.listQAScripts(schema);

        return new ResponseEntity<Vector>(results, HttpStatus.OK);
    }
    
    @ExceptionHandler(QaServiceException.class)
    public void HandleQaServiceException(Exception exception, HttpServletResponse response) {
        exception.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmptyParameterException.class)
    public ResponseEntity<HashMap<String, String>> HandleEmptyParameterException(Exception exception) {
        exception.printStackTrace();
        HashMap<String, String> errorResult = new HashMap<String, String>();
        errorResult.put("httpStatusCode", HttpStatus.BAD_REQUEST.toString());
        errorResult.put("errorMessage", exception.getMessage());
        return new ResponseEntity<HashMap<String, String>>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(XMLConvException.class)
    public void HandleGDEMException(Exception exception, HttpServletResponse response) {
        exception.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<HashMap<String, String>> HandleUnsupportedOperationException(Exception exception, HttpServletResponse response) {
        exception.printStackTrace();
        LinkedHashMap<String, String> errorResult = new LinkedHashMap<String, String>();
        errorResult.put("httpStatusCode", HttpStatus.NOT_IMPLEMENTED.toString());
        errorResult.put("errorMessage", exception.getMessage());
        return new ResponseEntity<HashMap<String, String>>(errorResult, HttpStatus.NOT_IMPLEMENTED);
    }

    public String ConvertByteArrayToString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "UTF-8");
    }

}