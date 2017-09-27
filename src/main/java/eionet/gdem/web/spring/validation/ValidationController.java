package eionet.gdem.web.spring.validation;

import eionet.gdem.Constants;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dto.ValidateDto;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.qa.QARestService;
import eionet.gdem.qa.QAService;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
import eionet.gdem.web.spring.SpringMessages;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 *
 *
 */
@Controller
@RequestMapping("/validation")
public class ValidationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationController.class);
    private MessageService messageService;
    private QARestService qaRestService;

    @Autowired
    public ValidationController(MessageService messageService, QARestService qaRestService) {
        this.messageService = messageService;
        this.qaRestService = qaRestService;
    }

    @GetMapping
    public String form(Model model) {

        SpringMessages errors = new SpringMessages();
        ValidationForm form = new ValidationForm();
        model.addAttribute("form", form);
        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        return "/validation";
    }

    @PostMapping
    public String formSubmit(@ModelAttribute ValidationForm cForm, RedirectAttributes redirectAttributes, HttpSession session, HttpServletRequest request, HttpServletResponse response) {

        String ticket = (String) session.getAttribute(Constants.TICKET_ATT);
        SpringMessages errors = new SpringMessages();


        String url = cForm.getXmlUrl();
        String schema = cForm.getSchemaUrl();

        if (Utils.isNullStr(url)) {
            errors.add(messageService.getMessage("label.conversion.selectSource"));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/validation";
        }
        if (!Utils.isURL(url)) {
            errors.add(messageService.getMessage(BusinessConstants.EXCEPTION_CONVERT_URL_MALFORMED));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/validation";
        }

        try {
            /*List<ValidateDto> valid;*/
            String validatedSchema = null;
            String originalSchema = null;
            String warningMessage = null;
            CompletableFuture<ValidateDto[]> future;
            future = qaRestService.executeValidation(url, schema);
            ValidateDto[] valid = future.get();

/*  TODO: experimental
           try {
                ValidationService v = new JaxpValidationService();
                //v.setTrustedMode(false);
                //v.setTicket(ticket);
                if (schema == null) {
                    v.validate(url);
                } else {
                    v.validateSchema(url, schema);
                }
                valid = v.getErrorList();
                validatedSchema = v.getValidatedSchemaURL();
                originalSchema = v.getOriginalSchema();
                warningMessage = v.getWarningMessage();
            } catch (DCMException dcme) {
                throw dcme;
            } catch (Exception e) {
                throw new DCMException(BusinessConstants.EXCEPTION_VALIDATION_ERROR);
            }*/
            request.setAttribute("conversion.valid", valid);
            request.setAttribute("conversion.originalSchema", originalSchema);
            if (!StringUtils.equals(originalSchema, validatedSchema)) {
                request.setAttribute("conversion.validatedSchema", validatedSchema);
            }
            request.setAttribute("conversion.warningMessage", warningMessage);
        } catch (DCMException e) {
            LOGGER.error("Error validating xml", e);
            errors.add(messageService.getMessage(e.getErrorCode()));
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
            return "redirect:/validation";
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "redirect:/validation";
    }

}