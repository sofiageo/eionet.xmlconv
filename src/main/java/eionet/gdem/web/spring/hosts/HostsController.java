package eionet.gdem.web.spring.hosts;

import eionet.gdem.Constants;
import eionet.gdem.dto.HostDto;
import eionet.gdem.services.MessageService;
import eionet.gdem.services.db.dao.IHostDao;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.spring.SpringMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 *
 */
@Controller
@RequestMapping("/hosts")
public class HostsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HostsController.class);

    private MessageService messageService;
    private IHostDao hostDao;

    @Autowired
    public HostsController(MessageService messageService, IHostDao hostDao) {
        this.messageService = messageService;
        this.hostDao = hostDao;
    }

    @GetMapping
    public String list(Model model, HttpSession session) {

        SpringMessages errors = new SpringMessages();

        List result = new ArrayList();
        String user = (String) session.getAttribute("user");
        try {
            if (!SecurityUtil.hasPerm(user, "/" + Constants.ACL_HOST_PATH, "v")) {
                Vector list = hostDao.getHosts(null);
                for (int i = 0; i < list.size(); i++) {
                    Hashtable host = (Hashtable) list.get(i);
                    HostDto h = new HostDto();
                    h.setId((String) host.get("host_id"));
                    h.setHostname((String) host.get("host_name"));
                    h.setUsername((String) host.get("user_name"));
                    result.add(h);
                }
            } else {
                errors.add(messageService.getMessage("error.vnoperm", "label.hosts"));
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
        }

        model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        model.addAttribute("hosts.list", result);

        return "/hosts/list.jsp";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable String id, Model model, HttpSession session) {

        HostForm hostForm = new HostForm();
        String user = (String) session.getAttribute("user");
        SpringMessages errors = new SpringMessages();
        try {
            if (!SecurityUtil.hasPerm(user, Constants.ACL_HOST_PATH, "u")) {
                Vector hosts = hostDao.getHosts(id);

                if (hosts != null) {
                    Hashtable host = (Hashtable) hosts.get(0);
                    hostForm.setId((String)host.get("host_id"));
                    hostForm.setHost((String)host.get("host_name"));
                    hostForm.setUsername((String)host.get("user_name"));
                    hostForm.setPassword((String)host.get("pwd"));
                }
            } else {
                errors.add(messageService.getMessage("error.unoperm", "label.hosts"));
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
        }

        if (errors.size() > 0) {
            // request.getSession().setAttribute("dcm.errors", errors);
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }
        model.addAttribute("form", hostForm);
        return "/hosts/details.jsp";
    }

    @PostMapping("/edit/{id}")
    public String editSubmit(@ModelAttribute HostForm updatedForm, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        SpringMessages messages = new SpringMessages();
        SpringMessages errors = new SpringMessages();

        String hostId = updatedForm.getId();
        String host = updatedForm.getHost();
        String username = updatedForm.getUsername();
        String password = updatedForm.getPassword();

        String user = (String) session.getAttribute("user");

        try {
            if (hostId == null) { // Add new host
                LOGGER.debug("ADDING NEW HOST !!!");
                if (!SecurityUtil.hasPerm(user, Constants.ACL_HOST_PATH, "i")) {
                    hostDao.addHost(host, username, password);
                    messages.add(messageService.getMessage("label.hosts.inserted"));
                } else {
                    errors.add(messageService.getMessage("error.inoperm"));
                }
            } else { // Update host
                LOGGER.debug("UPDATE HOST !!!");
                if (!SecurityUtil.hasPerm(user, Constants.ACL_HOST_PATH, "u")) {
                    hostDao.updateHost(hostId, host, username, password);
                    messages.add(messageService.getMessage("label.hosts.updated"));
                } else {
                    errors.add(messageService.getMessage("error.unoperm", "label.hosts"));
                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
        }

        if (errors.size() > 0) {
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }
        if (messages.size() > 0) {
            redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        }
        return "redirect:/web/hosts/edit/{id}";
    }

    @GetMapping("/add")
    public String add(Model model, HttpSession session) {
        SpringMessages errors = new SpringMessages();
        String user = (String) session.getAttribute("user");
        HostForm form = new HostForm();

        try {
            if (!SecurityUtil.hasPerm(user, Constants.ACL_HOST_PATH, "i")) {
                errors.add(messageService.getMessage("error.inoperm", "label.hosts"));
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
        }
        if (errors.size() > 0) {
            model.addAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }
        model.addAttribute("form", form);
        return "hosts/add";
    }

    @PostMapping("/delete")
    public String delete(@ModelAttribute HostForm form, RedirectAttributes redirectAttributes, HttpSession session) {
        SpringMessages errors = new SpringMessages();
        SpringMessages messages = new SpringMessages();

        String user = (String) session.getAttribute("user");

        try {
            if (!SecurityUtil.hasPerm(user, Constants.ACL_HOST_PATH, "d")) {
                hostDao.removeHost(form.getId());
                messages.add(messageService.getMessage("label.hosts.deleted"));
            } else {
                errors.add(messageService.getMessage("error.dnoperm", "label.hosts"));
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            errors.add(messageService.getMessage("label.exception.unknown"));
        }

        if (errors.size() > 0) {
            redirectAttributes.addFlashAttribute(SpringMessages.ERROR_MESSAGES, errors);
        }
        if (messages.size() > 0) {
            redirectAttributes.addFlashAttribute(SpringMessages.SUCCESS_MESSAGES, messages);
        }

        return "redirect:/web/hosts";
    }

}