package eionet.gdem.web.spring.schemas;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.FileUploadWrapper;
import eionet.gdem.web.spring.SpringMessage;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.schemas.SchemaElemHolder;
import eionet.gdem.web.spring.schemas.UplSchemaHolder;
import eionet.gdem.web.spring.scripts.QAScriptListLoader;
import eionet.gdem.web.spring.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 *
 *
 */
@Controller
@RequestMapping("/schemas")
public class SchemasController {

    private MessageService messageService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemasController.class);

    @Autowired
    public SchemasController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {
        UplSchemaHolder holder = null;
        SpringMessages errors = new SpringMessages();

        String user = (String) session.getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            holder = sm.getAllSchemas(user);
            SingleForm cForm = new SingleForm();
            model.addAttribute("form", cForm);
            model.addAttribute("schemas", holder);
        } catch (DCMException e) {
            LOGGER.error("Upload schema form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/list";
    }

    @GetMapping("/{schemaId}")
    public String view(@PathVariable String schemaId, Model model, HttpServletRequest request, HttpSession session) {
        SpringMessages errors = new SpringMessages();

        SchemaForm form = new SchemaForm();
        /*String schemaId = httpServletRequest.getParameter("schemaId");*/
        String user = (String) session.getAttribute("user");

        /*if (schemaId == null || schemaId.trim().isEmpty()) {
            schemaId = httpServletRequest.getParameter("schema");
        }*/

        try {
            SchemaManager sm = new SchemaManager();
            SchemaElemHolder seHolder = sm.getSchemaElems(user, schemaId);
            if (seHolder == null || seHolder.getSchema() == null) {
                throw new DCMException(BusinessConstants.EXCEPTION_SCHEMA_NOT_EXIST);
            }
            schemaId = seHolder.getSchema().getId();
            form.setSchema(seHolder.getSchema().getSchema());
            form.setDescription(seHolder.getSchema().getDescription());
            form.setSchemaId(schemaId);
            form.setDtdId(seHolder.getSchema().getDtdPublicId());
            form.setElemName("");
            form.setNamespace("");
            form.setDoValidation(seHolder.getSchema().isDoValidation());
            form.setBlocker(seHolder.getSchema().isBlocker());
            form.setSchemaLang(seHolder.getSchema().getSchemaLang());
            form.setDtd(seHolder.getSchema().getIsDTD());
            String fileName = seHolder.getSchema().getUplSchemaFileName();
            form.setExpireDateObj(seHolder.getSchema().getExpireDate());
            if (seHolder.getSchema().getUplSchema() != null && !Utils.isNullStr(fileName)) {
                form.setUplSchemaId(seHolder.getSchema().getUplSchema().getUplSchemaId());
                form.setUplSchemaFileUrl(seHolder.getSchema().getUplSchema().getUplSchemaFileUrl());
                form.setLastModified(seHolder.getSchema().getUplSchema().getLastModified());
                form.setUplSchemaFileName(fileName);
                form.setUplSchemaFileUrl(Properties.gdemURL + "/schema/" + fileName);
            }
            seHolder.setSchemaIdRemoteUrl(Utils.isURL(seHolder.getSchema().getSchema())
                    && !seHolder.getSchema().getSchema().startsWith(SecurityUtil.getUrlWithContextPath(request)));
            model.addAttribute("rootElements", seHolder);
            model.addAttribute("schemaForm", form);
        } catch (DCMException e) {
            LOGGER.error("Schema element form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "/schemas/list";
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/view";
    }

    @GetMapping("/{schemaId}/edit")
    public String edit(@PathVariable String schemaId, Model model, HttpServletRequest request, HttpSession session) {
        SpringMessages errors = new SpringMessages();

        SchemaForm form = new SchemaForm();
        /*String schemaId = httpServletRequest.getParameter("schemaId");*/
        String user = (String) session.getAttribute("user");

        /*if (schemaId == null || schemaId.trim().isEmpty()) {
            schemaId = httpServletRequest.getParameter("schema");
        }*/

        try {
            SchemaManager sm = new SchemaManager();
            SchemaElemHolder seHolder = sm.getSchemaElems(user, schemaId);
            if (seHolder == null || seHolder.getSchema() == null) {
                throw new DCMException(BusinessConstants.EXCEPTION_SCHEMA_NOT_EXIST);
            }
            schemaId = seHolder.getSchema().getId();
            form.setSchema(seHolder.getSchema().getSchema());
            form.setDescription(seHolder.getSchema().getDescription());
            form.setSchemaId(schemaId);
            form.setDtdId(seHolder.getSchema().getDtdPublicId());
            form.setElemName("");
            form.setNamespace("");
            form.setDoValidation(seHolder.getSchema().isDoValidation());
            form.setBlocker(seHolder.getSchema().isBlocker());
            form.setSchemaLang(seHolder.getSchema().getSchemaLang());
            form.setDtd(seHolder.getSchema().getIsDTD());
            String fileName = seHolder.getSchema().getUplSchemaFileName();
            form.setExpireDateObj(seHolder.getSchema().getExpireDate());
            if (seHolder.getSchema().getUplSchema() != null && !Utils.isNullStr(fileName)) {
                form.setUplSchemaId(seHolder.getSchema().getUplSchema().getUplSchemaId());
                form.setUplSchemaFileUrl(seHolder.getSchema().getUplSchema().getUplSchemaFileUrl());
                form.setLastModified(seHolder.getSchema().getUplSchema().getLastModified());
                form.setUplSchemaFileName(fileName);
                form.setUplSchemaFileUrl(Properties.gdemURL + "/schema/" + fileName);
            }
            seHolder.setSchemaIdRemoteUrl(Utils.isURL(seHolder.getSchema().getSchema())
                    && !seHolder.getSchema().getSchema().startsWith(SecurityUtil.getUrlWithContextPath(request)));
            model.addAttribute("rootElements", seHolder);
            model.addAttribute("schemaForm", form);
        } catch (DCMException e) {
            LOGGER.error("Schema element form error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "/schemas/list";
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/edit";
    }

    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable String id, @ModelAttribute SchemaForm form, HttpServletRequest httpServletRequest, HttpSession session, RedirectAttributes redirectAttributes) {
        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String schemaId = form.getSchemaId();
        String schema = form.getSchema();
        String description = form.getDescription();
        String dtdId = form.getDtdId();
        String schemaLang = form.getSchemaLang();
        boolean doValidation = form.isDoValidation();
        Date expireDate = form.getExpireDateObj();
        boolean blocker = form.isBlocker();

/*        if (isCancelled(httpServletRequest)) {
            try {

                httpServletRequest.setAttribute("schema", sch.getSchema());
                return actionMapping.findForward("back");
            } catch (DCMException e) {
                e.printStackTrace();
                LOGGER.error("Error editing schema", e);
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
            }
        }*/
        /*errors = form.validate(actionMapping, httpServletRequest);*/
        if (errors.size() > 0) {
            redirectAttributes.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/old/schemas/list";
        }
        if (schema == null || schema.equals("")) {
            errors.add(messageService.getMessage("label.schema.validation"));
            redirectAttributes.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/old/schemas/list";
        }

        if (!(new SchemaUrlValidator().isValidUrlSet(schema))) {
            errors.add(messageService.getMessage("label.uplSchema.validation.urlFormat"));
            redirectAttributes.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/old/schemas/list";
        }

        String user = (String) session.getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            String schemaIdByUrl = sm.getSchemaId(schema);

            if (schemaIdByUrl != null && !schemaIdByUrl.equals(schemaId)) {
                String schemaTargetUrl = String.format("viewSchemaForm?schemaId=%s", schemaIdByUrl);
                errors.add(messageService.getMessage("label.schema.url.exists", schemaTargetUrl));
                redirectAttributes.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/old/schemas/list";
            }

            sm.update(user, schemaId, schema, description, schemaLang, doValidation, dtdId, expireDate, blocker);

            messages.add(messageService.getMessage("label.schema.updated"));
            redirectAttributes.addAttribute("schema", schema);
            // clear qascript list in cache
            QAScriptListLoader.reloadList(httpServletRequest);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            LOGGER.error("Error editing schema", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);

        return "redirect:/old/schemas/edit";
    }

    @GetMapping("/add")
    public String add(Model model) {
        UploadSchemaForm form = new UploadSchemaForm();
        model.addAttribute("schemaForm", form);
        return "/schemas/add";
    }

    @PostMapping("/add")
    public String addSubmit(@ModelAttribute UploadSchemaForm form, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        FileUploadWrapper schemaFile = form.getSchemaFile();
        String desc = form.getDescription();
        String schemaUrl = form.getSchemaUrl();
        boolean doValidation = form.isDoValidation();
        String schemaLang = form.getSchemaLang();
        boolean blocker = form.isBlockerValidation();

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        if ((schemaFile == null || schemaFile.getFile().getSize() == 0) && Utils.isNullStr(schemaUrl)) {
            errors.add(messageService.getMessage("label.uplSchema.validation"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/old/schemas/add";
        }

        if (!(new SchemaUrlValidator().isValidUrlSet(schemaUrl))) {
            errors.add(messageService.getMessage("label.uplSchema.validation.urlFormat"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/old/schemas/add";
        }

        try {
            SchemaManager sm = new SchemaManager();
            String fileName = "";
            String tmpSchemaUrl = "";
            // generate unique file name
            if (schemaFile != null) {
                fileName = sm.generateUniqueSchemaFilename(user, Utils.extractExtension(schemaFile.getFile().getName(), "xsd"));
                if (Utils.isNullStr(schemaUrl)) {
                    tmpSchemaUrl = Properties.gdemURL + "/schema/" + fileName;
                    schemaUrl = tmpSchemaUrl;
                }
            }
            // Add row to T_SCHEMA table
            String schemaID = sm.addSchema(user, schemaUrl, desc, schemaLang, doValidation, blocker);
            if (schemaFile != null && schemaFile.getFile().getSize() > 0) {
                // Change the filename to schema-UniqueIDxsd
                fileName =
                        sm.generateSchemaFilenameByID(Properties.schemaFolder, schemaID, Utils.extractExtension(schemaFile.getFile().getName()));
                // Add row to T_UPL_SCHEMA table
                sm.addUplSchema(user, schemaFile, fileName, schemaID);
                // Update T_SCHEMA table set
                if (!Utils.isNullStr(tmpSchemaUrl)) {
                    schemaUrl = Properties.gdemURL + "/schema/" + fileName;
                }
                sm.update(user, schemaID, schemaUrl, desc, schemaLang, doValidation, null, null, blocker);
            }
            messages.add(messageService.getMessage("label.uplSchema.inserted"));
            QAScriptListLoader.reloadList(httpServletRequest);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            LOGGER.error("Error adding upload schema", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/old/schemas";
    }

    @PostMapping("/delete")
    public String delete(@ModelAttribute SingleForm form, RedirectAttributes redirectAttributes, HttpSession session) {
        SpringMessages errors = new SpringMessages();

        SchemaManager sm = new SchemaManager();
        String user = (String) session.getAttribute("user");
        String id = Integer.toString(form.getId());
        try {
            sm.deleteUplSchema(user, id, true);
        } catch (DCMException e) {
            LOGGER.error("Could not remove selected schemas", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "redirect:/old/schemas";
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "redirect:/old/schemas";
    }


}