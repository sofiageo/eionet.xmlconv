package eionet.xmlconv.conversions;

/**
 * Temporary Properties class. Should be replaced by an external configuration service (probably)
 *
 */
public class Properties {

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
}
