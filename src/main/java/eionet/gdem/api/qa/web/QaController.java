package eionet.gdem.api.qa.web;

import eionet.gdem.XMLConvException;
import eionet.gdem.api.errors.EmptyParameterException;
import eionet.gdem.api.errors.QaServiceException;
import eionet.gdem.api.qa.model.EnvelopeWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.qa.XQueryService;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
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
     * Perform a Qa Job instantly
     **/
    @RequestMapping(value = "/sendQARequest", method = RequestMethod.POST,consumes = "application/json", produces = "application/json")
    public ResponseEntity<HashMap<String,String>> sendQARequest(@RequestBody EnvelopeWrapper envelopeWrapper) throws QaServiceException, EmptyParameterException {

        if (envelopeWrapper.getSourceUrl()==null) {
            throw new EmptyParameterException("source_url");
        }
        if (envelopeWrapper.getScriptId()==null) {
            throw new EmptyParameterException("script_id");
        }
        
       Vector results =  qaService.runQaScript(envelopeWrapper.getSourceUrl(), envelopeWrapper.getScriptId());
     
       HashMap<String,String> jsonResults = new HashMap<String,String>();
       jsonResults.put("contentType", results.get(0).toString());
       
       byte[] bytes = (byte[])results.get(1);
        try {
            String str = new String(bytes, "UTF-8");
            jsonResults.put("results",str);
        } catch (UnsupportedEncodingException ex) {
            jsonResults.put("results",results.get(1).toString());
        }
       return new ResponseEntity<HashMap<String,String>>(jsonResults, HttpStatus.OK);
    }

   /**
     * Schedule a Qa Job
     **/
    @RequestMapping(value = "/analyzeEnvelope", method = RequestMethod.POST,consumes = "application/json", produces = "application/json")
    public ResponseEntity<Vector> scheduleQaRequest(@RequestBody  EnvelopeWrapper envelopeWrapper) throws QaServiceException ,EmptyParameterException {

        if (envelopeWrapper.getEnvelopeUrl()==null) {
            throw new EmptyParameterException("envelope_url");
        }
        HashMap<String, String> fileSchemasAndLinks = qaService.extractSchemasAndFilesFromEnvelopeUrl(envelopeWrapper.getEnvelopeUrl());
        Vector jobIds = qaService.scheduleJobs(fileSchemasAndLinks);

        return new ResponseEntity<Vector>(jobIds, HttpStatus.OK);

    }

    
    /**
     * Authorized Schedule a Qa Job
     **/
    @RequestMapping(value = "/auth/analyzeEnvelope", method = RequestMethod.POST,consumes = "application/json", produces = "application/json")
    public ResponseEntity<Vector> SecuredScheduleQaRequest(@RequestBody  EnvelopeWrapper envelopeWrapper) throws QaServiceException ,EmptyParameterException {

        if (envelopeWrapper.getEnvelopeUrl()==null) {
            throw new EmptyParameterException("envelope_url");
        }
        HashMap<String, String> fileSchemasAndLinks = qaService.extractSchemasAndFilesFromEnvelopeUrl(envelopeWrapper.getEnvelopeUrl());
        Vector jobIds = qaService.scheduleJobs(fileSchemasAndLinks);

        return new ResponseEntity<Vector>(jobIds, HttpStatus.OK);

    }
    
    @RequestMapping(value = "/getQAResults/{jobId}", method = RequestMethod.GET)
    public ResponseEntity<Hashtable> getQAResultsForJob(@PathVariable String jobId) throws XMLConvException {

        XQueryService xqueryService = new XQueryService();
        Hashtable results = xqueryService.getResult(jobId);
    
        return new ResponseEntity<Hashtable>(results, HttpStatus.OK);
    }

    
    @RequestMapping(value="/listQueries",method = RequestMethod.GET)
    public ResponseEntity<Vector> listQeuries(@RequestParam String schema)throws XMLConvException{
    
        XQueryService xqueryService = new XQueryService();
        Vector results = xqueryService.listQueries(schema);
        
        return new ResponseEntity<Vector>(results,HttpStatus.OK);
    }
    
  /*  
    @ExceptionHandler(JobNotFoundException.class)
    public void handleJobNotFoundException(JobNotFoundException exception, HttpServletResponse response) {
        exception.printStackTrace();
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
   
    }

   */ 
    @ExceptionHandler(QaServiceException.class)
    public void HandleQaServiceException(Exception exception, HttpServletResponse response) {
        exception.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    
    /**
     *This Exception is quite generic and common so we should consider moving it to a Global Exception Handler class with @ControllerAdvice 
     **/
    @ExceptionHandler(EmptyParameterException.class)
    public ResponseEntity<HashMap<String,String>> HandleEmptyParameterException(Exception exception) {
        exception.printStackTrace();
        HashMap<String,String> errorResult = new HashMap<String,String>();
        errorResult.put("error message",exception.getMessage());
        return new ResponseEntity<HashMap<String,String>>(errorResult,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(XMLConvException.class)
    public void HandleGDEMException(Exception exception, HttpServletResponse response) {
        exception.printStackTrace();
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
