package eionet.gdem.qa.engines;

import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Properties;
import eionet.gdem.SpringApplicationContext;
import eionet.gdem.exceptions.XMLConvException;
import eionet.gdem.qa.QAResultPostProcessor;
import eionet.gdem.qa.model.XQScript;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Execute an FME query. Runs synchronously.
 *
 * @author Bilbomatica
 */
@Service
public class FMEQueryEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(FMEQueryEngine.class);

    private CloseableHttpClient client_ = null;

    private static Builder requestConfigBuilder = null;

    /**
     * Security token for authentication.
     */
    private String token_ = null;

    private String fmeUrl = null;

    private String encoding = null;
    private String outputType = null;
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String DEFAULT_OUTPUTTYPE = "html";
    private static final String HTML_CONTENT_TYPE = "html";
    private static final String XML_CONTENT_TYPE = "xml";

    private static final RestTemplate restTemplate = (RestTemplate) SpringApplicationContext.getBean("restTemplate");
    private QAResultPostProcessor postProcessor;

    @Autowired
    public FMEQueryEngine(QAResultPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }

    protected void runQuery(XQScript script, OutputStream result) throws XMLConvException {

        int count = 0;
        int retryMilisecs = Properties.fmeRetryHours * 60 * 60 * 1000;
        int timeoutMilisecs = Properties.fmeTimeout;
        int retries = 70;
        boolean interrupted = false;
        URI fmeUri = null;
        int count204 = 0;
        getConnectionInfo();

        fmeUrl = script.getScriptSource() + "?source_xml=" + script.getOrigFileUrl() + "&token={token}";

        while (count < retries && !interrupted) {
            try {
                HttpHeaders requestHeaders = new HttpHeaders();
                requestHeaders.setAccept(Collections.singletonList(new MediaType("application", "json")));
                org.springframework.http.HttpEntity<?> requestEntity = new org.springframework.http.HttpEntity<>(requestHeaders);

                // Add the Jackson message converter
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                ResponseEntity<String> responseEntity = restTemplate.exchange(fmeUrl, HttpMethod.GET, requestEntity, String.class, token_);

                HttpStatus statusCode = responseEntity.getStatusCode();
                if (statusCode.value() == 204) {
                    // Job submitted
                    count204++;
                    if (count204 >= 3) throw new XMLConvException("The FME Server declined to schedule the job.");
                } else if (statusCode.value() == 200) {
                    String responseBody = responseEntity.getBody();
                    ObjectMapper mapper = new ObjectMapper();
                    FmeApiDto fmeResponse = mapper.readValue(responseBody, FmeApiDto.class);

                    if (fmeResponse.getExecutionStatus().getStatusId() == 0) { // Job Ready
                        //IOUtils.copy( fmeResponse.getFeedbackContent().getBytes("UTF-8") , result);
                        result.write(fmeResponse.getFeedbackContent().getBytes("UTF-8"));
                        return;
                    } else if (fmeResponse.getExecutionStatus().getStatusId() == 0) { // Job Pending
                        try {
                            wait(count);
                        } catch (InterruptedException e) {
                            throw new XMLConvException("The FME Job has been interrupted.");
                        }
                    }
                } else {
                    throw new XMLConvException("Invalid status code from remote server: " + statusCode.toString());

                }
            } catch (IOException e) {
                throw new XMLConvException(e.getMessage());
            }
            //
            count++;
        }

    }

    public String getResult(XQScript script) throws XMLConvException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        String res = "";
        getResult(script, result);
        try {
            res = result.toString(DEFAULT_ENCODING);
        } catch (Exception e) {
            LOGGER.error("Error while converting QA result" + e);
        }
        // add "red coloured warning" if script is expired
        if (script.getOutputType().equals(XQScript.SCRIPT_RESULTTYPE_HTML) && script.getSchema() != null) {
            res = postProcessor.processQAResult(res, script.getSchema());
        }
        return res;
    }

    public void setOutputType(String outputType) {
        outputType = (outputType == null) ? DEFAULT_OUTPUTTYPE : outputType.trim().toLowerCase();
        outputType = (outputType.equals("txt")) ? "text" : outputType;

        if (outputType.equals("xml") || outputType.equals("html") || outputType.equals("text") || outputType.equals("xhtml")) {
            this.outputType = outputType;
        } else {
            this.outputType = DEFAULT_OUTPUTTYPE;
        }
    }

    public void getResult(XQScript script, OutputStream out) throws XMLConvException {
        try {
            setOutputType(script.getOutputType());
            runQuery(script, out);
        } catch (Exception e) {
            throw new XMLConvException(e.getMessage(), e);
        }
    }

    private void wait(int count) throws InterruptedException {
        if (count < 10) {
            Thread.sleep(10 * 1000);
            return;
        }
        if (count < 25) {
            Thread.sleep(20 * 1000);
            return;
        }
        if (count < 40) {
            Thread.sleep(60 * 1000);
            return;
        }
        if (count < 50) {
            Thread.sleep(120 * 1000);
            return;
        }
        Thread.sleep(600 * 1000);
    }

    /**
     * Gets a user token from the FME server.
     *
     * @throws XMLConvException If an error occurs.
     */
    private void getConnectionInfo() throws XMLConvException {

        HttpPost method = null;
        CloseableHttpResponse response = null;

        try {
            // We must first generate a security token for authentication
            // purposes
            fmeUrl = "http://" + Properties.fmeHost + ":" + Properties.fmePort
                    + "/fmetoken/generate";

            java.net.URI uri = new URIBuilder(fmeUrl)
                    .addParameter("user", Properties.fmeUser)
                    .addParameter("password", Properties.fmePassword)
                    .addParameter("expiration", Properties.fmeTokenExpiration)
                    .addParameter("timeunit", Properties.fmeTokenTimeunit).build();
            method = new HttpPost(uri);
            client_ = HttpClients.createDefault();
            response = client_.execute(method);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                token_ = new String(IOUtils.toByteArray(stream), StandardCharsets.UTF_8);
                IOUtils.closeQuietly(stream);
            } else {
                LOGGER.error("FME authentication failed. Could not retrieve a Token");
                throw new XMLConvException("FME authentication failed");
            }
        } catch (Exception e) {
            throw new XMLConvException(e.toString(), e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }

    }

}
