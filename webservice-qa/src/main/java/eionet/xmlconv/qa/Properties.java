package eionet.xmlconv.qa;

import java.text.MessageFormat;

/**
 *
 */
public class Properties {

    /** Folder for temporary files. */
    public static final String TMP_DIR = null;
    public static final String TMP_FILE_PREFIX = "xmlconv_tmp_";

    // Public constants for SourceFileAdapter
    public static final String GETSOURCE_URL = "/s/getsource";
    public static final String AUTH_PARAM = "auth";
    public static final String TICKET_PARAM = "ticket";
    public static final String SOURCE_URL_PARAM = "source_url";

    /** Date pattern used for displaying date values on UI. */
    public static final String dateFormatPattern = "dd MMM yyyy";
    /** Time pattern used for displaying time values on UI. */
    public static final String timeFormatPattern = "dd MMM yyyy hh:mm:ss";

    /**
     * Default parameter name of the source URL to be given to the XQuery script by the QA service
     */
    public static final String XQ_SOURCE_PARAM_NAME = "source_url";
    public static final String XQ_SCRIPT_ID_PARAM = "script_id";
    public static final String CATALOG_PATH = "catalog.xml";
    public static final String XSL_DIRECTORY = "xsl/";


    /** Cache Configuration */
    public static final String CACHE_TEMP_DIR;
    public static final long CACHE_HTTP_SIZE;
    public static final int CACHE_HTTP_EXPIRY;
    public static final int HTTP_CACHE_ENTRIES;
    public static final long HTTP_CACHE_OBJECTSIZE;
    public static final int HTTP_SOCKET_TIMEOUT;
    public static final int HTTP_CONNECT_TIMEOUT;
    public static final int HTTP_MANAGER_TOTAL;
    public static final int HTTP_MANAGER_ROUTE;

    static {
        CACHE_TEMP_DIR = "";
        CACHE_HTTP_SIZE = 0l;
        CACHE_HTTP_EXPIRY = 10;
        HTTP_CACHE_ENTRIES = 10;
        HTTP_CACHE_OBJECTSIZE = 10;
        HTTP_SOCKET_TIMEOUT = 10;
        HTTP_CONNECT_TIMEOUT = 10;
        HTTP_MANAGER_TOTAL = 10;
        HTTP_MANAGER_ROUTE = 10;
    }

    /**
     * Load message property from resource bundle.
     *
     * @param key
     *            Resource bundle key.
     * @return String value.
     * TODO: Fix this
     */
    public static String getMessage(String key) {
        return null;
    }

    /**
     * Load message property with parameters from resource bundle.
     *
     * @param key
     *            Resource bundle key.
     * @param replacement
     *            Replacement array.
     * @return
     * TODO: Fix this
     */
    public static String getMessage(String key, Object[] replacement) {

        String message = MessageFormat.format(getMessage(key), replacement);
        if (message != null) {
            return message;
        }
        return null;
    }
}