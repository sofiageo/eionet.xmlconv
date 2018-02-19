package eionet.gdem.web.spring.schemas;

import eionet.gdem.Properties;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.generic.SingleForm;
import eionet.gdem.web.spring.scripts.QAScriptListLoader;
import eionet.gdem.web.spring.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    private SchemasService schemasService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemasController.class);

    @Autowired
    public SchemasController(MessageService messageService, SchemasService schemasService) {
        this.messageService = messageService;
        this.schemasService = schemasService;
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


    @GetMapping("/add")
    public String add(@ModelAttribute("form") UploadSchemaForm form, Model model) {
        model.addAttribute("form", form);
        return "/schemas/add";
    }

    @PostMapping("/add")
    public String addSubmit(@ModelAttribute("form") UploadSchemaForm form, HttpServletRequest httpServletRequest,
                            BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        SpringMessages messages = new SpringMessages();

        MultipartFile schemaFile = form.getSchemaFile();
        String desc = form.getDescription();
        String schemaUrl = form.getSchemaUrl();
        boolean doValidation = form.isDoValidation();
        String schemaLang = form.getSchemaLang();
        boolean blocker = form.isBlockerValidation();

        new UploadSchemaFormValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/schemas/add";
        }

        String user = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            String fileName = "";
            String tmpSchemaUrl = "";
            // generate unique file name
            if (schemaFile != null && schemaFile.getSize() > 0) {
                fileName = sm.generateUniqueSchemaFilename(user, Utils.extractExtension(schemaFile.getOriginalFilename(), "xsd"));
                if (Utils.isNullStr(schemaUrl)) {
                    tmpSchemaUrl = Properties.gdemURL + "/schema/" + fileName;
                    schemaUrl = tmpSchemaUrl;
                }
            }
            // Add row to T_SCHEMA table
            String schemaID = sm.addSchema(user, schemaUrl, desc, schemaLang, doValidation, blocker);
            if (schemaFile != null && schemaFile.getSize() > 0) {
                // Change the filename to schema-UniqueIDxsd
                fileName = sm.generateSchemaFilenameByID(Properties.schemaFolder, schemaID, Utils.extractExtension(schemaFile.getOriginalFilename()));
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
            throw new RuntimeException("Error adding upload schema: " + e.getErrorCode());
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas";
    }

    @GetMapping("/{schemaId}")
    public String view(@PathVariable String schemaId, @ModelAttribute("form") SchemaForm schemaForm,
                       Model model, HttpServletRequest request, HttpSession session) {
        SpringMessages errors = new SpringMessages();

        SchemaForm form = new SchemaForm();
        String user = (String) session.getAttribute("user");

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
            model.addAttribute("form", form);
            model.addAttribute("schemaId", schemaId);
        } catch (DCMException e) {
            throw new RuntimeException("Schema element form error: " + e.getErrorCode());
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/view";
    }

    @GetMapping("/{schemaId}/edit")
    public String edit(@PathVariable String schemaId, @ModelAttribute("form") SchemaForm form,
                       Model model, HttpServletRequest request) {
        SpringMessages errors = new SpringMessages();
        String user = (String) request.getSession().getAttribute("user");

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
            model.addAttribute("schemaId", schemaId);
            model.addAttribute("rootElements", seHolder);
            model.addAttribute("form", form);
        } catch (DCMException e) {
            throw new RuntimeException("Schema element form error: " + e.getErrorCode());
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/schemas/edit";
    }

    @PostMapping(params = {"update"})
    public String editSubmit(@ModelAttribute SchemaForm form, HttpServletRequest httpServletRequest,
                             BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {
        SpringMessages messages = new SpringMessages();

        String schemaId = form.getSchemaId();
        String schema = form.getSchema();
        String description = form.getDescription();
        String dtdId = form.getDtdId();
        String schemaLang = form.getSchemaLang();
        boolean doValidation = form.isDoValidation();
        Date expireDate = form.getExpireDateObj();
        boolean blocker = form.isBlocker();

        new SchemaFormValidator().validate(form, bindingResult);
        if (bindingResult.hasErrors()) {
            return "/schemas/edit";
        }

        String user = (String) session.getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            String schemaIdByUrl = sm.getSchemaId(schema);

            if (schemaIdByUrl != null && !schemaIdByUrl.equals(schemaId)) {
                String schemaTargetUrl = String.format("viewSchemaForm?schemaId=%s", schemaIdByUrl);
                bindingResult.reject(messageService.getMessage("label.schema.url.exists", schemaTargetUrl));
                return "/schemas/edit";
            }

            sm.update(user, schemaId, schema, description, schemaLang, doValidation, dtdId, expireDate, blocker);
            messages.add(messageService.getMessage("label.schema.updated"));

            QAScriptListLoader.reloadList(httpServletRequest);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            throw new RuntimeException("Error editing schema" + e.getErrorCode());
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas/" + schemaId + "/edit";
    }

    @PostMapping(params = {"delete"})
    public String delete(@ModelAttribute SingleForm form, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
        SpringMessages messages = new SpringMessages();

        String schemaId = form.getId();
        String user_name = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            int schemaDeleted = sm.deleteUplSchema(user_name, schemaId, true);
            if (schemaDeleted == 2) {
                messages.add(messageService.getMessage("label.uplSchema.deleted"));
            }

            if (schemaDeleted == 1 || schemaDeleted == 3) {
                messages.add(messageService.getMessage("label.schema.deleted"));
            }

            if (schemaDeleted == 0 || schemaDeleted == 2) {
                messages.add(messageService.getMessage("label.uplSchema.notdeleted"));
            }

            // clear qascript list in cache
            QAScriptListLoader.reloadList(httpServletRequest);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            throw new RuntimeException("Error deleting root schema: " + e.getErrorCode());
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        return "redirect:/schemas";
    }
}