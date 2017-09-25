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

    /** Data Dictionary URL, used when generating XSLs. */
    public static final String DD_URL = null;

    /** Folder for OpenDocument helper files. */
    public static final String OPENDOCUMENT_DIR = null;
    /** Folder for temporary files. */
    public static final String TMP_DIR = null;

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

        CACHE_HTTP_SIZE = getLongProperty("cache.http.size");
        CACHE_TEMP_DIR = getStringProperty("cache.temp.dir");
        CACHE_HTTP_EXPIRY = 10;
        HTTP_CACHE_ENTRIES = 10;
        HTTP_CACHE_OBJECTSIZE = 10;
        HTTP_SOCKET_TIMEOUT = 10;
        HTTP_CONNECT_TIMEOUT = 10;
        HTTP_MANAGER_TOTAL = 10;
        HTTP_MANAGER_ROUTE = 10;
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
