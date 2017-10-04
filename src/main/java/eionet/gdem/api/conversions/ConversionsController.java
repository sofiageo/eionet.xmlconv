package eionet.gdem.api.conversions;

import eionet.gdem.api.conversions.model.ConversionResponse;
import eionet.gdem.conversions.StylesheetManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 */
@RestController("/conversions")
public class ConversionsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionsController.class);
    private StylesheetManager stylesheetManager;

    @Autowired
    public ConversionsController(StylesheetManager stylesheetManager) {
        this.stylesheetManager = stylesheetManager;
    }

    @GetMapping("/convert")
    public ResponseEntity<ConversionResponse> convert() {
        ConversionResponse res = new ConversionResponse();
        stylesheetManager.getStylesheets();
        res.setId(0);
        LOGGER.info("Conversion completed: " + res.getId());
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
