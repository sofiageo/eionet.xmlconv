package eionet.gdem.web.spring.qasandbox;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 */
@Controller
@RequestMapping("/qaSandbox")
public class QASandboxController {

    @GetMapping("/execute")
    public String execute(Model model) {


        return "qasandbox-result";
    }
}
