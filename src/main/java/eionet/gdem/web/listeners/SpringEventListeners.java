package eionet.gdem.web.listeners;

import edu.yale.its.tp.cas.client.filter.CASFilter;
import eionet.gdem.Properties;
import eionet.gdem.dto.ConvType;
import eionet.gdem.qa.model.XQScript;
import eionet.gdem.web.spring.scripts.QAScriptListLoader;
import eionet.gdem.web.spring.stylesheet.StylesheetListLoader;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
@Component
public class SpringEventListeners {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringEventListeners.class);
    private QAScriptListLoader qaScriptListLoader;
    private StylesheetListLoader stylesheetListLoader;

    @Autowired
    public SpringEventListeners(QAScriptListLoader qaScriptListLoader, StylesheetListLoader stylesheetListLoader) {
        this.qaScriptListLoader = qaScriptListLoader;
        this.stylesheetListLoader = stylesheetListLoader;
    }

    @EventListener
    public void handleContextEvent(ContextRefreshedEvent e) {
        ApplicationContext appContext = e.getApplicationContext();
        if (!(appContext instanceof WebApplicationContext)) {
            return;
        }
        WebApplicationContext ctx = (WebApplicationContext) e.getApplicationContext();
        ServletContext context = ctx.getServletContext();
        try {
            Properties.metaXSLFolder = context.getRealPath("/dcm");
            Properties.convFile = context.getRealPath("/dcm/conversions.xml");
            Properties.odsFolder = context.getRealPath("/opendoc/ods");
            Properties.appHome = context.getRealPath("/WEB-INF/classes");
            Properties.contextPath = context.getContextPath();

            context.setAttribute("qascript.resulttypes",
                    loadConvTypes(XQScript.SCRIPT_RESULTTYPES));
            context.setAttribute("qascript.scriptlangs", loadConvTypes(XQScript.SCRIPT_LANGS));
            context.setAttribute(QAScriptListLoader.QASCRIPT_PERMISSIONS_ATTR,
                    qaScriptListLoader.loadQAScriptPermissions(null));
            context.setAttribute(StylesheetListLoader.STYLESHEET_PERMISSIONS_ATTR,
                    stylesheetListLoader.loadStylesheetPermissions(null));
            context.setInitParameter(CASFilter.LOGIN_INIT_PARAM, "https://sso.eionet.europa.eu/login");

        } catch (Exception e1) {
            LOGGER.error("An exception occurred while creating context" + e1);
        }
    }

    /**
     * Gets conversion types
     * @param types Conversion types
     * @return List of conversion types
     */
    public static List<ConvType> loadConvTypes(String[] types) {

        List<ConvType> l = new ArrayList<ConvType>(types.length);

        for (String type : types) {
            ConvType ct = new ConvType();
            ct.setConvType(type);
            l.add(ct);
        }
        return l;
    }

}
