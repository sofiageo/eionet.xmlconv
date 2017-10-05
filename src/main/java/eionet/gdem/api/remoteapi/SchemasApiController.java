package eionet.gdem.api.remoteapi;

import eionet.gdem.conversions.ConversionService;
import eionet.gdem.exceptions.XMLResult;
import eionet.gdem.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *
 *
 */
@Controller
@RequestMapping("/getXMLSchemas")
public class SchemasApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemasApiController.class);
    private MessageService messageService;
    private final ConversionService conversionService;

    @Autowired
    public SchemasApiController(MessageService messageService, ConversionService conversionService) {
        this.messageService = messageService;
        this.conversionService = conversionService;
    }

    @GetMapping
    public ResponseEntity action(HttpServletRequest request) throws Exception {

        // get request parameters
        Map params = request.getParameterMap();

        // Call ConversionService
        List schemas = conversionService.getXMLSchemas();
        // parse the result of Conversion Service method and format it as XML
        // TODO Replace
        GetXMLSchemasResult xmlResult = new GetXMLSchemasResult();
        xmlResult.setResult(schemas);
        // Do nothing, then response is already sent.
        return new ResponseEntity(HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<XMLResult> handleExceptions(Exception ex, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map params = request.getParameterMap();
        XMLResult xml = new XMLResult();
        xml.setParams(params);
        xml.setMessage(ex.getMessage());
        xml.setUrl("/getXMLSchemas");
        return ResponseEntity.badRequest().body(xml);
    }
}
