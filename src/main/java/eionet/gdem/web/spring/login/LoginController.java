package eionet.gdem.web.spring.login;

import eionet.acl.AppUser;
import eionet.gdem.Constants;
import eionet.gdem.services.MessageService;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.struts.qascript.QAScriptListLoader;
import eionet.gdem.web.struts.stylesheet.StylesheetListLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    public static final String AFTER_LOGIN_ATTR_NAME = "afterLogin";
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    private MessageService messageService;

    public LoginController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public String getForm(Model model, HttpSession session) {

        return "";
    }

    @GetMapping("/afterLogin")
    public String afterLogin(Model model, HttpServletRequest httpServletRequest) {
        AppUser aclUser = SecurityUtil.getUser(httpServletRequest, Constants.USER_ATT);

        // remove session data, that contains permission related attributes
        QAScriptListLoader.loadPermissions(httpServletRequest);
        StylesheetListLoader.loadPermissions(httpServletRequest);

        String afterLogin = (String) httpServletRequest.getSession().getAttribute(AFTER_LOGIN_ATTR_NAME);

        if (afterLogin != null && !afterLogin.toLowerCase().contains("/tiles/layout.jsp")) {
            //httpServletResponse.sendRedirect(afterLogin);
            return null;
        }

        return "/";
    }
}