package eionet.xmlconv.qa.rest;

import eionet.xmlconv.qa.model.ErrorResponse;
import eionet.xmlconv.qa.model.QARequest;
import eionet.xmlconv.qa.model.QAResponse;
import eionet.xmlconv.qa.model.QAScript;
import eionet.xmlconv.qa.model.ValidationRequest;
import eionet.xmlconv.qa.model.ValidationResult;
import eionet.xmlconv.qa.services.basex.BaseXLocalService;
import eionet.xmlconv.qa.services.saxon.SaxonService;
import eionet.xmlconv.qa.services.validation.JaxpValidationService;
import eionet.xmlconv.qa.services.validation.ValidationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 */
@RestController
public class QARestController {

    private BaseXLocalService baseXService;
    private SaxonService saxonService;
    //private ValidationService validationService;
    private ModelMapper modelMapper;

    @Autowired
    public QARestController(BaseXLocalService baseXService, SaxonService saxonService, ModelMapper modelMapper) {
        this.baseXService = baseXService;
        this.saxonService = saxonService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/basex")
    public ResponseEntity<QAResponse> basex(@RequestBody QARequest request) {
        QAScript script = modelMapper.map(request, QAScript.class);
        String xqueryResult = baseXService.execute(script);
        QAResponse qaResponse = new QAResponse();
        qaResponse.setQaResult(xqueryResult);
        return ResponseEntity.status(HttpStatus.OK).body(qaResponse);
    }

    @PostMapping("/saxon")
    public ResponseEntity<QAResponse> saxon(@RequestBody QARequest request) {
        QAScript script = modelMapper.map(request, QAScript.class);
        String result = saxonService.execute(script);
        QAResponse qaResponse = new QAResponse();
        qaResponse.setQaResult(result);
        return ResponseEntity.status(HttpStatus.OK).body(qaResponse);
    }

    @PostMapping(value = "/validation", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ValidationResult> validate(@RequestBody ValidationRequest req) {
        ValidationService validationService = new JaxpValidationService();
        String sourceUrl = req.getSourceUrl();
        String schemaUrl = req.getSchemaUrl();
        ValidationResult res = validationService.validate(sourceUrl, schemaUrl);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/external")
    public ResponseEntity<QAResponse>  external() {
        QAResponse qaResponse = new QAResponse();
        return ResponseEntity.status(HttpStatus.OK).body(qaResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
