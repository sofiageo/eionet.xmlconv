package eionet.gdem.web.filters;

import edu.yale.its.tp.cas.client.filter.CASFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * CAS Login Filter.
 *
 */
public class CASLoginFilter extends CASFilter {

    /** Static logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CASLoginFilter.class);
    /** FQN of this class. */
    private static final String CLASS_NAME = CASLoginFilter.class.getName();

    /*
     * (non-Javadoc)
     *
     * @see edu.yale.its.tp.cas.client.filter.CASFilter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig config) throws ServletException {
        LOGGER.info("Initializing " + CLASS_NAME + " ...");
        super.init(CASFilterConfig.getInstance(config));
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.yale.its.tp.cas.client.filter.CASFilter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
     * javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws ServletException, IOException {
        LOGGER.trace(CLASS_NAME + ".doFilter() invoked ...");
        super.doFilter(request, response, fc);
    }
}
