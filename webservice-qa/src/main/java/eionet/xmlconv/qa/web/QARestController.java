package eionet.xmlconv.qa.web;

import eionet.xmlconv.qa.model.QAScriptDto;
import eionet.xmlconv.qa.services.QAService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public QARestController(QAService qaService) {
        this.qaService = qaService;
    }

    // TODO: decide on one or many endpoints
    @GetMapping("/basex")
    public String basex(@RequestBody QAApiDto script) {

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
