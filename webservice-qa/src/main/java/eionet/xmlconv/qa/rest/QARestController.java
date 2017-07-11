package eionet.xmlconv.qa.rest;

import eionet.xmlconv.qa.model.QARequest;
import eionet.xmlconv.qa.model.QAResponse;
import eionet.xmlconv.qa.model.QAScript;
import eionet.xmlconv.qa.services.basex.BaseXLocalService;
import eionet.xmlconv.qa.services.saxon.SaxonService;
import eionet.xmlconv.qa.services.validation.ValidationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private ValidationService validationService;
    private ModelMapper modelMapper;

    @Autowired
    public QARestController(BaseXLocalService baseXService, SaxonService saxonService, ValidationService validationService, ModelMapper modelMapper) {
        this.baseXService = baseXService;
        this.saxonService = saxonService;
        this.validationService = validationService;
        this.modelMapper = modelMapper;
    }

    // TODO: decide on one or many endpoints
    @PostMapping("/basex")
    public ResponseEntity<QAResponse> basex(@RequestBody QARequest request) {
        QAScript script = modelMapper.map(request, QAScript.class);

        // TODO: investigate for reactive streams or queues to handle errors
        String xqueryResult = baseXService.execute(script);
        QAResponse qaResponse = new QAResponse();
        qaResponse.setQaResult(xqueryResult);
        return ResponseEntity.status(HttpStatus.OK).body(qaResponse);
    }

    @PostMapping("/saxon")
    public ResponseEntity<QAResponse> saxon() {
        QAResponse qaResponse = new QAResponse();
        return ResponseEntity.status(HttpStatus.OK).body(qaResponse);
    }

    @PostMapping("/validate")
    public ResponseEntity<QAResponse> validate() {
        QAResponse qaResponse = new QAResponse();
        return ResponseEntity.status(HttpStatus.OK).body(qaResponse);
    }

    @PostMapping("/external")
    public ResponseEntity<QAResponse>  external() {
        QAResponse qaResponse = new QAResponse();
        return ResponseEntity.status(HttpStatus.OK).body(qaResponse);
    }
}
