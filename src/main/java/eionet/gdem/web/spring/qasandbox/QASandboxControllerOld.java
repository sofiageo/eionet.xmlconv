package eionet.gdem.web.spring.qasandbox;

import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.XMLConvException;
import eionet.gdem.qa.QARestService;
import eionet.gdem.qa.QAService;
import eionet.gdem.qa.model.ValidationResult;
import eionet.gdem.conversions.ConvTypeManager;
import eionet.gdem.qa.QAScriptManager;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import eionet.gdem.conversions.model.ConvType;
import eionet.gdem.dto.CrFileDto;
import eionet.gdem.dto.QAScriptDto;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.model.XQScript;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.InputAnalyser;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.scripts.QAScriptListHolder;
import eionet.gdem.web.spring.scripts.QAScriptListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
@Controller
@RequestMapping("/qaSandboxOld")
public class QASandboxControllerOld {

    private static final Logger LOGGER = LoggerFactory.getLogger(QASandboxControllerOld.class);
    private MessageService messageService;
    private QAScriptManager qaScriptManager;
    private SchemaManager schemaManager;
    private WorkqueueManager workqueueManager;
    private ConvTypeManager convTypeManager;
    private QAScriptListLoader qaScriptListLoader;
    private QAService qaService;
    private QARestService qaRestService;

    @Autowired
    public QASandboxControllerOld(MessageService messageService, QAScriptManager qaScriptManager, SchemaManager schemaManager, WorkqueueManager workqueueManager, ConvTypeManager convTypeManager, QAScriptListLoader qaScriptListLoader, QAService qaService, QARestService qaRestService) {
        this.messageService = messageService;
        this.qaScriptManager = qaScriptManager;
        this.schemaManager = schemaManager;
        this.workqueueManager = workqueueManager;
        this.convTypeManager = convTypeManager;
        this.qaScriptListLoader = qaScriptListLoader;
        this.qaService = qaService;
        this.qaRestService = qaRestService;
    }

    @GetMapping
    public String index(Model model, HttpServletRequest request) {

        SpringMessages errors = new SpringMessages();
        QASandboxForm form = new QASandboxForm();

/*        boolean resetForm = true;

        if (httpServletRequest.getParameter("reset") != null) {
            resetForm = !"false".equals(httpServletRequest.getParameter("reset"));
        }
        if (resetForm) {
            cForm.resetAll(actionMapping, httpServletRequest);
        }
*/

        try {
            model.addAttribute("schemaList", qaScriptListLoader.getList(request));
            model.addAttribute("QASandboxForm", form);
        } catch (DCMException e) {
            LOGGER.error("QA Sandbox form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/qaSandboxOld/view";
    }

    @PostMapping("/findQAScripts")
    public String find(@ModelAttribute QASandboxForm cForm, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();

        String schemaUrl = cForm.getSchemaUrl();
        Schema schema = cForm.getSchema();

        if (Utils.isNullStr(schemaUrl)) {
            errors.add(messageService.getMessage("error.qasandbox.missingSchemaUrl"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandbox";
        }
        try {
            // cForm.setScriptId(null);
            String schemaId = schemaManager.getSchemaId(schemaUrl);
            QAScriptListHolder qaScripts = schemaManager.getSchemasWithQAScripts(schemaId);

            if (qaScripts != null && !Utils.isNullList(qaScripts.getQascripts())) {
                Schema newSchema = qaScripts.getQascripts().get(0);
                if (schema == null || !schema.equals(newSchema)) {
                    cForm.setSchema(newSchema);
                } else {
                    schema.setDoValidation(newSchema.isDoValidation());
                    schema.setQascripts(newSchema.getQascripts());
                    cForm.setSchema(schema);
                }
            }
            cForm.setShowScripts(true);
            if (Utils.isNullStr(cForm.getScriptId())) {
                if (Utils.isNullList(cForm.getSchema().getQascripts()) && cForm.getSchema().isDoValidation()) {
                    cForm.setScriptId("-1");
                } else if (!Utils.isNullList(cForm.getSchema().getQascripts()) && cForm.getSchema().getQascripts().size() == 1
                        && !cForm.getSchema().isDoValidation()) {
                    cForm.setScriptId(cForm.getSchema().getQascripts().get(0).getScriptId());
                }
            }
        } catch (DCMException e) {
            // e.printStackTrace();
            LOGGER.error("Error searching XML files", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandbox";
        }

        return "redirect:/qaSandboxOld/find";
    }

    @PostMapping("/addToWorkqueue")
    public String addToWorkQueue(@ModelAttribute QASandboxForm cForm, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        String sourceUrl = cForm.getSourceUrl();
        String content = cForm.getScriptContent();
        String scriptType = cForm.getScriptType();
        String schemaUrl = cForm.getSchemaUrl();

        if (Utils.isNullStr(sourceUrl)) {
            errors.add(messageService.getMessage("error.qasandbox.missingUrl"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandbox";
        }
        if (Utils.isNullStr(content) && !cForm.isShowScripts()) {
            errors.add(messageService.getMessage("error.qasandbox.missingContent"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandbox";
        }
        if (Utils.isNullStr(schemaUrl) && cForm.isShowScripts()) {
            errors.add(messageService.getMessage("error.qasandbox.error.qasandbox.missingSchemaUrl"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandbox";
        }
        if (!Utils.isURL(sourceUrl)) {
            errors.add(messageService.getMessage("error.qasandbox.notUrl"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandbox";
        }
        try {
            String userName = (String) session.getAttribute("user");
            if (cForm.isShowScripts()) {
                List<String> jobIds = workqueueManager.addSchemaScriptsToWorkqueue(userName, sourceUrl, schemaUrl);
                messages.add(messageService.getMessage("message.qasandbox.jobsAdded", jobIds.toString()));
            } else {
                String jobId = workqueueManager.addQAScriptToWorkqueue(userName, sourceUrl, content, scriptType);
                messages.add(messageService.getMessage("message.qasandbox.jobAdded", jobId));
            }
        } catch (DCMException e) {
            LOGGER.error("Error saving script content", e);
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandbox";
        } catch (Exception e) {
            LOGGER.error("Error saving script content", e);
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandbox";
        }

        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/qaSandbox";
    }

    @GetMapping("/edit/{scriptId}")
    public String edit(@PathVariable String scriptId, Model model, HttpServletRequest request) {

        SpringMessages errors = new SpringMessages();

        // reset the form in the session
        QASandboxForm cForm = new QASandboxForm();

        /*String scriptIdParam = null;
        if (httpServletRequest.getParameter("scriptId") != null) {
            scriptIdParam = httpServletRequest.getParameter("scriptId");
        }
        boolean reset = false;
        // request comes from Schema Queries page
        if (httpServletRequest.getParameter("reset") != null) {
            reset = "true".equals(httpServletRequest.getParameter("reset"));
        }*/
        /*if (Utils.isNullStr(scriptId)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.qasandbox.missingId"));
            saveErrors(httpServletRequest, errors);
            return actionMapping.findForward("error");
        }*/

        try {
            request.setAttribute(QAScriptListLoader.QASCRIPT_LIST_ATTR, qaScriptListLoader.getList(request));
            // reset field values
            /*if (reset) {
                cForm.setSourceUrl("");
                if (cForm.getSchema() != null) {
                    Schema schema = cForm.getSchema();
                    schema.setCrfiles(null);
                    cForm.setSchema(schema);
                }
            }*/
            cForm.setShowScripts(false);

            // write a new script
            if ("0".equals(scriptId)) {
                cForm.setScriptId(scriptId);
                cForm.setScriptContent("");
                cForm.setScriptType(XQScript.SCRIPT_LANG_XQUERY1);
                return "/qaSandboxOld/view";
            }
            QAScriptDto script = qaScriptManager.getQAScript(scriptId);

            cForm.setScriptId(scriptId);
            cForm.setScriptContent(script.getScriptContent());
            cForm.setScriptType(script.getScriptType());

            cForm.setSchemaId(script.getSchemaId());
            cForm.setSchemaUrl(script.getSchema());
            Schema schema = cForm.getSchema();
            if (schema == null) {
                schema = new Schema();
                schema.setSchema(script.getSchema());
                schema.setId(script.getSchemaId());
                cForm.setSchema(schema);
            }
        } catch (DCMException e) {
            LOGGER.error("QA Sandbox form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }

        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/qaSandboxOld/view";
    }

    @PostMapping("/extract")
    public String extract(@ModelAttribute QASandboxForm cForm, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        SpringMessages errors = new SpringMessages();
        Schema oSchema = cForm.getSchema();
        String sourceUrl = cForm.getSourceUrl();

        if (Utils.isNullStr(sourceUrl)) {
            errors.add(messageService.getMessage("error.qasandbox.missingUrl"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandboxOld";
        }
        if (!Utils.isURL(sourceUrl)) {
            errors.add(messageService.getMessage("error.qasandbox.notUrl"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandboxOld";
        }

        String schemaUrl = null;
        try {
            if (!Utils.isNullStr(sourceUrl)) {
                schemaUrl = findSchemaFromXml(sourceUrl);
                if (!Utils.isURL(schemaUrl)) {
                    errors.add(messageService.getMessage("error.qasandbox.schemaNotFound"));
                    redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                    return "redirect:/qaSandboxOld";
                }
                if (schemaExists(request, schemaUrl)) {
                    cForm.setSchemaUrl(schemaUrl);
                } else if (!Utils.isNullStr(schemaUrl)) {
                    if (oSchema == null) {
                        oSchema = new Schema();
                    }

                    oSchema.setSchema(null);
                    oSchema.setDoValidation(false);
                    oSchema.setQascripts(null);
                    cForm.setSchemaUrl(null);
                    cForm.setShowScripts(true);
                    cForm.setSchema(oSchema);

                    errors.add(messageService.getMessage("error.qasandbox.noSchemaScripts", schemaUrl));
                    redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                    return "redirect:/qaSandboxOld";
                }
            }
        } catch (DCMException e) {
            LOGGER.error("Error extracting schema from XML file", e);
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandboxOld";
        }

        return "redirect:/qaSandboxOld/findScripts";

    }

    /**
     * check if schema passed as request parameter exists in the list of schemas stored in the session. If there is no schema list
     * in the session, then create it
     *
     * @param httpServletRequest Request
     * @param schema Schema
     * @return True if schema exists.
     * @throws DCMException If an error occurs.
     */
    private boolean schemaExists(HttpServletRequest httpServletRequest, String schema) throws DCMException {
        QAScriptListHolder schemasInSession = qaScriptListLoader.getList(httpServletRequest);
        Schema oSchema = new Schema();
        oSchema.setSchema(schema);
        return schemasInSession.getQascripts().contains(oSchema);
    }

    /**
     * Finds schema from XML
     * @param xml XML
     * @return Result
     */
    private String findSchemaFromXml(String xml) {
        InputAnalyser analyser = new InputAnalyser();
        try {
            analyser.parseXML(xml);
            String schemaOrDTD = analyser.getSchemaOrDTD();
            return schemaOrDTD;
        } catch (Exception e) {
            // do nothing - did not find XML Schema
            // handleError(request, response, e);
        }
        return null;
    }

    @PostMapping("/openQAService")
    public String openQAService(@ModelAttribute QASandboxForm cForm, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        SpringMessages errors = new SpringMessages();
        String schemaIdParam = null;
        if (request.getParameter("schemaId") != null) {
            schemaIdParam = request.getParameter("schemaId");
        }
        if (Utils.isNullStr(schemaIdParam)) {

            errors.add(messageService.getMessage("error.qasandbox.missingSchemaId"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandboxOld";
        }

        try {
            request.setAttribute(QAScriptListLoader.QASCRIPT_LIST_ATTR, qaScriptListLoader.getList(request));
            cForm.setShowScripts(true);
            cForm.setSourceUrl("");

            QAScriptListHolder qascripts = schemaManager.getSchemasWithQAScripts(schemaIdParam);
            Schema schema = null;

            if (qascripts == null || qascripts.getQascripts() == null || qascripts.getQascripts().size() == 0) {
                schema = new Schema();
            } else {
                schema = qascripts.getQascripts().get(0);
                cForm.setSchemaId(schema.getId());
                cForm.setSchemaUrl(schema.getSchema());
            }
            cForm.setSchema(schema);
            if (Utils.isNullList(cForm.getSchema().getQascripts()) && cForm.getSchema().isDoValidation()) {
                cForm.setScriptId("-1");
            } else if (!Utils.isNullList(cForm.getSchema().getQascripts()) && cForm.getSchema().getQascripts().size() == 1
                    && !cForm.getSchema().isDoValidation()) {
                cForm.setScriptId(cForm.getSchema().getQascripts().get(0).getScriptId());
            }
        } catch (DCMException e) {
            LOGGER.error("QA Sandbox form error error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }

        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "redirect:/qaSandboxOld";
    }

    @PostMapping("/runScript")
    public String runQAScript(@ModelAttribute QASandboxForm cForm, RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request, HttpServletResponse response) {

        SpringMessages errors = new SpringMessages();
        cForm.setResult(null);
        String scriptId = cForm.getScriptId();
        String scriptContent = cForm.getScriptContent();
        String scriptType = cForm.getScriptType();
        String sourceUrl = cForm.getSourceUrl();
        boolean showScripts = cForm.isShowScripts();
        String userName = (String) session.getAttribute("user");

        try {
            if (showScripts && Utils.isNullStr(scriptId)) {
                errors.add(messageService.getMessage("error.qasandbox.missingId"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/qaSandboxOld";
            }
            if (!showScripts && Utils.isNullStr(scriptContent)) {
                errors.add(messageService.getMessage("error.qasandbox.missingContent"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/qaSandboxOld";
            }
            if (!Utils.isNullStr(scriptContent) && !SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QASANDBOX_PATH, "i")) {
                errors.add(messageService.getMessage("label.autorization.qasandbox.execute"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/qaSandboxOld";
            }
            if (Utils.isNullStr(sourceUrl)) {
                errors.add(messageService.getMessage("error.qasandbox.missingUrl"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/qaSandboxOld";
            }
            if (!Utils.isURL(sourceUrl)) {
                errors.add(messageService.getMessage("error.qasandbox.notUrl"));
                redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/qaSandboxOld";
            }
            String result = null;

            // VALIDATION! if it is a validation job, then do the action and get
            // out of here
            if (String.valueOf(Constants.JOB_VALIDATION).equals(scriptId)) {
                try {
                    CompletableFuture<ValidationResult> future;
                    //vs.setTrustedMode(false);
                    // result = vs.validateSchema(dataURL, xml_schema);
                    future = qaRestService.executeValidation(sourceUrl);
                    ValidationResult valid = future.get();
                    // TODO FIX ASAP
                } catch (DCMException de) {
                    result = de.getMessage();
                }
                cForm.setResult(result);
                return "redirect:/qaSandboxOld";
            }

            QAScriptDto qascript = null;
            String outputContentType = "text/html";
            String xqResultType = null;

            // get QA script
            if (!Utils.isNullStr(scriptId) && !"0".equals(scriptId)) {
                qascript = qaScriptManager.getQAScript(scriptId);
                String resultType = qascript.getResultType();
                if (qascript != null && qascript.getScriptType() != null) {
                    scriptType = qascript.getScriptType();
                }
                // get correct output type by convTypeId
                ConvType cType = convTypeManager.getConvType(resultType);
                if (cType != null && !Utils.isNullStr(cType.getContType())) {
                    outputContentType = cType.getContType();
                    xqResultType = cType.getConvType();
                }

            }
            XQScript xq = null;
            if (showScripts && qascript != null && qascript.getScriptContent() != null) {
                // run script by ID
                // read scriptContent from file
                try {
                    scriptContent = Utils.readStrFromFile(Properties.queriesFolder + File.separator + qascript.getFileName());
                } catch (Exception e) {
                    errors.add(messageService.getMessage("error.qasandbox.fileNotFound"));
                    redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                    return "redirect:/qaSandboxOld";
                }
            }

            if (qascript != null && qascript.getScriptType() != null) {
                if (XQScript.SCRIPT_LANG_FME.equals(qascript.getScriptType())) {
                    scriptType = XQScript.SCRIPT_LANG_FME;
                    scriptContent = qascript.getUrl();
                }
            }

            String[] pars = new String[1];
            pars[0] = Constants.XQ_SOURCE_PARAM_NAME + "=" + sourceUrl;
            if (xqResultType == null) {
                xqResultType = XQScript.SCRIPT_RESULTTYPE_HTML;
            }
            xq = new XQScript(scriptContent, pars, xqResultType);
            xq.setScriptType(scriptType);
            xq.setSrcFileUrl(sourceUrl);

            if (qascript != null && qascript.getSchemaId() != null) {
                xq.setSchema(schemaManager.getSchema(qascript.getSchemaId()));
            }

            OutputStream output = null;
            try {
                // XXX: REPLACE ASAP
                // write the result directly to servlet outputstream
                if (!outputContentType.startsWith("text/html")) {
                    response.setContentType(outputContentType);
                    response.setCharacterEncoding("utf-8");
                    output = response.getOutputStream();
                    qaService.execute(xq, output);
                    // TODO: remove flush and close
                    output.flush();
                    output.close();
                    return null;
                } else {
                    result = qaService.execute(xq);
                    cForm.setResult(result);
                }
            } catch (XMLConvException ge) {
                result = ge.getMessage();
                if (output == null) {
                    cForm.setResult(result);
                    return "redirect:/qaSandboxOld";
                } else {
                    output.write(result.getBytes(StandardCharsets.UTF_8));
                    // TODO: remove flush and close
                    output.flush();
                    output.close();
                    return null;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error executing QA script", e);
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandboxOld";
        }
        return "redirect:/qaSandboxOld";
    }

    @PostMapping("/save/{scriptId}")
    public String save(@ModelAttribute QASandboxForm cForm, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();
        String scriptId = cForm.getScriptId();
        String content = cForm.getScriptContent();

        if (Utils.isNullStr(scriptId)) {
            errors.add(messageService.getMessage("error.qasandbox.missingId"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandboxOld";
        }
        if (Utils.isNullStr(content)) {
            errors.add(messageService.getMessage("error.qasandbox.missingContent"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandboxOld";
        }
        try {
            String userName = (String) session.getAttribute("user");
            qaScriptManager.storeQAScriptFromString(userName, scriptId, content);
            messages.add(messageService.getMessage("message.qasandbox.contentSaved"));
        } catch (DCMException | IOException e) {
            LOGGER.error("Error saving script content", e);
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandboxOld";
        }

        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "redirect:/qaSandboxOld";
    }

    @PostMapping("/searchCR")
    public String searchCR(@ModelAttribute QASandboxForm cForm, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();
        String schemaUrl = null;

        schemaUrl = cForm.getSchemaUrl();
        Schema oSchema = cForm.getSchema();

        try {
            // use the Schema data from the session, if schema is the same
            // otherwise load the data from database and search CR
            if (!Utils.isNullStr(schemaUrl)
                    && (oSchema == null || oSchema.getSchema() == null || !oSchema.getSchema().equals(schemaUrl) || oSchema
                    .getCrfiles() == null)) {
                if (!schemaExists(request, schemaUrl)) {
                    throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
                }
                List<CrFileDto> crfiles = null;
                crfiles = schemaManager.getCRFiles(schemaUrl);
                if (oSchema == null) {
                    oSchema = new Schema();
                }
                oSchema.setSchema(schemaUrl);
                oSchema.setCrfiles(crfiles);

                cForm.setSchema(oSchema);

                if (cForm.isShowScripts()) {
                    return "redirect:/qaSandboxOld/find";
                }
            }
        } catch (DCMException e) {
            LOGGER.error("Error searching XML files", e);
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/qaSandboxOld";
        }

        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "redirect:/qaSandboxOld";
    }
}
