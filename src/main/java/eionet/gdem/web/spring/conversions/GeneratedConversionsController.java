package eionet.gdem.web.spring.conversions;

import eionet.gdem.Properties;
import eionet.gdem.dcm.Conversion;
import eionet.gdem.dcm.XslGenerator;
import eionet.gdem.dto.ConversionDto;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.SpringMessages;
import eionet.gdem.web.spring.schemas.SchemaManager;
import eionet.gdem.web.spring.stylesheet.StylesheetListHolder;
import eionet.gdem.web.spring.stylesheet.StylesheetListLoader;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 *
 *
 */
@Controller
@RequestMapping("/conversions")
public class GeneratedConversionsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratedConversionsController.class);
    private MessageService messageService;

    @Autowired
    public GeneratedConversionsController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/generated")
    public String list(Model model, HttpServletRequest request) {

        try {
            model.addAttribute("conversions", StylesheetListLoader.getGeneratedList(request));
        } catch (DCMException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }
        return "/conversions/generated";
    }

    @GetMapping(value = "/generated/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public byte[] getConversion(@PathVariable String id, @RequestParam(value = "conv", required = false) String convId) {

        String metaXSLFolder = Properties.metaXSLFolder;
        String tableDefURL = Properties.ddURL;

        try {
            ConversionDto conv = Conversion.getConversionById(convId);
            String format = metaXSLFolder + File.separatorChar + conv.getStylesheet();
            String url = tableDefURL + "/GetTableDef?id=" + id;
            return IOUtils.toByteArray(XslGenerator.convertXML(url, format));
        } catch (Exception ge) {
            LOGGER.error("", ge);
//            errors.add(messageService.getMessage("label.stylesheet.error.generation"));
//            model.addAttribute("dcm.errors", errors);
            return null;
        }
    }

    @GetMapping(value = "/generated", params = { "schemaUrl" })
    public String show(@ModelAttribute("schemaUrl") String schemaUrl, Model model) {

        // TODO fix this - not working
        SchemaManager sm = new SchemaManager();
        StylesheetListHolder st = null;
        try {
            st = sm.getSchemaStylesheetsList(schemaUrl);
        } catch (DCMException e) {
            throw new RuntimeException(messageService.getMessage("label.exception.unknown"));
        }
        model.addAttribute("conversions", st);

        return "/schemas/conversions";
    }
}
