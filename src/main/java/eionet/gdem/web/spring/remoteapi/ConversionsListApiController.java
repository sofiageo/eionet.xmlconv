package eionet.gdem.web.spring.remoteapi;

import eionet.gdem.deprecated.ConversionService;
import eionet.gdem.exceptions.XMLResult;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Vector;

/**
 *
 *
 */
@Controller
@RequestMapping("/listConversions")
public class ConversionsListApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionsListApiController.class);
    private MessageService messageService;

    protected static final String SCHEMA_PARAM_NAME = "schema";


    @GetMapping
    public ResponseEntity listConversions(HttpServletRequest request) throws Exception {
        // get request parameters
        Map params = request.getParameterMap();

            String schema = null;
            if (params.containsKey(SCHEMA_PARAM_NAME)) {
                schema = (String) ((Object[]) params.get(SCHEMA_PARAM_NAME))[0];
            }
            if (Utils.isNullStr(schema)) {
                schema = null;
            }

            // Call ConversionService
            ConversionService cs = new ConversionService();
            Vector v = cs.listConversions(schema);

            // parse the result of Conversion Service method and format it as XML
            ListConversionsResult xmlResult = new ListConversionsResult();
            xmlResult.setResult(v);
        return ResponseEntity.ok(xmlResult);
    }

    @ExceptionHandler
    public ResponseEntity<XMLResult> handleExceptions(Exception ex, HttpServletRequest request) throws Exception {
        Map params = request.getParameterMap();
        XMLResult xml = new XMLResult();
        /*xml.setParams = params;
        xml.setMessage = ex.getMessage();
        xml.setUrl = "/listConversions";*/
        return ResponseEntity.badRequest().body(xml);
    }
}
