package eionet.gdem.qa;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.exceptions.XMLConvException;
import eionet.gdem.qa.model.XQScript;
import eionet.gdem.services.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.logging.Markers;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.db.dao.IQueryDao;
import eionet.gdem.services.db.dao.IXQJobDao;
import eionet.gdem.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * QA job in the workqueue. A task executing the QA task and storing the results of processing.
 * TODO investigate adding dependency injection
 */
public class QAJob implements Job, InterruptableJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(QAJob.class);

    private String scriptFile;
    private String resultFile;
    private String jobId;
    private String queryID;
    private String scriptType;
    private String url;
    private IXQJobDao xqJobDao = GDEMServices.getDaoService().getXQJobDao();
    private IQueryDao queryDao = GDEMServices.getDaoService().getQueryDao();

    // XXX: Fix asap
    private SchemaManager schemaManager = (SchemaManager) SpringApplicationContext.getBean("schemaManager");
    private QAService qaService = (QAService) SpringApplicationContext.getBean("QAService");
    private QARestService qaRestService = (QARestService) SpringApplicationContext.getBean("QARestService");

    private volatile Thread thisThread;

    /**
     * Public constructor.
     */
    public QAJob() {
    }

    /**
     * Run XQuery script.
     */
    @Override
    public void execute(JobExecutionContext paramJobExecutionContext) throws JobExecutionException {
        thisThread = Thread.currentThread();
        try {
            //LOGGER.info("Job ID=  " + jobId + " started.");
            initVariables();
            String srcFile = url;
            //
            int jobRetries = getJobRetries();
            if (jobRetries >= 4) // break execution
                throw new XMLConvException("Retry count reached");

            // update the Job status on db
            processJob();

            // Do validation
            if (queryID.equals(String.valueOf(Constants.JOB_VALIDATION))) {
                try {
                    // validate only the first XML Schema
                    if (scriptFile.contains(" ")) {
                        scriptFile = StringUtils.substringBefore(scriptFile, " ");
                    }
                    LOGGER.info("** XML Validation Job starting, ID=" + jobId + " schema: " + scriptFile + " result will be stored to " + resultFile);
                    /*ValidationService vs = new JaxpValidationService();*/
                    String query = StringUtils.defaultIfEmpty(new URI(srcFile).getQuery(), "");
                    List<NameValuePair> params = URLEncodedUtils.parse(query, StandardCharsets.UTF_8);
                    for (NameValuePair param : params) {
                        if (Constants.TICKET_PARAM.equals(param.getName())) {
                            //vs.setTicket(param.getValue());
                        }
                        if (Constants.SOURCE_URL_PARAM.equals(param.getName())) {
                            srcFile = param.getValue();
                        }
                    }
                    // XML Schema should be in schemaLocation attribute
                    String result = null;
                    // TODO FIX ASAP
                    ResponseEntity<ValidateDto[]> valid;
                    valid = qaRestService.executeValidation(srcFile, scriptFile);
                    LOGGER.debug("Validation proceeded, now store to the result file");
                    Utils.saveStrToFile(resultFile, result, null);
                    changeStatus(Constants.XQ_READY);
                } catch (Exception e) {
                    handleError("Error during validation:" + e.toString(), true);
                }
            } else {
                // read query info from DB.
                Map query = getQueryInfo(queryID);
                String contentType = null;
                Schema schema = null;
                //boolean schemaExpired = false;
                //boolean isNotLatestReleasedDDSchema = false;

                if (query != null && query.containsKey(QaScriptView.CONTENT_TYPE)) {
                    contentType = (String) query.get(QaScriptView.CONTENT_TYPE);
                }
                // get script type if it comes from T_QUERY table
                if (query != null && query.containsKey(QaScriptView.SCRIPT_TYPE)) {
                    scriptType = (String) query.get(QaScriptView.SCRIPT_TYPE);
                }

                // stylesheet - to check if it is expired
                if (query != null && query.containsKey(QaScriptView.XML_SCHEMA)) {
                    // set schema if exists:
                    schema = getSchema((String) query.get(QaScriptView.XML_SCHEMA));
                    //schemaExpired = (schema != null && schema.isExpired());
                    //isNotLatestReleasedDDSchema = DataDictUtil.isDDSchemaAndNotLatestReleased(schema.getSchema());
                }

                // get script type if it stored in filesystem and we have to guess it by file extension.
                if (Utils.isNullStr(scriptType)) {
                    scriptType =
                            scriptFile.endsWith(XQScript.SCRIPT_LANG_XSL) ? XQScript.SCRIPT_LANG_XSL
                                    : scriptFile.endsWith(XQScript.SCRIPT_LANG_XGAWK) ? XQScript.SCRIPT_LANG_XGAWK
                                    : XQScript.SCRIPT_LANG_XQUERY1;
                }
                String[] xqParam = {Constants.XQ_SOURCE_PARAM_NAME + "=" + srcFile};

                if (scriptFile.contains(" ")) {
                    scriptFile = StringUtils.substringBefore(scriptFile, " ");
                }

                XQScript xq = new XQScript(null, xqParam, contentType);
                xq.setScriptFileName(scriptFile);
                xq.setScriptType(scriptType);
                xq.setSrcFileUrl(srcFile);
                xq.setSchema(schema);
                xq.setJobId(this.jobId);

                if (XQScript.SCRIPT_LANG_FME.equals(scriptType)) {
                    if (query != null && query.containsKey(QaScriptView.URL)) {
                        xq.setScriptSource((String) query.get(QaScriptView.URL));
                    }
                    LOGGER.info("** FME Job starts, ID=" + jobId + " params: " + xqParam[0] + " result will be stored to " + resultFile);
                } else {
                    LOGGER.info("** XQuery Job starts, ID=" + jobId + " params: " + xqParam[0] + " result will be stored to " + resultFile);
                }

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(new File(resultFile));
                    qaService.execute(xq, out);
                    /*xq.getResult(out);*/
                    changeStatus(Constants.XQ_READY);
                    LOGGER.info("Job ID=" + jobId + " finished.");
                } catch (HttpStatusCodeException | XMLConvException e) {
                    changeStatus(Constants.XQ_FATAL_ERR);
                    StringBuilder errBuilder = new StringBuilder();
                    errBuilder.append("<div class=\"feedbacktext\"><span id=\"feedbackStatus\" class=\"BLOCKER\" style=\"display:none\">Unexpected error occured!</span><h2>Unexpected error occured!</h2>");
                    errBuilder.append(Utils.escapeXML(e.toString()));
                    errBuilder.append("</div>");
                    IOUtils.write(errBuilder.toString(), out, "UTF-8");
                    LOGGER.error("XQueryJob ID=" + this.jobId + " exception: ", e);
                } finally {
                    IOUtils.closeQuietly(out);
                }
            }
        } catch (Exception ee) {
            handleError("Error in thread run():" + ee.toString(), true);
        }
    }

    /**
     * Read data from the DB where it is stored for further processing.
     */
    private void initVariables() {
        try {
            String[] jobData = xqJobDao.getXQJobData(jobId);
            if (jobData == null) {
                handleError("No such job: " + jobId, true);
                return;
            }
            url = jobData[0];
            scriptFile = jobData[1];
            resultFile = jobData[2]; // just a file name, file is not created
            queryID = jobData[5];
            scriptType = jobData[8];
        } catch (SQLException sqe) {
            handleError("Error getting WQ data from the DB: " + sqe.toString(), true);
        }
    }

    /**
     * Changes the status to ERROR and finishes the thread normally saves the error message as the result of the job.
     *
     * @param error Error message.
     * @param fatal True if the error is fatal and there is no result.
     */
    private void handleError(String error, boolean fatal) {
        LOGGER.error("Error handling started: <<< " + error + " >>> ");
        try {
            int errStatus;
            if (fatal) {
                errStatus = Constants.XQ_FATAL_ERR;
            } else {
                errStatus = Constants.XQ_LIGHT_ERR;
            }
            changeStatus(errStatus);
            // if result file already ok, store the error message in the file:
            if (resultFile == null) {
                resultFile = Properties.tmpFolder + File.separatorChar + "gdem_error" + jobId + ".txt";
            }
            LOGGER.info("******* The error message is stored to: " + resultFile);
            if (error == null) {
                error = "No error message for job=" + jobId;
            }
            Utils.saveStrToFile(resultFile, "<error>" + error + "</error>", null);
        } catch (Exception e) {
            // what to do if exception occurs here...
            LOGGER.error(Markers.FATAL, "** Error occurred when handling XQ error: " + e.toString());
        }
    }

    /**
     * Change job status in DB.
     *
     * @param status Job status to be stored in DB.
     * @throws Exception Unable to store data into DB.
     */
    private void changeStatus(int status) throws Exception {
        try {
            xqJobDao.changeJobStatus(jobId, status);
        } catch (Exception e) {
            LOGGER.error("Database exception when changing job status. " + e.toString());
            throw e;
        }
    }

    private void processJob() throws SQLException {
        try {
            xqJobDao.processXQJob(jobId);
        } catch (Exception e) {
            LOGGER.error("Database exception when changing job status. " + e.toString());
            throw e;
        }
    }

    /**
     * Get retry count for the job.
     *
     * @throws Exception Unable to store data into DB.
     */
    private int getJobRetries() throws Exception {
        try {
            return xqJobDao.getJobRetries(jobId);
        } catch (Exception e) {
            LOGGER.error("Database exception when changing job status. " + e.toString());
            throw e;
        }
    }

    /**
     * Loads Query info from database.
     *
     * @param id Query Id to be queried from DB.
     * @return Map with query data.
     */
    private Map getQueryInfo(String id) {
        Map query = null;
        if (id != null) {
            try {
                query = queryDao.getQueryInfo(id);
            } catch (Exception e) {
                LOGGER.error("Error: ", e);
            }
        }
        return query;
    }

    /**
     * Query Schema information from DB.
     *
     * @param schemaUrl Schema Url or unique ID in DB to be queried.
     * @return Schema object.
     */
    private Schema getSchema(String schemaUrl) {
        try {
            if (schemaUrl != null) {
                String schemaId = schemaManager.getSchemaId(schemaUrl);
                if (schemaId != null) {
                    return schemaManager.getSchema(schemaId);
                }
            }
        } catch (Exception e) {
            LOGGER.error("getSchema() error : " + e.toString());
        }
        return null;
    }

    /**
     * Setter to be able to parse the jobId from JobData.
     *
     * @param jobId Job id
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        LOGGER.info("Job " + this.jobId + "  -- INTERRUPTING --");
        if (thisThread != null && XQScript.SCRIPT_LANG_FME.equals(scriptType)) {
            // this call causes the ClosedByInterruptException to happen
            thisThread.interrupt();
        } else {
            throw new UnableToInterruptJobException("Unable to interrupt XQ job.");
        }
    }
}
