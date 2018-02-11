package eionet.gdem.web.spring.conversions;

import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.dto.Stylesheet;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.schemas.IRootElemDao;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.schemas.SchemaForm;
import eionet.gdem.web.spring.stylesheet.ConvTypeHolder;
import eionet.gdem.web.spring.stylesheet.ConversionsUtils;
import eionet.gdem.web.spring.stylesheet.StylesheetForm;
import eionet.gdem.web.spring.stylesheet.StylesheetListHolder;
import eionet.gdem.web.spring.stylesheet.StylesheetListLoader;
import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 */
@Controller("webConversions")
@RequestMapping("/conversions")
public class ConversionsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionsController.class);
    private MessageService messageService;
    private IRootElemDao rootElemDao;

    @Autowired
    public ConversionsController(MessageService messageService, IRootElemDao rootElemDao) {
        this.messageService = messageService;
        this.rootElemDao = rootElemDao;
    }

    @GetMapping
    public String list(Model model, HttpServletRequest httpServletRequest) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        try {
            model.addAttribute("conversions", StylesheetListLoader.getStylesheetList(httpServletRequest));
        } catch (DCMException e) {
            LOGGER.error("Error getting stylesheet list", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
            model.addAttribute("errors", errors);
        }
        return "/conversions/list";
    }

    @GetMapping("/{conversionId}")
    public String view(@PathVariable String conversionId, Model model) {

        SpringMessages errors = new SpringMessages();
        StylesheetForm form = new StylesheetForm();

        ConvTypeHolder types = new ConvTypeHolder();
        StylesheetManager stylesheetManager = new StylesheetManager();

        try {
            Stylesheet stylesheet = stylesheetManager.getStylesheet(conversionId);

            if (stylesheet == null) {
                errors.add(HttpServletResponse.SC_NOT_FOUND);
                return "redirect:/conversions";
            }

            form.setDescription(stylesheet.getDescription());
            form.setOutputtype(stylesheet.getType());
            form.setStylesheetId(stylesheet.getConvId());
            form.setXsl(stylesheet.getXsl());
            form.setXslContent(stylesheet.getXslContent());
            form.setXslFileName(stylesheet.getXslFileName());
            form.setModified(stylesheet.getModified());
            form.setChecksum(stylesheet.getChecksum());
            form.setSchemas(stylesheet.getSchemas());
            // set empty string if dependsOn is null to avoid struts error in define tag:
            // Define tag cannot set a null value
            form.setDependsOn(stylesheet.getDependsOn() == null ? "" : stylesheet.getDependsOn());

            if (stylesheet.getSchemas().size() > 0) {
                //set first schema for Run Conversion link
                form.setSchema(stylesheet.getSchemas().get(0).getSchema());
                // check if any related schema has type=EXCEL, if yes, then depends on info should be visible
                List<Schema> relatedSchemas = new ArrayList<Schema>(stylesheet.getSchemas());
                CollectionUtils.filter(relatedSchemas, new BeanPredicate("schemaLang", new EqualPredicate("EXCEL")));
                if (relatedSchemas.size() > 0) {
                    form.setShowDependsOnInfo(true);
                    List<Stylesheet> existingStylesheets = new ArrayList<Stylesheet>();
                    for (Schema relatedSchema : relatedSchemas) {
                        CollectionUtils.addAll(existingStylesheets, stylesheetManager.getSchemaStylesheets(relatedSchema.getId(),
                                conversionId).toArray());
                    }
                    form.setExistingStylesheets(existingStylesheets);
                }
            }
            types = stylesheetManager.getConvTypes();

            /** FIXME - do we need the list of DD XML Schemas on the page
             StylesheetListHolder stylesheetList = StylesheetListLoader.getGeneratedList(httpServletRequest);
             List<Schema> schemas = stylesheetList.getDdStylesheets();
             httpServletRequest.setAttribute("stylesheet.DDSchemas", schemas);
             */


            /*
            String schemaId = schema.getSchemaId(stylesheet.getSchema());
            if (!Utils.isNullStr(schemaId)) {
                httpServletRequest.setAttribute("schemaInfo", schema.getSchema(schemaId));
                httpServletRequest.setAttribute("existingStylesheets", stylesheetManager.getSchemaStylesheets(schemaId, stylesheetId));
            }
            */
            //httpServletRequest.setAttribute(StylesheetListLoader.STYLESHEET_LIST_ATTR, StylesheetListLoader.getStylesheetList(httpServletRequest));

        } catch (DCMException e) {
            LOGGER.error("Edit stylesheet error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }

        model.addAttribute("form", form);
        model.addAttribute("types", types);
        return "/conversions/view";
    }

    @PostMapping(params = "delete")
    public String delete(@ModelAttribute ConversionForm cForm, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        try {
            String stylesheetId = httpServletRequest.getParameter("conversionId");
            String userName = (String) httpServletRequest.getSession().getAttribute("user");

            httpServletRequest.setAttribute("schema", httpServletRequest.getParameter("schema"));
            StylesheetManager sm = new StylesheetManager();
            sm.delete(userName, stylesheetId);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            success.add(messageService.getMessage("label.stylesheet.deleted"));
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
        } catch (DCMException e) {
            LOGGER.error("Error deleting stylesheet", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            return "redirect:/conversions";
        }
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, success);
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "redirect:/conversions";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        StylesheetForm form = new StylesheetForm();

        ConvTypeHolder ctHolder = new ConvTypeHolder();
        StylesheetManager stylesheetManager = new StylesheetManager();

        try {
            Stylesheet stylesheet = stylesheetManager.getStylesheet(id);

            if (stylesheet == null) {
                LOGGER.error("not found");
                errors.add(messageService.getMessage("not found"));
                model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
                return "redirect:/conversions";
            }

            form.setDescription(stylesheet.getDescription());
            form.setOutputtype(stylesheet.getType());
            form.setStylesheetId(stylesheet.getConvId());
            form.setXsl(stylesheet.getXsl());
            form.setXslContent(stylesheet.getXslContent());
            form.setXslFileName(stylesheet.getXslFileName());
            form.setModified(stylesheet.getModified());
            form.setChecksum(stylesheet.getChecksum());
            form.setSchemas(stylesheet.getSchemas());
            // set empty string if dependsOn is null to avoid struts error in define tag:
            // Define tag cannot set a null value
            form.setDependsOn(stylesheet.getDependsOn() == null ? "" : stylesheet.getDependsOn());

            if (stylesheet.getSchemas().size() > 0) {
                //set first schema for Run Conversion link
                form.setSchema(stylesheet.getSchemas().get(0).getSchema());
                // check if any related schema has type=EXCEL, if yes, then depends on info should be visible
                List<Schema> relatedSchemas = new ArrayList<Schema>(stylesheet.getSchemas());
                CollectionUtils.filter(relatedSchemas, new BeanPredicate("schemaLang", new EqualPredicate("EXCEL")));
                if (relatedSchemas.size() > 0) {
                    form.setShowDependsOnInfo(true);
                    List<Stylesheet> existingStylesheets = new ArrayList<Stylesheet>();
                    for (Schema relatedSchema : relatedSchemas) {
                        CollectionUtils.addAll(existingStylesheets, stylesheetManager.getSchemaStylesheets(relatedSchema.getId(), id).toArray());
                    }
                    form.setExistingStylesheets(existingStylesheets);
                }
            }
            ctHolder = stylesheetManager.getConvTypes();
            model.addAttribute("form", form);
            model.addAttribute("outputtypes", ctHolder);
            /** FIXME - do we need the list of DD XML Schemas on the page
             StylesheetListHolder stylesheetList = StylesheetListLoader.getGeneratedList(httpServletRequest);
             List<Schema> schemas = stylesheetList.getDdStylesheets();
             httpServletRequest.setAttribute("stylesheet.DDSchemas", schemas);
             */


            /*
            String schemaId = schema.getSchemaId(stylesheet.getSchema());
            if (!Utils.isNullStr(schemaId)) {
                httpServletRequest.setAttribute("schemaInfo", schema.getSchema(schemaId));
                httpServletRequest.setAttribute("existingStylesheets", stylesheetManager.getSchemaStylesheets(schemaId, stylesheetId));
            }
            */
            //httpServletRequest.setAttribute(StylesheetListLoader.STYLESHEET_LIST_ATTR, StylesheetListLoader.getStylesheetList(httpServletRequest));

        } catch (DCMException e) {
            LOGGER.error("Edit stylesheet error", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/conversions/{id}";
        }
        model.addAttribute(SpringMessages.SUCCESS_MESSAGES, success);

//        //TODO why is it needed to update session attribute in each request
//        httpServletRequest.getSession().setAttribute("stylesheet.outputtype", ctHolder);
        return "/conversions/edit";
    }

    @GetMapping("/delete")
    public String schemaDelete(Model model, HttpServletRequest httpServletRequest) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        SchemaForm form = new SchemaForm();
        String schemaId = form.getSchemaId();

        String user_name = (String) httpServletRequest.getSession().getAttribute("user");

        try {
            SchemaManager sm = new SchemaManager();
            sm.deleteSchemaStylesheets(user_name, schemaId);
            StylesheetListLoader.reloadStylesheetList(httpServletRequest);
            StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
            success.add(messageService.getMessage("label.stylesheets.deleted"));
        } catch (DCMException e) {
            LOGGER.error("Error deleting schema", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
        }

        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        model.addAttribute(SpringMessages.SUCCESS_MESSAGES, success);
        return "/conversions/list";
    }

    @GetMapping("/type")
    public String type(Model model, HttpServletRequest httpServletRequest) {

        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        ConvTypeHolder ctHolder = new ConvTypeHolder();
        String schema = httpServletRequest.getParameter("schema");
        httpServletRequest.setAttribute("schema", schema);

        try {
            StylesheetManager sm = new StylesheetManager();
            ctHolder = sm.getConvTypes();
            SchemaManager schemaMan = new SchemaManager();

            StylesheetListHolder stylesheetList = StylesheetListLoader.getGeneratedList(httpServletRequest);
            List<Schema> schemas = stylesheetList.getDdStylesheets();
            httpServletRequest.setAttribute("stylesheet.DDSchemas", schemas);

            if (!Utils.isNullStr(schema)) {
                String schemaId = schemaMan.getSchemaId(schema);
                if (schemaId != null) {
                    httpServletRequest.setAttribute("schemaInfo", schemaMan.getSchema(schemaId));
                    httpServletRequest.setAttribute("existingStylesheets", sm.getSchemaStylesheets(schemaId, null));
                }
            }

        } catch (DCMException e) {
            LOGGER.error("Error getting conv types", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            model.addAttribute("errors", errors);
        }
        httpServletRequest.getSession().setAttribute("stylesheet.outputtype", ctHolder);
        model.addAttribute("success", success);
        // todo fix url
        return "/conversions/type";
    }


    @GetMapping("/add")
    public String add(Model model) throws DCMException {
        SchemaManager sm = new SchemaManager();
        StylesheetForm form = new StylesheetForm();
        StylesheetManager stylesheetManager = new StylesheetManager();
        model.addAttribute("outputtypes", stylesheetManager.getConvTypes());
        model.addAttribute("form", form);
        return "/conversions/add";
    }

    @PostMapping(params = "add")
    public String addSubmit(@ModelAttribute StylesheetForm form, Model model, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes) {
        SpringMessages success = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        Stylesheet stylesheet = ConversionsUtils.convertFormToStylesheetDto(form, httpServletRequest);

        MultipartFile xslFile = form.getXslfile();
        //todo fix
        String schemaId = form.getSchemaId();
        String user = (String) httpServletRequest.getSession().getAttribute("user");
        String schema = (form.getNewSchemas() == null || form.getNewSchemas().size() == 0) ? null : form.getNewSchemas().get(0);
        redirectAttributes.addFlashAttribute("schema", schema);

        // TODO FIX THIS:
        // || xslFile.getFileSize() == 0) {
        if (xslFile == null) {
            errors.add(messageService.getMessage("label.stylesheet.validation"));
            model.addAttribute("errors", errors);
            return "redirect:/conversions/list";
        }
        String description = form.getDescription();
        if (description == null || description.isEmpty()) {
            errors.add(messageService.getMessage("label.stylesheet.error.descriptionMissing"));
            model.addAttribute("errors", errors);
            return "redirect:/conversions/list";
        }
        stylesheet.setXslFileName(xslFile.getOriginalFilename());
        try {
            // TODO FIX THIS: xslFile.getFileData()
            stylesheet.setXslContent(new String(xslFile.getBytes(), "UTF-8"));
        } catch (Exception e) {
            LOGGER.error("Error in edit stylesheet action when trying to load XSL file content from FormFile object", e);
            errors.add(messageService.getMessage(BusinessConstants.EXCEPTION_GENERAL));
            return "redirect:/conversions/list";
        } finally {
            /*xslFile.destroy();*/
        }
        ConversionsUtils.validateXslFile(stylesheet, errors);

        if (errors.isEmpty()) {
            try {
                StylesheetManager stylesheetManager = new StylesheetManager();
                // stylesheetManager.add(user, schema, xslFile, type, desc, dependsOn);
                stylesheetManager.add(stylesheet, user);
                success.add(messageService.getMessage("label.stylesheet.inserted"));
                StylesheetListLoader.reloadStylesheetList(httpServletRequest);
                StylesheetListLoader.reloadConversionSchemasList(httpServletRequest);
            } catch (DCMException e) {
                LOGGER.error("Add stylesheet error", e);
                errors.add(messageService.getMessage(e.getErrorCode()));
            }
        }
        redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, success);
        if (!StringUtils.isEmpty(schemaId)) {
            return "redirect:/schemas/" + schemaId + "/conversions";
        } else {
            return "redirect:/conversions/";
        }
    }

    /**
     * check if schema passed as request parameter exists in the list of schemas stored in the session. If there is no schema list
     * in the session, then create it
     *
     * @param httpServletRequest Request
     * @param schema Schema
     * @return True if schema exists
     * @throws DCMException If an error occurs.
     */
    private boolean schemaExists(HttpServletRequest httpServletRequest, String schema) throws DCMException {
        List<Schema> schemasInCache = StylesheetListLoader.getConversionSchemasList(httpServletRequest);

        Schema oSchema = new Schema();
        oSchema.setSchema(schema);
        return schemasInCache.contains(oSchema);
    }
}
