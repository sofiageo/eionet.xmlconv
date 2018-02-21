package eionet.gdem.web.spring.qasandbox;

import eionet.acl.SignOnException;
import eionet.gdem.Constants;
import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.web.spring.conversions.ConvTypeManager;
import eionet.gdem.qa.QAScriptManager;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.workqueue.WorkqueueManager;
import eionet.gdem.web.spring.conversions.ConvType;
import eionet.gdem.dto.CrFileDto;
import eionet.gdem.dto.QAScript;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.XQScript;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.InputAnalyser;
import eionet.gdem.validation.JaxpValidationService;
import eionet.gdem.validation.ValidationService;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.scripts.QAScriptListHolder;
import eionet.gdem.web.spring.scripts.QAScriptListLoader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.management.RuntimeErrorException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 */
@Controller
@RequestMapping("/qaSandbox")
public class QASandboxController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QASandboxController.class);
    private MessageService messageService;

    @Autowired
    public QASandboxController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String index(@ModelAttribute("form") QASandboxForm cForm, Model model, @RequestParam(required = false) String schemaId, HttpServletRequest request) {
        SpringMessages errors = new SpringMessages();

        cForm.setAction("");

        try {
            if (!Utils.isNullStr(schemaId)) {
                cForm.setShowScripts(true);
                cForm.setSourceUrl("");

                SchemaManager sm = new SchemaManager();
                QAScriptListHolder qascripts = sm.getSchemasWithQAScripts(schemaId);
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
            }
            model.addAttribute("scripts", QAScriptListLoader.getList(request));
        } catch (DCMException e) {
            LOGGER.error("QA Sandbox form error", e);
            throw new RuntimeException(messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute("form", cForm);
        return "/qaSandbox/view";
    }

    @GetMapping("/openQAService")
    public String openQAService(@ModelAttribute("form") QASandboxForm cForm, HttpServletRequest httpServletRequest) {

        SpringMessages errors = new SpringMessages();

        String schemaId = cForm.getSchemaId();

        try {
            httpServletRequest.setAttribute(QAScriptListLoader.QASCRIPT_LIST_ATTR, QAScriptListLoader.getList(httpServletRequest));
            cForm.setShowScripts(true);
            cForm.setSourceUrl("");

            SchemaManager sm = new SchemaManager();
            QAScriptListHolder qascripts = sm.getSchemasWithQAScripts(schemaId);
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
            throw new RuntimeException("QA Sandbox form error error");
        }
        return "/qaSandbox/view";
    }

    @PostMapping(params = {"findScripts"})
    public String find(@ModelAttribute("form") QASandboxForm cForm, Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();

        String schemaUrl = cForm.getSchemaUrl();
        Schema schema = cForm.getSchema();

        new QASandboxValidator().validate(cForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/qaSandbox/view";
        }
        try {
            // cForm.setScriptId(null);

            SchemaManager sm = new SchemaManager();
            String schemaId = sm.getSchemaId(schemaUrl);
            QAScriptListHolder qaScripts = sm.getSchemasWithQAScripts(schemaId);

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
            throw new RuntimeException("Error searching XML files: " + e.getErrorCode());
        }

        return "/qaSandbox/view";
    }

    @PostMapping(params = {"addToWorkqueue"})
    public String addToWorkQueue(@ModelAttribute("form") QASandboxForm cForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        String sourceUrl = cForm.getSourceUrl();
        String content = cForm.getScriptContent();
        String scriptType = cForm.getScriptType();
        String schemaUrl = cForm.getSchemaUrl();

        String userName = (String) session.getAttribute("user");

        new QASandboxValidator().validate(cForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/qaSandbox/view";
        }

        try {
            WorkqueueManager workqueueManager = new WorkqueueManager();
            if (cForm.isShowScripts()) {
                List<String> jobIds = workqueueManager.addSchemaScriptsToWorkqueue(userName, sourceUrl, schemaUrl);
                messages.add(messageService.getMessage("message.qasandbox.jobsAdded", jobIds.toString()));
            } else {
                String jobId = workqueueManager.addQAScriptToWorkqueue(userName, sourceUrl, content, scriptType);
                messages.add(messageService.getMessage("message.qasandbox.jobAdded", jobId));
            }
        } catch (DCMException e) {
            throw new RuntimeException("Error saving script content");
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "/qaSandbox/view";
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

        try {
            model.addAttribute("scripts", QAScriptListLoader.getList(request));
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
                return "/qaSandbox/view";
            }
            QAScriptManager qm = new QAScriptManager();
            QAScript script = qm.getQAScript(scriptId);

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
            throw new RuntimeException(messageService.getMessage(e.getErrorCode()));
        }

        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/qaSandbox/view";
    }

    @PostMapping(params = {"extractSchema"})
    public String extract(@ModelAttribute("form") QASandboxForm cForm, Model model,
                          BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        SpringMessages errors = new SpringMessages();
        Schema oSchema = cForm.getSchema();
        String sourceUrl = cForm.getSourceUrl();
        String schemaUrl = null;
        cForm.setAction("extractSchema");
        new QASandboxValidator().validate(cForm, bindingResult);

        try {
            if (!Utils.isNullStr(sourceUrl)) {
                schemaUrl = findSchemaFromXml(sourceUrl);
                if (!Utils.isURL(schemaUrl)) {
                    bindingResult.reject("error.qasandbox.schemaNotFound");
                    return "/qaSandbox/view";
                }
                if (schemaExists(request, schemaUrl)) {
                    cForm.setSchemaUrl(schemaUrl);
                } else if (!Utils.isNullStr(schemaUrl)) {
                    bindingResult.reject(messageService.getMessage("error.qasandbox.noSchemaScripts", schemaUrl));
                    return "/qaSandbox/view";

                   /* if (oSchema == null) {
                        oSchema = new Schema();
                    }

                    oSchema.setSchema(null);
                    oSchema.setDoValidation(false);
                    oSchema.setQascripts(null);
                    cForm.setSchemaUrl(null);
                    cForm.setShowScripts(true);
                    cForm.setSchema(oSchema);

                    errors.add();
                    model.addAttribute("form", cForm);
                    return "/qaSandbox/view";*/
                }
            }
        } catch (DCMException e) {
            throw new RuntimeException("Error extracting schema from XML file");
        }
        model.addAttribute("form", cForm);
        return "forward:/qaSandbox";
    }

    @PostMapping(params = {"runScript"})
    public String runQAScript(@ModelAttribute("form") QASandboxForm cForm, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                              HttpSession session, HttpServletRequest request, HttpServletResponse response) {

        SpringMessages errors = new SpringMessages();
        cForm.setResult(null);
        String scriptId = cForm.getScriptId();
        String scriptContent = cForm.getScriptContent();
        String scriptType = cForm.getScriptType();
        String sourceUrl = cForm.getSourceUrl();
        boolean showScripts = cForm.isShowScripts();
        String userName = (String) session.getAttribute("user");

        try {
            if (!Utils.isNullStr(scriptContent) && !SecurityUtil.hasPerm(userName, "/" + Constants.ACL_QASANDBOX_PATH, "i")) {
                throw new AccessDeniedException(messageService.getMessage("label.autorization.qasandbox.execute"));
            }
            String result = null;

            // VALIDATION! if it is a validation job, then do the action and get
            // out of here
            if (String.valueOf(Constants.JOB_VALIDATION).equals(scriptId)) {
                try {
                    ValidationService vs = new JaxpValidationService();
                    //vs.setTrustedMode(false);
                    // result = vs.validateSchema(dataURL, xml_schema);
                    result = vs.validate(sourceUrl);
                } catch (DCMException de) {
                    result = de.getMessage();
                }
                cForm.setResult(result);
                return "/qaSandbox/view";
            }

            QAScript qascript = null;
            String outputContentType = "text/html";
            String xqResultType = null;
            QAScriptManager qm = new QAScriptManager();
            ConvTypeManager ctm = new ConvTypeManager();
            SchemaManager schM = new SchemaManager();

            // get QA script
            if (!Utils.isNullStr(scriptId) && !"0".equals(scriptId)) {
                qascript = qm.getQAScript(scriptId);
                String resultType = qascript.getResultType();
                if (qascript != null && qascript.getScriptType() != null) {
                    scriptType = qascript.getScriptType();
                }
                // get correct output type by convTypeId
                ConvType cType = ctm.getConvType(resultType);
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
                } catch (IOException e) {
                    bindingResult.reject("error.qasandbox.fileNotFound");
                    /*redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
                    return "redirect:/qaSandbox";*/
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
                xq.setSchema(schM.getSchema(qascript.getSchemaId()));
            }

            OutputStream output = null;
            try {
                /*// XXX: REPLACE ASAP
                // write the result directly to servlet outputstream
                if (!outputContentType.startsWith("text/html")) {*/
                response.setContentType(outputContentType);
                response.setCharacterEncoding("utf-8");
                output = response.getOutputStream();
                xq.getResult(output);

                /*} else {
                    result = xq.getResult();
                    cForm.setResult(result);
                }*/
            } catch (XMLConvException ge) {
                throw new RuntimeException("Exception:" + ge.getMessage());
            }
        } catch (IOException | DCMException | SignOnException e) {
            throw new RuntimeException("Exception:" + e.getMessage());
        }
        return null;
    }

    @PostMapping(value = "/scripts", params = {"save"})
    public String save(@ModelAttribute("form") QASandboxForm cForm, Model model,
                       RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();
        String scriptId = cForm.getScriptId();
        String content = cForm.getScriptContent();

        String userName = (String) session.getAttribute("user");

        try {
            QAScriptManager qm = new QAScriptManager();
            qm.storeQAScriptFromString(userName, scriptId, content);
            messages.add(messageService.getMessage("message.qasandbox.contentSaved"));
        } catch (DCMException | IOException e) {
            throw new RuntimeException("Error saving script content");
        }
        model.addAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "/qaSandbox/view";
//        return "redirect:/qaSandbox";
    }

    @PostMapping(params = {"searchCR"})
    public String searchCR(@ModelAttribute("form") @Valid QASandboxForm cForm, Model model,
                           HttpServletRequest request, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();
        String schemaUrl = null;

        schemaUrl = cForm.getSchemaUrl();
        Schema oSchema = cForm.getSchema();

        try {
            SchemaManager sm = new SchemaManager();
            // use the Schema data from the session, if schema is the same
            // otherwise load the data from database and search CR
            if (!Utils.isNullStr(schemaUrl)
                    && (oSchema == null || oSchema.getSchema() == null || !oSchema.getSchema().equals(schemaUrl) || oSchema
                    .getCrfiles() == null)) {
                if (!schemaExists(request, schemaUrl)) {
                    throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);
                }
                List<CrFileDto> crfiles = null;
                crfiles = sm.getCRFiles(schemaUrl);
                if (oSchema == null) {
                    oSchema = new Schema();
                }
                oSchema.setSchema(schemaUrl);
                oSchema.setCrfiles(crfiles);

                cForm.setSchema(oSchema);

                if (cForm.isShowScripts()) {
                    return "redirect:/qaSandbox/find";
                }
            }
        } catch (DCMException e) {
            throw new RuntimeException("Error searching XML files");
        }
        model.addAttribute("form", cForm);
        return "/qaSandbox/view";
    }

    /**
     * check if schema passed as request parameter exists in the list of schemas stored in the session. If there is no schema list
     * in the session, then create it
     *
     * @param httpServletRequest Request
     * @param schema             Schema
     * @return True if schema exists.
     * @throws DCMException If an error occurs.
     */
    private boolean schemaExists(HttpServletRequest httpServletRequest, String schema) throws DCMException {
        QAScriptListHolder schemasInSession = QAScriptListLoader.getList(httpServletRequest);
        Schema oSchema = new Schema();
        oSchema.setSchema(schema);
        return schemasInSession.getQascripts().contains(oSchema);
    }

    /**
     * Finds schema from XML
     *
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

}
