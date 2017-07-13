package eionet.gdem.web.filters;

import eionet.gdem.Properties;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

/**
 * CAS Filter Config.
 *
 */
public class CASFilterConfig extends Hashtable<String, String> implements FilterConfig {
    private static volatile CASFilterConfig instance;
    private String filterName;
    private ServletContext servletContext;

    /**
     *
     * @param defaultConfig
     */
    private CASFilterConfig(FilterConfig defaultConfig) {
        super();
        if (defaultConfig != null) {
            // load default configuration supplied by CAS
            for (Enumeration names = defaultConfig.getInitParameterNames(); names.hasMoreElements();) {
                String name = names.nextElement().toString();
                put(name, defaultConfig.getInitParameter(name));
            }

            // set filter name and servlet context as they came from default config
            filterName = defaultConfig.getFilterName();
            servletContext = defaultConfig.getServletContext();
        }

        // overwrite with DD's own values
        for (CASInitParam casInitParam : CASInitParam.values()) {
            String name = casInitParam.toString();
            String temp = Properties.getStringProperty(name);
            if (temp != null) {
                put(name, Properties.getStringProperty(name));
            }
        }
    }

    /**
     *
     * @param defaultConfig
     */
    public static CASFilterConfig getInstance(FilterConfig defaultConfig) {
        if (instance == null) {
            synchronized (CASFilterConfig.class) {
                // double-checked locking pattern
                // (http://www.ibm.com/developerworks/java/library/j-dcl.html)
                if (instance == null) {
                    instance = new CASFilterConfig(defaultConfig);
                }
            }
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.FilterConfig#getFilterName()
     */
    public String getFilterName() {
        return filterName;
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.FilterConfig#getInitParameter(java.lang.String)
     */
    public String getInitParameter(String paramName) {
        return get(paramName);
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.FilterConfig#getInitParameterNames()
     */
    public Enumeration<String> getInitParameterNames() {
        return keys();
    }

    /*
     * (non-Javadoc)
     * @see javax.servlet.FilterConfig#getServletContext()
     */
    public ServletContext getServletContext() {
        return servletContext;
    }
}
