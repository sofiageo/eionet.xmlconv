package eionet.gdem.web.spring.scripts;

import javax.servlet.http.HttpServletRequest;

import eionet.gdem.Constants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Stores qa scripts list in the system cache.
 */
@Service
public class QAScriptListLoader {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(QAScriptListLoader.class);

    public static final String QASCRIPT_LIST_ATTR = "qascript.qascriptList";
    public static final String QASCRIPT_PERMISSIONS_ATTR = "qascript.permissions";
    private SchemaManager schemaManager;

    @Autowired
    public QAScriptListLoader(SchemaManager schemaManager) {
        this.schemaManager = schemaManager;
    }

    private QAScriptListHolder loadQAScriptList(HttpServletRequest httpServletRequest, boolean reload) throws DCMException {

        Object st = httpServletRequest.getSession().getServletContext().getAttribute(QASCRIPT_LIST_ATTR);
        if (st == null || !(st instanceof QAScriptListHolder) || reload) {
            st = new QAScriptListHolder();
            try {
                st = schemaManager.getSchemasWithQAScripts(null);
            } catch (DCMException e) {
                LOGGER.error("Error getting QA scripts list", e);
                throw e;
            }
            httpServletRequest.getSession().getServletContext().setAttribute(QASCRIPT_LIST_ATTR, st);
        }
        Object permissions = httpServletRequest.getSession().getAttribute(QASCRIPT_PERMISSIONS_ATTR);
        if (permissions == null || !(permissions instanceof QAScriptListHolder)) {
            loadPermissions(httpServletRequest);
        }

        return (QAScriptListHolder) st;
    }

    public void clearList(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().getServletContext().removeAttribute(QASCRIPT_LIST_ATTR);
    }

    public void reloadList(HttpServletRequest httpServletRequest) throws DCMException {
        loadQAScriptList(httpServletRequest, true);
    }

    public QAScriptListHolder getList(HttpServletRequest httpServletRequest) throws DCMException {
        return loadQAScriptList(httpServletRequest, false);
    }

    public void loadPermissions(HttpServletRequest httpServletRequest) {
        String user_name = (String) httpServletRequest.getSession().getAttribute("user");
        try {
            httpServletRequest.getSession().setAttribute(QASCRIPT_PERMISSIONS_ATTR, loadQAScriptPermissions(user_name));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error getting QA script permissions", e);
        }
    }

    public void clearPermissions(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(QASCRIPT_PERMISSIONS_ATTR);
    }

    public QAScriptListHolder loadQAScriptPermissions(String userName) throws Exception {
        QAScriptListHolder qa = new QAScriptListHolder();

        boolean ssiPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QUERIES_PATH, "i");
        boolean ssdPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QUERIES_PATH, "d");
        boolean wqiPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_WQ_PATH, "i");
        boolean wquPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QUERIES_PATH, "u");
        boolean qsiPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QASANDBOX_PATH, "i");
        boolean qsuPrm = SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QASANDBOX_PATH, "u");

        qa.setSsdPrm(ssdPrm);
        qa.setSsiPrm(ssiPrm);
        qa.setWqiPrm(wqiPrm);
        qa.setWquPrm(wquPrm);
        qa.setQsiPrm(qsiPrm);
        qa.setQsuPrm(qsuPrm);
        return qa;
    }
}
