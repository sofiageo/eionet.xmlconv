package eionet.xmlconv.conversions.cache;

import eionet.xmlconv.conversions.Properties;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.*;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import java.util.Collections;
import java.util.List;

/**
 * Creates and manages cache.
 * @author George Sofianos
 */
public final class CacheManagerUtil {

    /**
     * Public constructor
     */
    public CacheManagerUtil() {
        // do nothing
    }
    /**
     * Application (main) cache name.
     */
    public static final String APPLICATION_CACHE = "ApplicationCache";

    public static final String HTTP_CACHE = "http-cache";

    /** Data Dictionary tables data cache name. */
    private static final String DD_TABLES_CACHE = "ddTables";

    private static CacheManager cacheManager;

    public static CacheManager getCacheManager() {
        return cacheManager;
    }

    public static Cache getHttpCache() {
        return cacheManager.getCache(HTTP_CACHE);
    }

    /**
     * Cache manager initializer. Used by Spring DI.
     */
    public void initializeCacheManager() {
        Configuration cacheManagerConfig = new Configuration()
                .diskStore(new DiskStoreConfiguration()
                .path(Properties.CACHE_TEMP_DIR));
        cacheManager = new CacheManager(cacheManagerConfig);
        Cache appCache = new Cache(new CacheConfiguration(APPLICATION_CACHE, 10000)
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                .eternal(true));
        cacheManager.addCache(appCache);
        Cache httpCache = new Cache(new CacheConfiguration()
                .name(HTTP_CACHE)
                .maxEntriesLocalHeap(1)
                .maxBytesLocalDisk(Properties.CACHE_HTTP_SIZE, MemoryUnit.MEGABYTES)
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                .diskExpiryThreadIntervalSeconds(Properties.CACHE_HTTP_EXPIRY)
                .persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP)));
        cacheManager.addCache(httpCache);
    }

    /**
     * Used to destroy the cache manager. Used by Spring DI.
     */
    public void destroyCacheManager() {
        cacheManager.shutdown();
    }
}
