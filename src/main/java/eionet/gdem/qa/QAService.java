package eionet.gdem.qa;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.web.spring.schemas.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.exceptions.XMLConvException;
import eionet.gdem.http.HttpFileManager;
import eionet.gdem.qa.engines.FMEQueryEngine;
import eionet.gdem.qa.model.Response;
import eionet.gdem.qa.model.ValidationResult;
import eionet.gdem.qa.model.XQScript;
import eionet.gdem.qa.utils.ScriptUtils;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.conversions.IConvTypeDao;
import eionet.gdem.web.spring.schemas.ISchemaDao;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import eionet.gdem.utils.Utils;
import eionet.gdem.utils.xml.FeedbackAnalyzer;
import eionet.gdem.validation.InputAnalyser;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static eionet.gdem.Constants.JOB_VALIDATION;
import static eionet.gdem.qa.ScriptStatus.getActiveStatusList;
import static eionet.gdem.quartz.JobSchedulerHelper.getQuartzHeavyScheduler;
import static eionet.gdem.quartz.JobSchedulerHelper.getQuartzScheduler;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Used by QARestService, Remote API, intended to replace XQueryService.class
 * TODO Replace slowly part by part
 */
@Service
public class QAService {

    /** Query ID property key in ListQueries method result. */
    public static final String KEY_QUERY_ID = "query_id";
    /** Query file property key in ListQueries method result. */
    public static final String KEY_QUERY = "query";
    /** Query short name property key in ListQueries method result. */
    public static final String KEY_SHORT_NAME = "short_name";
    /** Query description property key in ListQueries method result. */
    public static final String KEY_DESCRIPTION = "description";
    /** Schema ID property key in ListQueries method result. */
    public static final String KEY_SCHEMA_ID = "schema_id";
    /** Schema URL property key in ListQueries method result. */
    public static final String KEY_XML_SCHEMA = "xml_schema";
    /** Type property key in ListQueries method result. */
    public static final String KEY_TYPE = "type";
    /** Output content type property key in ListQueries method result. */
    public static final String KEY_CONTENT_TYPE_OUT = "content_type_out";
    /** Output content type ID property key in ListQueries method result. */
    public static final String KEY_CONTENT_TYPE_ID = "content_type_id";
    /** XML file upper limit property key in ListQueries method result. */
    public static final String KEY_UPPER_LIMIT = "upper_limit";
    /** Upper limit for xml file size to be sent to manual QA. */
    public static final int VALIDATION_UPPER_LIMIT = Properties.qaValidationXmlUpperLimit;

    private static final long heavyJobThreshhold = Properties.heavyJobThreshhold;
    /** Default conversion output type. */
    public static final String DEFAULT_CONTENT_TYPE_ID = "HTML";

    private static final Logger LOGGER = LoggerFactory.getLogger(QAService.class);
    public static final String SCRIPT_ID = "scriptId";


    /** DAO for getting schema info. */
    private final ISchemaDao schemaDao;
    /** DAO for getting query info. */
    private final IQueryDao queryDao;
    /** DAO for getting conversion types info. */
    private final IConvTypeDao convTypeDao;
    private final IXQJobDao xqJobDao;

    private final SchemaManager schemaManager;
    private final FMEQueryEngine fmeQueryEngine;
    private final QARestService qaRestService;

    @Autowired
    public QAService(ISchemaDao schemaDao, IQueryDao queryDao, IConvTypeDao convTypeDao, IXQJobDao xqJobDao, SchemaManager schemaManager, FMEQueryEngine fmeQueryEngine, QARestService qaRestService) {
        this.schemaDao = schemaDao;
        this.queryDao = queryDao;
        this.convTypeDao = convTypeDao;
        this.xqJobDao = xqJobDao;
        this.schemaManager = schemaManager;
        this.fmeQueryEngine = fmeQueryEngine;
        this.qaRestService = qaRestService;
    }

    public String execute(XQScript script) {
        if (XQScript.SCRIPT_LANG_FME.equals(script.getScriptType())) {
            return fmeQueryEngine.getResult(script);
        }

        return "";
    }

    public void execute(XQScript script, OutputStream out) throws IOException {
        String scriptType = script.getScriptType();
        if (XQScript.SCRIPT_LANG_FME.equals(scriptType)) {
            fmeQueryEngine.getResult(script, out);
        } else if (XQScript.SCRIPT_LANG_XQUERY3.equals(scriptType)) {
            Response response = qaRestService.executeBaseX(script);
            IOUtils.write(response.getResult().getBytes(StandardCharsets.UTF_8), out);
        } else {
            qaRestService.executeSaxon(script);
        }

    }

    /**
     * List all possible QA scripts (XQueries, XML Schemas, DTD, XSLT?) for this
     * XML Schema. If schema is null, then all possible QA scripts are returned
     *
     * @param schema URL of XML schema
     * @return array of Hastables with the following keys: qyery_id, short_name,
     * description, query, schema_id, xml_schema, content_type_out, type
     *
     * @throws XMLConvException If an error occurs.
     */
    public Vector listQueries(String schema) throws XMLConvException {

        Vector v = new Vector();
        if (schema != null && schema.equals("")) {
            schema = null;
        }

        try {
            // Get schemas that has to be validated
            Vector schemas = schemaDao.getSchemas(schema, false);
            Hashtable convType = convTypeDao.getConvType(DEFAULT_CONTENT_TYPE_ID);
            String contentType
                    = (convType != null && convType.containsKey("content_type")) ? (String) convType.get("content_type")
                    : "text/html;charset=UTF-8";

            if (schemas != null) {
                for (int i = 0; i < schemas.size(); i++) {
                    HashMap h = (HashMap) schemas.get(i);
                    String validate = (String) h.get("validate");
                    if (!Utils.isNullStr(validate)) {
                        if (validate.equals("1")) {
                            Hashtable ht = new Hashtable();
                            ht.put(QaScriptView.QUERY_ID, String.valueOf(Constants.JOB_VALIDATION));
                            ht.put(QaScriptView.SHORT_NAME, "XML Schema Validation");
                            ht.put(QaScriptView.QUERY, h.get("xml_schema"));
                            ht.put(QaScriptView.DESCRIPTION, h.get("description"));
                            ht.put(QaScriptView.SCHEMA_ID, h.get("schema_id"));
                            ht.put(QaScriptView.XML_SCHEMA, h.get("xml_schema"));
                            ht.put(QaScriptView.CONTENT_TYPE_ID, DEFAULT_CONTENT_TYPE_ID);
                            ht.put(QaScriptView.CONTENT_TYPE_OUT, contentType);
                            ht.put(QaScriptView.TYPE, ((String) h.get("schema_lang")).toLowerCase());
                            ht.put(QaScriptView.UPPER_LIMIT, String.valueOf(VALIDATION_UPPER_LIMIT));
                            v.add(ht);
                        }
                    }
                }
            }
            // Get XQueries
            Vector queries = queryDao.listQueries(schema);
            if (queries != null) {
                for (int i = 0; i < queries.size(); i++) {
                    Hashtable ht = (Hashtable) queries.get(i);
                    if (!isActive(ht)) {
                        continue;
                    }
                    ht.put(KEY_TYPE, Constants.QA_TYPE_XQUERY);
                    // return full URL of XQuerys
                    ht.put(KEY_QUERY, Properties.gdemURL + "/" + Constants.QUERIES_FOLDER + (String) ht.get("query"));
                    v.add(ht);
                }
            }
        } catch (Exception e) {

            throw new XMLConvException("Error getting data from the DB " + e.toString(), e);
        }
        return v;
    }

    /**
     * List all XQueries and their modification times for this namespace returns
     * also XML Schema validation.
     *
     * @param schema Schema to use
     * @return result is an Array of Arrays that contains 3 fields (script_id,
     * description, last modification)
     * @throws XMLConvException If an error occurs.
     */
    public Vector listQAScripts(String schema) throws XMLConvException {
        Vector<Vector<String>> result = new Vector<Vector<String>>();
        Vector<String> resultQuery = null;
        try {
            Vector v = schemaDao.getSchemas(schema);

            if (Utils.isNullVector(v)) {
                return result;
            }

            HashMap h = (HashMap) v.get(0);
            String validate = (String) h.get("validate");
            if (!Utils.isNullStr(validate)) {
                if (validate.equals("1")) {
                    resultQuery = new Vector<String>();
                    resultQuery.add(String.valueOf(Constants.JOB_VALIDATION));
                    resultQuery.add("XML Schema Validation");
                    resultQuery.add("");
                    resultQuery.add(String.valueOf(VALIDATION_UPPER_LIMIT));
                    result.add(resultQuery);
                }
            }
            Vector queries = (Vector) h.get("queries");
            if (Utils.isNullVector(queries)) {
                return result;
            }

            for (int i = 0; i < queries.size(); i++) {
                HashMap hQueries = (HashMap) queries.get(i);
                if (!isActive(hQueries)) {
                    continue;
                }
                String queryId = (String) hQueries.get(QaScriptView.QUERY_ID);
                String queryFile = (String) hQueries.get(QaScriptView.QUERY);
                String queryDescription = (String) hQueries.get(QaScriptView.DESCRIPTION);
                String queryName = (String) hQueries.get(QaScriptView.SHORT_NAME);
                String queryUpperLimit = (String) hQueries.get(QaScriptView.UPPER_LIMIT);

                if (Utils.isNullStr(queryDescription)) {
                    if (Utils.isNullStr(queryName)) {
                        queryDescription = "Quality Assurance script";
                    } else {
                        queryDescription = queryName;
                    }
                }
                resultQuery = new Vector<String>();
                resultQuery.add(queryId);
                resultQuery.add(queryDescription);
                File f = new File(Properties.queriesFolder + File.separator + queryFile);
                String last_modified = "";

                if (f != null) {
                    last_modified = Utils.getDateTime(new Date(f.lastModified()));
                }

                resultQuery.add(last_modified);
                resultQuery.add(queryUpperLimit);
                result.add(resultQuery);
            }

        } catch (Exception e) {
            throw new XMLConvException("Error getting data from the DB " + e.toString(), e);
        }

        return result;
    }

    /*
    * List all possible QA scripts (XQueries, XML Schemas, DTD, XSLT?) for this XML Schema , according
    * to the active status passed. If schema is null, then all possible QA scripts are returned.
    **/
    public Vector listQAScripts(String schema, String active) throws XMLConvException {

        if (!getActiveStatusList().contains(active)) {
            throw new XMLConvException("wrong query active value " + active);
        }

        Vector v = new Vector();
        if (schema != null && schema.equals("")) {
            schema = null;
        }

        try {
            // Get schemas that has to be validated
            if (active.equals("true") || active.equals("all")) {

                Vector schemas = schemaDao.getSchemas(schema, false);
                Hashtable convType = convTypeDao.getConvType(DEFAULT_CONTENT_TYPE_ID);
                String contentType
                        = (convType != null && convType.containsKey("content_type")) ? (String) convType.get("content_type")
                        : "text/html;charset=UTF-8";

                if (schemas != null) {
                    for (int i = 0; i < schemas.size(); i++) {
                        HashMap h = (HashMap) schemas.get(i);
                        String validate = (String) h.get("validate");
                        if (!Utils.isNullStr(validate)) {
                            if (validate.equals("1")) {

                                Hashtable ht = new Hashtable();
                                ht.put(QaScriptView.IS_ACTIVE,"1");
                                ht.put(QaScriptView.QUERY_ID, String.valueOf(Constants.JOB_VALIDATION));
                                ht.put(QaScriptView.SHORT_NAME, "XML Schema Validation");
                                ht.put(QaScriptView.QUERY, h.get("xml_schema"));
                                ht.put(QaScriptView.DESCRIPTION, h.get("description"));
                                ht.put(QaScriptView.SCHEMA_ID, h.get("schema_id"));
                                ht.put(QaScriptView.XML_SCHEMA, h.get("xml_schema"));
                                ht.put(QaScriptView.CONTENT_TYPE_ID, DEFAULT_CONTENT_TYPE_ID);
                                ht.put(QaScriptView.CONTENT_TYPE, contentType);
                                ht.put(QaScriptView.TYPE, ((String) h.get("schema_lang")).toLowerCase());
                                ht.put(QaScriptView.UPPER_LIMIT, String.valueOf(VALIDATION_UPPER_LIMIT));
                                v.add(ht);

                            }
                        }
                    }
                }
            }

            // Get XQueries
            Vector queries;
            switch (active) {

                case "true":
                    queries = queryDao.listQueries(schema, true);
                    break;
                case "false":
                    queries = queryDao.listQueries(schema, false);
                    break;
                default:
                    // when active ->all
                    queries = queryDao.listQueries(schema);

            }
            if (queries != null) {
                for (int i = 0; i < queries.size(); i++) {
                    Hashtable ht = (Hashtable) queries.get(i);
                    ht.put(KEY_TYPE, Constants.QA_TYPE_XQUERY);
                    // return full URL of XQuerys
                    ht.put(KEY_QUERY, Properties.gdemURL + "/" + Constants.QUERIES_FOLDER + (String) ht.get("query"));
                    v.add(ht);
                }
            }

        } catch (Exception e) {
            throw new XMLConvException("Error getting data from the DB " + e.toString(), e);
        }
        return v;

    }

    /**
     * Remote method for running the QA script on the fly.
     *
     * @param sourceUrl URL of the source XML
     * @param scriptId  XQueryScript ID or -1 (XML Schema validation) to be processed
     * @return Vector of 2 fields: content type and byte array
     * @throws XMLConvException in case of business logic error
     */
    public Vector runQAScript(String sourceUrl, String scriptId) throws XMLConvException {
        Vector result = new Vector();
        String fileUrl;
        String contentType = "text/html;charset=UTF-8";
        String strResult;
        LOGGER.debug("==xmlconv== runQAScript: id=" + scriptId + " file_url=" + sourceUrl + "; ");
        try {
            if (scriptId.equals(String.valueOf(Constants.JOB_VALIDATION))) {
                //vs.setTicket(getTicket());
                CompletableFuture<ValidationResult> future;
                strResult = null;
                future = qaRestService.executeValidation(sourceUrl);
                ValidationResult valid = future.get();
            } else {
                fileUrl = HttpFileManager.getSourceUrlWithTicket("", sourceUrl, true);
                String[] pars = new String[1];
                pars[0] = Constants.XQ_SOURCE_PARAM_NAME + "=" + fileUrl;
                try {
                    HashMap hash = queryDao.getQueryInfo(scriptId);
                    String xqScript = "";
                    // If the script type is not FME, the script content is retrieved.
                    if (!XQScript.SCRIPT_LANG_FME.equals((String) hash.get(QaScriptView.SCRIPT_TYPE))) {
                        xqScript = queryDao.getQueryText(scriptId);
                    } else {
                        xqScript = XQScript.SCRIPT_LANG_FME; // Dummy value
                    }
                    String schemaId = (String) hash.get(QaScriptView.SCHEMA_ID);
                    Schema schema = null;
                    // check because ISchemaDao.getSchema(null) returns first schema
                    if (schemaId != null) {
                        schema = schemaManager.getSchema(schemaId);
                    }

                    if (Utils.isNullStr(xqScript) || hash == null) {
                        String errMess = "Could not find QA script with id: " + scriptId;
                        LOGGER.error(errMess);
                        throw new XMLConvException(errMess, new Exception());
                    } else {
                        if (!Utils.isNullStr((String) hash.get(QaScriptView.META_TYPE))) {
                            contentType = (String) hash.get(QaScriptView.META_TYPE);
                        }
                        LOGGER.debug("Script: " + xqScript);
                        XQScript xq = new XQScript(xqScript, pars, (String) hash.get(QaScriptView.CONTENT_TYPE));
                        xq.setScriptType((String) hash.get(QaScriptView.SCRIPT_TYPE));
                        xq.setSrcFileUrl(fileUrl);
                        xq.setSchema(schema);

                        if (XQScript.SCRIPT_LANG_FME.equals(xq.getScriptType())) {
                            xq.setScriptSource((String) hash.get(QaScriptView.URL));
                        }

                        strResult = this.execute(xq);
                    }
                } catch (SQLException sqle) {
                    throw new XMLConvException("Error getting data from DB: " + sqle.toString());
                } catch (Exception e) {
                    String errMess = "Could not execute runQAMethod";
                    LOGGER.error(errMess + "; " + e.toString(), e);
                    throw new XMLConvException(errMess, e);
                }
            }
            /*if (isHttpRequest()) {*/
                /*try {
                    HttpMethodResponseWrapper httpResponse = getHttpResponse();
                    httpResponse.setContentType(contentType);
                    httpResponse.setCharacterEncoding("UTF-8");
                    httpResponse.setContentDisposition("qaresult.xml");
                    OutputStream outstream = httpResponse.getOutputStream();
                    IOUtils.write(strResult, outstream, "UTF-8");
                } catch (IOException e) {
                    LOGGER.error("Error getting response outputstream ", e);
                    throw new XMLConvException("Error getting response outputstream " + e.toString(), e);
                }
            } else {*/
            result.add(contentType);
            result.add(strResult.getBytes());

            HashMap<String, String> fbResult = FeedbackAnalyzer.getFeedbackResultFromStr(strResult);
            result.add(fbResult.get(Constants.RESULT_FEEDBACKSTATUS_PRM).getBytes());
            result.add((fbResult.get(Constants.RESULT_FEEDBACKMESSAGE_PRM).getBytes()));
        } catch (DCMException e) {
            LOGGER.error("Error: ", e);
        } catch (URISyntaxException e) {
            LOGGER.error("Error: ", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * TODO REMOVE OR RENAME FOLLOWING METHODS
     */

    /**
     * Request from XML/RPC client Stores the source files and starts a job in the workqueue.
     *
     * @param files - Structure with XMLschemas as a keys and values are list of XML Files
     * @return Hashtable result: Structure with JOB ids as a keys and source files as values
     * @throws XMLConvException If an error occurs.
     */
    public Vector analyzeXMLFiles(Hashtable files) throws XMLConvException {

        Vector result = new Vector();

        if (files == null) {
            return result;
        }

        Enumeration _schemas = files.keys();
        while (_schemas.hasMoreElements()) {
            String _schema = _schemas.nextElement().toString();
            Vector _files = (Vector) files.get(_schema);
            if (Utils.isNullVector(_files)) {
                continue;
            }

            for (int i = 0; i < _files.size(); i++) {
                String _file = (String) _files.get(i);
                analyzeXMLFiles(_schema, _file, result);
            }
        }
        return result;
    }

    /**
     * Stores one source file and starts a job in the workqueue.
     *
     * @param schema - XML Schema URL
     * @param file - Source file URL
     * @return Hashtable result: Structure with JOB ids as a keys and source files as values
     */
    // public Hashtable analyze(String schema, String file) throws XMLConvException{
    // return analyze(schema,file, null);
    // }

    /**
     * Analyzes XML files
     * @param schema XML Schema
     * @param origFile Original file
     * @param result Result
     * @return Processed result
     * @throws XMLConvException If an error occurs.
     */
    public Vector analyzeXMLFiles(String schema, String origFile, Vector result) throws XMLConvException {

        LOGGER.info("analyzeXMLFiles: " + origFile);

        if (result == null) {
            result = new Vector();
        }
        // get all possible xqueries from db
        String newId = "-1"; // should not be returned with value -1;

        Vector queries = listQueries(schema);

        if (!Utils.isNullVector(queries)) {
            for (int j = 0; j < queries.size(); j++) {

                String scriptId = String.valueOf(((Hashtable) queries.get(j)).get("id"));
                String scriptTitle = StringUtils.defaultIfEmpty(ConvertUtils.convert(((Hashtable) queries.get(j)).get("name")), "");
                newId = analyzeXMLFile(origFile, scriptId, schema);

                Vector queryResult = new Vector();
                queryResult.add(newId);
                queryResult.add(origFile);
                queryResult.add(scriptId);
                queryResult.add(scriptTitle);
                result.add(queryResult);
            }
        }

        LOGGER.info("Analyze xml result: " + result.toString());
        return result;
    }

    /**
     * Gets file extension
     * @param outputTypes Output Types
     * @param content_type Content type
     * @return Extension
     */
    private String getExtension(Vector outputTypes, String content_type) {
        String ret = "html";
        if (outputTypes == null) {
            return ret;
        }
        if (content_type == null) {
            return ret;
        }

        for (int i = 0; i < outputTypes.size(); i++) {
            Hashtable outType = (Hashtable) outputTypes.get(i);
            if (outType == null) {
                continue;
            }
            if (!outType.containsKey("conv_type") || !outType.containsKey("file_ext") || outType.get("conv_type") == null
                    || outType.get("file_ext") == null) {
                continue;
            }
            String typeId = (String) outType.get("conv_type");
            if (!content_type.equalsIgnoreCase(typeId)) {
                continue;
            }
            ret = (String) outType.get("file_ext");
        }

        return ret;
    }

    /**
     * Request from XML/RPC client Stores the xqScript and starts a job in the workqueue.
     *
     * @param sourceURL - URL of the source XML
     * @param xqScript - XQueryScript to be processed
     * @param scriptType - xquery, xsl or xgawk
     * @throws XMLConvException If an error occurs.
     */
    public String addJob(String sourceURL, String xqScript, String scriptType) throws XMLConvException {
        String xqFile = "";
        String originalSourceURL = sourceURL;

        LOGGER.info("XML/RPC call for analyze xml with custom script: " + sourceURL);
        // save XQScript in a text file for the WQ
        try {
            String extension = ScriptUtils.getExtensionFromScriptType(scriptType);
            xqFile = Utils.saveStrToFile(xqScript, extension);
        } catch (FileNotFoundException fne) {
            throw new XMLConvException("Folder does not exist: :" + fne.toString());
        } catch (IOException ioe) {
            throw new XMLConvException("Error storing XQScript into file:" + ioe.toString());
        }

        // name for temporary output file where the esult is stored:
        String resultFile = Properties.tmpFolder + File.separatorChar + "gdem_" + System.currentTimeMillis() + ".html";
        String newId = "-1"; // should not be returned with value -1;

        // start a job in the Workqueue
        try {
            // get the trusted URL from source file adapter
            // TODO: check
            sourceURL = HttpFileManager.getSourceUrlWithTicket("", sourceURL, true);
            long sourceSize = HttpFileManager.getSourceURLSize("", originalSourceURL, true);

            newId = xqJobDao.startXQJob(sourceURL, xqFile, resultFile, scriptType);
            //
            scheduleJob(newId, sourceSize, scriptType);

        } catch (SQLException sqe) {
            LOGGER.error("DB operation failed: " + sqe.toString());
            throw new XMLConvException("DB operation failed: " + sqe.toString());
        } catch (URISyntaxException e) {
            throw new XMLConvException("URI syntax error: " + e);
        } catch (SchedulerException e) {
            LOGGER.error("Scheduler exception: " + e.toString());
            throw new XMLConvException("Scheduler exception: " + e.toString());
        }
        return newId;
    }

    /**
     * Checks if the job is ready (or error) and returns the result (or error message).
     *
     * @param jobId Job Id
     * @return Hash including code and result
     * @throws XMLConvException If an error occurs.
     */
    public Hashtable getResult(String jobId) throws XMLConvException {

        LOGGER.info("XML/RPC call for getting result with JOB ID: " + jobId);

        String[] jobData = null;
        HashMap scriptData = null;
        int status = 0;
        try {
            jobData = xqJobDao.getXQJobData(jobId);

            if (jobData == null) { // no such job
                // throw new XMLConvException("** No such job with ID=" + jobId + " in the queue.");
                status = Constants.XQ_JOBNOTFOUND_ERR;
            } else {
                scriptData = queryDao.getQueryInfo(jobData[5]);

                status = Integer.valueOf(jobData[3]).intValue();
            }
        } catch (SQLException sqle) {
            throw new XMLConvException("Error getting XQJob data from DB: " + sqle.toString());
        }

        LOGGER.info("XQueryService found status for job (" + jobId + "):" + String.valueOf(status));

        Hashtable ret = result(status, jobData, scriptData, jobId);
        if (LOGGER.isInfoEnabled()) {
            String result = ret.toString();
            if (result.length() > 100) {
                result = result.substring(0, 100).concat("....");
            }
            LOGGER.info("result: " + result);
        }
        return ret;
    }

    /**
     * Hashtable to be composed for the getResult() method return value.
     * @param status Status
     * @param jobData Job data
     * @param scriptData Script data
     * @param jobId Job Id
     * @return Result
     * @throws XMLConvException If an error occurs.
     */
    protected Hashtable result(int status, String[] jobData, HashMap scriptData, String jobId) throws XMLConvException {
        Hashtable<String, String> h = new Hashtable<String, String>();
        int resultCode;
        String resultValue = "";
        String metatype = "";
        String script_title = "";

        String feedbackStatus = Constants.XQ_FEEDBACKSTATUS_UNKNOWN;
        String feedbackMsg = "";

        int xq_id = 0;

        if (status == Constants.XQ_RECEIVED || status == Constants.XQ_DOWNLOADING_SRC || status == Constants.XQ_PROCESSING) {
            resultCode = Constants.JOB_NOT_READY;
            resultValue = "*** Not ready ***";
        } else if (status == Constants.XQ_JOBNOTFOUND_ERR) {
            resultCode = Constants.JOB_LIGHT_ERROR;
            resultValue = "*** No such job or the job result has been already downloaded. ***";
        } else {
            if (status == Constants.XQ_READY) {
                resultCode = Constants.JOB_READY;
            } else if (status == Constants.XQ_LIGHT_ERR) {
                resultCode = Constants.JOB_LIGHT_ERROR;
            } else if (status == Constants.XQ_FATAL_ERR) {
                resultCode = Constants.JOB_FATAL_ERROR;
            } else {
                resultCode = -1; // not expected to reach here
            }
            try {

                try {
                    xq_id = Integer.parseInt(jobData[5]);
                } catch (NumberFormatException n) {
                }

                if (xq_id == JOB_VALIDATION) {
                    metatype = "text/html";
                    script_title = "XML Schema validation";
                } else if (xq_id > 0) {
                    metatype = (String) scriptData.get(QaScriptView.META_TYPE);
                    script_title = (String) scriptData.get(QaScriptView.SHORT_NAME);
                }

                resultValue = Utils.readStrFromFile(jobData[2]);
                HashMap<String, String> feedbackResult = FeedbackAnalyzer.getFeedbackResultFromFile(jobData[2]);

                feedbackStatus = feedbackResult.get(Constants.RESULT_FEEDBACKSTATUS_PRM);
                feedbackMsg = feedbackResult.get(Constants.RESULT_FEEDBACKMESSAGE_PRM);


            } catch (Exception ioe) {
                resultCode = Constants.JOB_FATAL_ERROR;
                resultValue = "<error>Error reading the XQ value from the file:" + jobData[2] + "</error>";
            }

        }
        try {
            h.put(Constants.RESULT_CODE_PRM, Integer.toString(resultCode));
            h.put(Constants.RESULT_VALUE_PRM, resultValue);
            h.put(Constants.RESULT_METATYPE_PRM, metatype);
            h.put(Constants.RESULT_SCRIPTTITLE_PRM, script_title);
            h.put(Constants.RESULT_FEEDBACKSTATUS_PRM, feedbackStatus);
            h.put(Constants.RESULT_FEEDBACKMESSAGE_PRM, feedbackMsg);
            h.put(QAService.SCRIPT_ID, Integer.toString(xq_id));

        } catch (Exception e) {
            String err_mess =
                    "JobID: " + jobId + "; Creating result Hashtable for getResult method failed result: " + e.toString();
            LOGGER.error(err_mess);
            throw new XMLConvException(err_mess, e);
        }

        return h;

    }

    /**
     * Schedule to workqueue one job with direct script id
     * @param sourceURL -
     * @param scriptId -
     * @return the jobId on succesful scheduling
     * @throws XMLConvException -
     */
    public String analyzeXMLFile(String sourceURL, String scriptId) throws XMLConvException {
        return analyzeXMLFile(sourceURL, scriptId, null);
    }

    /**
     * Schedule to workqueue one job with direct script id and schema if needed for validation
     * @param sourceURL -
     * @param scriptId -
     * @param schema -
     * @return the jobId on succesful scheduling
     * @throws XMLConvException -
     */
    public String analyzeXMLFile(String sourceURL, String scriptId, String schema) throws XMLConvException {

        String jobId = "-1";
        HashMap query;
        String originalSourceURL = sourceURL;

        try {

            if (String.valueOf(Constants.JOB_VALIDATION).equals(scriptId)) { // Validation
                query = new HashMap();
                if (isEmpty(schema)){
                    InputAnalyser analyser = new InputAnalyser();
                    try {
                        analyser.parseXML(sourceURL);
                        schema = analyser.getSchemaOrDTD();
                        //return schemaOrDTD;
                    } catch (Exception e) {
                        throw new XMLConvException("Could not extract schema");
                    }
                    //String schemaUrl = findSchemaFromXml(sourceUrl);
                    query.put(QaScriptView.QUERY, schema);
                }
                //else {
                query.put(QaScriptView.QUERY, schema);
                //Vector schemas = schemaDao.getSchemas(schema, false);
                //}
                query.put(QaScriptView.QUERY_ID, "-1");
                query.put(QaScriptView.CONTENT_TYPE, DEFAULT_CONTENT_TYPE_ID);
                query.put(QaScriptView.SCRIPT_TYPE, "xsd");

            }
            else{
                query = queryDao.getQueryInfo(scriptId);
            }
            if (isNull(query)) {
                throw new XMLConvException("Script ID does not exist");
            }
            if ("0".equals(query.get(QaScriptView.IS_ACTIVE))) {
                throw new XMLConvException("Script is not active");
            }
            Vector outputTypes = convTypeDao.getConvTypes();

            String query_id = String.valueOf(query.get(QaScriptView.QUERY_ID));
            String queryFile = (String) query.get(QaScriptView.QUERY);
            String contentType = (String) query.get(QaScriptView.CONTENT_TYPE_ID);
            String scriptType = (String) query.get(QaScriptView.SCRIPT_TYPE);
            String fileExtension = getExtension(outputTypes, contentType);
            String resultFile =
                    Properties.tmpFolder + File.separatorChar + "gdem_q" + query_id + "_" + System.currentTimeMillis() + "."
                            + fileExtension;

            int queryId;
            try {
                queryId = Integer.parseInt(query_id);
            } catch (NumberFormatException n) {
                queryId = 0;
            }
            // if it is a XQuery script, then append the system folder
            if (queryId != JOB_VALIDATION
                    && queryFile.startsWith(Properties.gdemURL + "/" + Constants.QUERIES_FOLDER)) {
                queryFile =
                        Utils.Replace(queryFile, Properties.gdemURL + "/" + Constants.QUERIES_FOLDER,
                                Properties.queriesFolder + File.separator);
            }
            else if (queryId != JOB_VALIDATION
                    && ! queryFile.startsWith(Properties.gdemURL + "/" + Constants.QUERIES_FOLDER)) {
                queryFile = Properties.queriesFolder + File.separator + queryFile;
            }

            // TODO check
            sourceURL = HttpFileManager.getSourceUrlWithTicket("", sourceURL, true);

            long sourceSize = HttpFileManager.getSourceURLSize("", originalSourceURL, true);

            //save the job definition in the DB
            jobId = xqJobDao.startXQJob(sourceURL, queryFile, resultFile, queryId, scriptType);
            //
            LOGGER.debug(jobId + " : " + sourceURL + " size: " + sourceSize);

            scheduleJob(jobId, sourceSize, scriptType);
        } catch (SQLException e) {
            LOGGER.error("AnalyzeXMLFile:", e);
            throw new XMLConvException(e.getMessage());
        } catch (SchedulerException e) {
            LOGGER.error("AnalyzeXMLFile:", e);
            throw new XMLConvException(e.getMessage());
        } catch (URISyntaxException e) {
            LOGGER.error("AnalyzeXMLFile:", e);
            throw new XMLConvException(e.getMessage());
        }

        return jobId;
    }

    /**
     *  Reschedule a job with quartz
     * @param JobID the id of the job
     */
    public void rescheduleJob(String JobID) throws SchedulerException, SQLException, XMLConvException {

        String[] jobData = xqJobDao.getXQJobData(JobID);
        String url = jobData[0];
        if (url.indexOf(Constants.GETSOURCE_URL) > 0 && url.indexOf(Constants.SOURCE_URL_PARAM) > 0) {
            int idx = url.indexOf(Constants.SOURCE_URL_PARAM);
            url = url.substring(idx + Constants.SOURCE_URL_PARAM.length() + 1);
        }

        String scriptType = jobData[8];

        long sourceSize = HttpFileManager.getSourceURLSize("", url, true);

        JobDetail job1 = newJob(eionet.gdem.qa.QAJob.class)
                .withIdentity(JobID, "XQueryJob")
                .usingJobData("jobId", JobID)
                .requestRecovery()
                .build();

        // Define a Trigger that will fire "now", and not repeat
        Trigger trigger = newTrigger()
                .startNow()
                .build();

        // Reschedule the job
        // Heavy jobs go into a separate scheduler
        if (sourceSize > heavyJobThreshhold && !scriptType.equals(XQScript.SCRIPT_LANG_FME)) {
            Scheduler quartzScheduler = getQuartzHeavyScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }
        else {
            Scheduler quartzScheduler = getQuartzScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }

    }

    /**
     *  Schedule a job with quartz
     * @param JobID the id of the job
     */
    public void scheduleJob (String JobID, long sizeInBytes, String scriptType) throws SchedulerException {
        // ** Schedule the job with quartz to execute as soon as possibly.
        // only the job_id is needed for the job to be executed
        // Define an anonymous job
        JobDetail job1 = newJob(eionet.gdem.qa.QAJob.class)
                .withIdentity(JobID, "XQueryJob")
                .usingJobData("jobId", JobID )
                .requestRecovery()
                .build();

        // Define a Trigger that will fire "now", and not repeat
        Trigger trigger = newTrigger()
                .startNow()
                .build();

        // Schedule the job
        // Heavy jobs go into a separate scheduler
        if (sizeInBytes > heavyJobThreshhold && !scriptType.equals(XQScript.SCRIPT_LANG_FME)) {
            Scheduler quartzScheduler = getQuartzHeavyScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }
        else {
            Scheduler quartzScheduler = getQuartzScheduler();
            quartzScheduler.scheduleJob(job1, trigger);
        }

    }

    /**
     * Returns if script is active.
     *
     * @param query Query map
     * @return True if script is active.
     */
    private boolean isActive(Map query) {
        return query.get(QaScriptView.IS_ACTIVE).equals("1");
    }
}
