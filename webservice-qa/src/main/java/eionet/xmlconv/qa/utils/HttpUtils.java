package eionet.xmlconv.qa.utils;

import eionet.xmlconv.qa.exceptions.DCMException;
import eionet.xmlconv.qa.http.HttpDefaultClientFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * HTTP Utilities.
 * @author Enriko Käsper, Tieto Estonia HttpUtils
 */

public final class HttpUtils {

    public static final String EXCEPTION_SCHEMAOPEN_ERROR = "label.schemaopen.error";

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * Private constructor to deal with reflection
     */
    private HttpUtils() {
        throw new AssertionError();
    }

    /**
     * Downloads remote file
     * @param url URL
     * @return Downloaded file
     * @throws DCMException If an error occurs.
     * @throws IOException If an error occurs.
     */
    public static byte[] downloadRemoteFile(String url) throws DCMException, IOException {
        byte[] responseBody = null;
        CloseableHttpClient client = HttpDefaultClientFactory.getInstance();

        HttpGet method = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.error("Method failed: " + response.getStatusLine().getReasonPhrase());
                throw new DCMException(EXCEPTION_SCHEMAOPEN_ERROR, response.getStatusLine().getReasonPhrase());
            }
            InputStream instream = entity.getContent();
            responseBody = IOUtils.toByteArray(instream);
        } catch (IOException e) {
            LOGGER.error("Fatal transport error: " + e.getMessage());
            throw e;
        } finally {
            response.close();
            method.releaseConnection();
        }
        return responseBody;
    }

    /**
     * Method checks whether the resource behind the given URL exist. The method calls HEAD request and if the resonse code is 200,
     * then returns true. If exception is thrown or response code is something else, then the result is false.
     *
     * @param url URL
     * @return True if resource behind the url exists.
     */
    public static boolean urlExists(String url) {

        CloseableHttpClient client = HttpDefaultClientFactory.getInstance();
        HttpHead method = new HttpHead(url);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode == HttpStatus.SC_OK;
        } catch (IOException e) {
            LOGGER.error("Fatal transport error: " + e.getMessage());
            return false;
        } finally {
            method.releaseConnection();
            try {
                response.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }
}
