package eionet.xmlconv.conversions;

import eionet.propertyplaceholderresolver.CircularReferenceException;
import eionet.propertyplaceholderresolver.ConfigurationPropertyResolver;
import eionet.propertyplaceholderresolver.UnresolvedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * Temporary Properties class. Should be replaced by an external configuration service (probably)
 *
 */
public class Properties {

    private static ConfigurationPropertyResolver configurationService;
    public static final Logger LOGGER = LoggerFactory.getLogger(Properties.class);

    public static final String RUNTIME_PATH;

    /** Data Dictionary URL, used when generating XSLs. */
    public static final String DD_URL = null;

    /** Folder for OpenDocument helper files. */
    public static final String OPENDOCUMENT_DIR = null;
    /** Folder for temporary files. */
    public static final String TMP_DIR;
    public static final String XSL_DIR = "/xsl";

    /** Date pattern used for displaying date values on UI. */
    public static final String dateFormatPattern = "dd MMM yyyy";
    /** Time pattern used for displaying time values on UI. */
    public static final String timeFormatPattern = "dd MMM yyyy hh:mm:ss";

    // Public constants for SourceFileAdapter
    public static final String GETSOURCE_URL = "/s/getsource";
    public static final String AUTH_PARAM = "auth";
    public static final String TICKET_PARAM = "ticket";
    public static final String SOURCE_URL_PARAM = "source_url";

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
        configurationService = (ConfigurationPropertyResolver) SpringApplicationContext.getBean("configurationPropertyResolver");

        RUNTIME_PATH = getStringProperty("runtime.path");
        TMP_DIR = getStringProperty("paths.tmp.dir");


        CACHE_TEMP_DIR = getStringProperty("cache.temp.dir");
        CACHE_HTTP_SIZE = getLongProperty("cache.http.size");
        CACHE_HTTP_EXPIRY = getIntProperty("cache.http.expiryinterval");
        HTTP_CACHE_ENTRIES = getIntProperty("http.cache.entries");
        HTTP_CACHE_OBJECTSIZE = getLongProperty("http.cache.objectsize");
        HTTP_SOCKET_TIMEOUT = getIntProperty("http.socket.timeout");
        HTTP_CONNECT_TIMEOUT = getIntProperty("http.connect.timeout");
        HTTP_MANAGER_TOTAL = getIntProperty("http.manager.total");
        HTTP_MANAGER_ROUTE = getIntProperty("http.manager.route");

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

    /**
     * Gets property numeric value from key
     * @param key Key
     * @return Value
     */
    private static int getIntProperty(String key) {
        String value = getStringProperty(key);

        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException nfe) {
            LOGGER.error(nfe.getMessage());
            return 0;
        }
    }

}
