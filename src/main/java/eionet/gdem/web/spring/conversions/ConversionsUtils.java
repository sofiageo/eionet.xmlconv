package eionet.gdem.web.spring.conversions;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.conversions.model.Stylesheet;
import eionet.gdem.xml.services.IXmlCtx;
import eionet.gdem.xml.services.sax.SaxContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Utility methods for Add and Edit stylesheet actions.
 *
 * @author Enriko Käsper
 */
public class ConversionsUtils {
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionsUtils.class);


    /**
     * Creates a new Stylesheet dto object and fills the properties with values inserted to web form.
     * @param form StylesheetForm
     * @param httpServletRequest HTTP servlet request object.
     * @return eionet.gdem.dto.Stylesheet object
     */
    public static Stylesheet convertFormToStylesheetDto(StylesheetForm form, HttpServletRequest httpServletRequest) {

        Stylesheet stylesheet = new Stylesheet();
        stylesheet.setConvId(form.getStylesheetId());
        stylesheet.setDescription(form.getDescription());
        stylesheet.setType(form.getOutputtype());
        stylesheet.setDependsOn(form.getDependsOn());
        stylesheet.setXslFileName(form.getXslFileName());
        stylesheet.setXslContent(form.getXslContent());

        if (httpServletRequest.getParameterValues("newSchemas") != null) {
            form.setNewSchemas(httpServletRequest.getParameterValues("newSchemas"));
            stylesheet.setSchemaUrls(form.getNewSchemas());
        }
        if (httpServletRequest.getParameterValues("schemaIds") != null) {
            stylesheet.setSchemaIds(Arrays.asList(httpServletRequest.getParameterValues("schemaIds")));
        }
        return stylesheet;
    }

    /**
     * Check the well-formedness of uploaded/inserted XSL file.
     * If the file is not well formed XML, then adds an error ito the list of Struts AcitonMessages.
     *
     * @param stylesheet Stylehseet dto
     * @param errors Struts ActionMessages
     */
    public static void validateXslFile(Stylesheet stylesheet, SpringMessages errors) {
        try {
            IXmlCtx x = new SaxContext();
            x.setWellFormednessChecking();
            x.checkFromString(stylesheet.getXslContent());
        } catch (Exception e) {
            LOGGER.error("stylesheet not valid", e);
            // TODO change to use messaging service
            //("label.stylesheet.error.notvalid")
            errors.add("Stylesheet content is not valid");
        }
    }
}
