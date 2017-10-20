package eionet.gdem.api.conversions;

import eionet.gdem.conversions.ConversionRestService;
import eionet.gdem.conversions.StylesheetManager;
import eionet.gdem.exceptions.XMLConvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 *
 *
 */
@RestController("restConversions")
@RequestMapping("/conversions")
public class ConversionsController {

    private ConversionRestService conversionRestService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionsController.class);
    private StylesheetManager stylesheetManager;

    @Autowired
    public ConversionsController(ConversionRestService conversionRestService, StylesheetManager stylesheetManager) {
        this.conversionRestService = conversionRestService;
        this.stylesheetManager = stylesheetManager;
    }

    @PostMapping("/convert")
    public ResponseEntity<ConversionResponse> convert(@RequestBody ConversionRequest conversionRequest) {
        ConversionResponse res = new ConversionResponse();
        stylesheetManager.getStylesheets();
        String url = conversionRequest.getSourceUrl();
        String type = conversionRequest.getType();
        conversionRestService.convert(url, type);
        res.setId(0);
        LOGGER.info("Conversion completed: " + res.getId());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @ExceptionHandler({XMLConvException.class})
    public ResponseEntity<HashMap<String, String>> HandleXMLConvException(Exception exception, HttpServletResponse response) {
        LOGGER.error("XMLConv Exception:", exception);
        HashMap<String, String> errorResult = new HashMap<String, String>();
        errorResult.put("httpStatusCode", HttpStatus.INTERNAL_SERVER_ERROR.toString());
        errorResult.put("errorMessage", exception.getMessage());
        return new ResponseEntity<HashMap<String, String>>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
