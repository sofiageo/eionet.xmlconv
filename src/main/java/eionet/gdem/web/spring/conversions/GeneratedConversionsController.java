package eionet.gdem.web.spring.conversions;

import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.MessageService;
import eionet.gdem.web.spring.SpringMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 *
 *
 */
@Controller
@RequestMapping("/conversions/generated")
public class GeneratedConversionsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratedConversionsController.class);
    private MessageService messageService;
    private StylesheetListLoader stylesheetListLoader;

    @Autowired
    public GeneratedConversionsController(MessageService messageService, StylesheetListLoader stylesheetListLoader) {
        this.messageService = messageService;
        this.stylesheetListLoader = stylesheetListLoader;
    }

    @GetMapping
    public String list(Model model, HttpServletRequest request) {

        SpringMessages errors = new SpringMessages();

        try {
            model.addAttribute("conversions", stylesheetListLoader.getGeneratedList(request));
        } catch (DCMException e) {
            LOGGER.error("Error getting stylesheet list", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
        }
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/conversions/generated";
    }

}
