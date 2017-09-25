package eionet.xmlconv.qa;

import eionet.propertyplaceholderresolver.CircularReferenceException;
import eionet.propertyplaceholderresolver.ConfigurationPropertyResolver;
import eionet.propertyplaceholderresolver.UnresolvedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 *
 */
public class Properties {

    private static ConfigurationPropertyResolver configurationService;

    public static final Logger LOGGER = LoggerFactory.getLogger(Properties.class);
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
    public static final String APP_HOME;
    public static final String XQ_SOURCE_PARAM_NAME = "source_url";
    public static final String XQ_SCRIPT_ID_PARAM = "script_id";
    public static final String CATALOG_PATH = "catalog.xml";
    public static final String XSL_DIRECTORY;
    public static final String QUERIES_DIR;
    public static final String SCHEMA_DIR;
    public static final String XMLCONV_URL;
    public static final String XMLFILE_DIR;


    public static final long QA_TIMEOUT;

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

    public static final String XGAWK_COMMAND;

    static {
        configurationService = (ConfigurationPropertyResolver) SpringApplicationContext.getBean("configurationPropertyResolver");

        APP_HOME = getStringProperty("app.home");
        XSL_DIRECTORY = APP_HOME + "/xsl/";
        QUERIES_DIR = APP_HOME + "/queries/";
        SCHEMA_DIR = APP_HOME + "/schema/";
        XMLFILE_DIR = APP_HOME + "/xml/";
        XMLCONV_URL = getStringProperty("app.xmlconv");


        CACHE_HTTP_EXPIRY = 10;
        HTTP_CACHE_ENTRIES = 10;
        HTTP_CACHE_OBJECTSIZE = 10;
        HTTP_SOCKET_TIMEOUT = 10;
        HTTP_CONNECT_TIMEOUT = 10;
        HTTP_MANAGER_TOTAL = 10;
        HTTP_MANAGER_ROUTE = 10;
        XGAWK_COMMAND = ""; /*getStringProperty("external.qa.command.xgawk");*/
        QA_TIMEOUT = 120000L;


        CACHE_HTTP_SIZE = getLongProperty("cache.http.size");
        CACHE_TEMP_DIR = getStringProperty("cache.temp.dir");
        /*CACHE_HTTP_EXPIRY = getIntProperty("cache.http.expiryinterval");
        HTTP_CACHE_ENTRIES = getIntProperty("http.cache.entries");
        HTTP_CACHE_OBJECTSIZE = getLongProperty("http.cache.objectsize");
        HTTP_SOCKET_TIMEOUT = getIntProperty("http.socket.timeout");
        HTTP_CONNECT_TIMEOUT = getIntProperty("http.connect.timeout");
        HTTP_MANAGER_TOTAL = getIntProperty("http.manager.total");
        HTTP_MANAGER_ROUTE = getIntProperty("http.manager.route");
        HTTP_TRANSFER_LOADBALANCER = getStringProperty("http.transfer.loadbalancer");*/
    }

    /**
     * Gets property value from key
     * @param key Key
     * @return Value
     */
    public static String getStringProperty(String key) {
        try {
            return configurationService.resolveValue(key);
        }
        catch (CircularReferenceException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
        catch (UnresolvedPropertyException ex) {
            LOGGER.error(ex.getMessage());
            return null;
        }
    }


    private static long getLongProperty(String key) {
        String value = getStringProperty(key);

        try {
            return Long.valueOf(value);
        } catch (NumberFormatException nfe) {
            LOGGER.error(nfe.getMessage());
            return 0L;
        }
    }

//
//
//
//
//    /**
//     * Load message property from resource bundle.
//     *
//     * @param key
//     *            Resource bundle key.
//     * @return String value.
//     * TODO: Fix this
//     */
//    public static String getMessage(String key) {
//        return null;
//    }
//
//    /**
//     * Load message property with parameters from resource bundle.
//     *
//     * @param key
//     *            Resource bundle key.
//     * @param replacement
//     *            Replacement array.
//     * @return
//     * TODO: Fix this
//     */
//    public static String getMessage(String key, Object[] replacement) {
//
//        String message = MessageFormat.format(getMessage(key), replacement);
//        if (message != null) {
//            return message;
//        }
//        return null;
//    }
}
