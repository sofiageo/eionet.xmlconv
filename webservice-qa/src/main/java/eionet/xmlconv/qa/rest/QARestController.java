package eionet.xmlconv.qa.rest;

import eionet.xmlconv.qa.model.QAApiDto;
import eionet.xmlconv.qa.model.ResultDto;
import eionet.xmlconv.qa.services.QAService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 *
 */
@RestController
public class QARestController {

    private QAService qaService;
    private ModelMapper modelMapper;

    @Autowired
    public QARestController(QAService qaService, ModelMapper modelMapper) {
        this.qaService = qaService;
        this.modelMapper = modelMapper;
    }

    // TODO: decide on one or many endpoints
    @GetMapping("/basex")
    public ResponseEntity<ResultDto> basex(@RequestBody QAApiDto script) {
        QAScript script = modelMapper.map(script, QAScript.class);

        // TODO: investigate for reactive streams or queues to handle errors
        qaService.basexExecute(script);
    }

    @GetMapping("/saxon")
    public String saxon() {

    }

    @GetMapping("/validate")
    public String validate() {

    }

    @GetMapping("/external")
    public String external() {

    }
}
