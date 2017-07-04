package eionet.xmlconv.qa.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/")
    public String execute() {

    }
}
