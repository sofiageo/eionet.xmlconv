package eionet.gdem.web.spring.stylesheet;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import eionet.gdem.Constants;
import eionet.gdem.services.SchemaManager;
import eionet.gdem.conversions.StylesheetManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.spring.scripts.QAScriptListHolder;
import eionet.gdem.web.spring.scripts.QAScriptListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Loads stylesheet list and stores it in the system cache.
 *
 *
 */
@Service
public class StylesheetListLoader {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(QAScriptListLoader.class);
    public static final String CONVERSION_SCHEMAS_ATTR = "conversion.schemas";
    /** Context key attribute name holding handcoded stylesheet list. */
    public static final String STYLESHEET_LIST_ATTR = "stylesheet.stylesheetListHolder";
    /** Context key attribute name holding generated stylesheet list. */
    public static final String STYLESHEET_GENERATED_LIST_ATTR = "stylesheet.generatedListHolder";
    /** Session key attribute name holding user permissions. */
    public static final String STYLESHEET_PERMISSIONS_ATTR = "stylesheet.permissions";

    private StylesheetManager stylesheetManager;
    private SchemaManager schemaManager;

    @Autowired
    public StylesheetListLoader(StylesheetManager stylesheetManager, SchemaManager schemaManager) {
        this.stylesheetManager = stylesheetManager;
        this.schemaManager = schemaManager;
    }

    /**
     * Expire time in milliseconds for updating generated stylesheets list from DD
     */
    public static final long STYLESHEET_GENERATED_LIST_ATTR_EXPIRE = 1800000L;

    public static long generatedListTimestamp = 0L;

    /**
     * Reload the schemas and handcoded stylesheet lists from cache
     *
     * @param httpServletRequest
     * @throws DCMException
     */
    public void reloadStylesheetList(HttpServletRequest httpServletRequest) throws DCMException {
        loadStylesheetList(httpServletRequest, true);
    }

    /**
     * Return the schemas and handcoded stylesheets lists from cache
     *
     * @param httpServletRequest
     * @return StylesheetListHolder
     * @throws DCMException
     */
    public StylesheetListHolder getStylesheetList(HttpServletRequest httpServletRequest) throws DCMException {
        return loadStylesheetList(httpServletRequest, false);
    }

    /**
     * Reload the schemas and generated stylesheets lists from cache
     *
     * @param httpServletRequest
     * @throws DCMException
     */
    public void reloadGeneratedList(HttpServletRequest httpServletRequest) throws DCMException {
        loadStylesheetGeneratedList(httpServletRequest, true);
    }

    /**
     * Return the schemas and generated stylesheets lists from cache
     *
     * @param httpServletRequest
     * @return StylesheetListHolder
     * @throws DCMException
     */
    public StylesheetListHolder getGeneratedList(HttpServletRequest httpServletRequest) throws DCMException {
        return loadStylesheetGeneratedList(httpServletRequest, false);
    }

    /**
     * Reload the distinct list of schemas that contain styelsheets in cache
     *
     * @param httpServletRequest
     * @throws DCMException
     */
    public void reloadConversionSchemasList(HttpServletRequest httpServletRequest) throws DCMException {
        loadConversionSchemasList(httpServletRequest, true);
    }

    /**
     * Get the distinct list of schemas that contain styelsheets from cache
     *
     * @param httpServletRequest
     * @throws DCMException
     */
    public List<Schema> getConversionSchemasList(HttpServletRequest httpServletRequest) throws DCMException {
        return loadConversionSchemasList(httpServletRequest, false);
    }

    public void clearLists(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().getServletContext().removeAttribute(STYLESHEET_LIST_ATTR);
        httpServletRequest.getSession().getServletContext().removeAttribute(STYLESHEET_GENERATED_LIST_ATTR);
        httpServletRequest.getSession().getServletContext().removeAttribute(CONVERSION_SCHEMAS_ATTR);
    }

    public void loadPermissions(HttpServletRequest httpServletRequest) {
        String user_name = (String) httpServletRequest.getSession().getAttribute("user");
        try {
            httpServletRequest.getSession().setAttribute(STYLESHEET_PERMISSIONS_ATTR, loadStylesheetPermissions(user_name));
        } catch (Exception e) {
            LOGGER.error("Error getting QA script permissions", e);
        }
    }

    public void clearPermissions(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(STYLESHEET_PERMISSIONS_ATTR);
    }

    public StylesheetListHolder loadStylesheetPermissions(String userName) throws Exception {
        StylesheetListHolder st = new StylesheetListHolder();
        boolean ssiPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_STYLESHEETS_PATH, "i");
        boolean ssdPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_STYLESHEETS_PATH, "d");
        boolean convPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_TESTCONVERSION_PATH, "x");
        st.setSsdPrm(ssdPrm);
        st.setSsiPrm(ssiPrm);
        st.setConvPrm(convPrm);
        return st;
    }

    /**
     * Load stylesheets info and permissions store it in the context.
     * @param httpServletRequest
     * @param reload
     * @return
     * @throws DCMException
     */
    private StylesheetListHolder loadStylesheetList(HttpServletRequest httpServletRequest, boolean reload) throws DCMException {

        Object holder = httpServletRequest.getSession().getServletContext().getAttribute(STYLESHEET_LIST_ATTR);
        if (holder == null || !(holder instanceof StylesheetListHolder) || reload) {
            StylesheetListHolder stylesheetsHolder = new StylesheetListHolder();
            try {
                List<Stylesheet> stylesheets = stylesheetManager.getStylesheets();
                stylesheetsHolder.setStylesheetList(stylesheets);
                holder = stylesheetsHolder;
            } catch (DCMException e) {
                LOGGER.error("Error getting stylesheet list", e);
                throw e;
            }
            httpServletRequest.getSession().getServletContext().setAttribute(STYLESHEET_LIST_ATTR, stylesheetsHolder);
        }

        Object permissions = httpServletRequest.getSession().getServletContext().getAttribute(STYLESHEET_PERMISSIONS_ATTR);
        if (permissions == null || !(permissions instanceof QAScriptListHolder)) {
            loadPermissions(httpServletRequest);
        }
        return (StylesheetListHolder) holder;
    }

    private List<Schema> loadConversionSchemasList(HttpServletRequest httpServletRequest, boolean reload) throws DCMException {

        Object schemas = httpServletRequest.getSession().getServletContext().getAttribute(CONVERSION_SCHEMAS_ATTR);
        if (schemas == null || !(schemas instanceof List) || reload) {
            schemas = new ArrayList<Schema>();

            try {
                schemas = schemaManager.getSchemas();
            } catch (DCMException e) {
                LOGGER.error("Error getting schemas list", e);
                throw e;
            }
            httpServletRequest.getSession().getServletContext().setAttribute(CONVERSION_SCHEMAS_ATTR, schemas);
        }

        return (List<Schema>) schemas;
    }

    private StylesheetListHolder loadStylesheetGeneratedList(HttpServletRequest httpServletRequest, boolean reload) throws DCMException {

        Object st = httpServletRequest.getSession().getServletContext().getAttribute(STYLESHEET_GENERATED_LIST_ATTR);
        if (st == null || !(st instanceof StylesheetListHolder) || reload || generatedListIsExpired()) {
            st = new StylesheetListHolder();
            try {
                st = schemaManager.getSchemas("generated");
            } catch (DCMException e) {
                LOGGER.error("Error getting stylesheet generated list", e);
                throw e;
            }
            httpServletRequest.getSession().getServletContext().setAttribute(STYLESHEET_GENERATED_LIST_ATTR, st);
        }
        Object permissions = httpServletRequest.getSession().getAttribute(STYLESHEET_PERMISSIONS_ATTR);
        if (permissions == null || !(permissions instanceof QAScriptListHolder)) {
            loadPermissions(httpServletRequest);
        }

        return (StylesheetListHolder) st;
    }

    /**
     * @return
     */
    private boolean generatedListIsExpired() {
        boolean isExpired = false;
        if ((System.currentTimeMillis() - STYLESHEET_GENERATED_LIST_ATTR_EXPIRE > generatedListTimestamp)) {
            isExpired = true;
            generatedListTimestamp = System.currentTimeMillis();
        }
        return isExpired;
    }
}
