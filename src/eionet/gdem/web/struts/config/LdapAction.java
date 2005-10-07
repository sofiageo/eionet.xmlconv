package eionet.gdem.web.struts.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.dcm.conf.DcmProperties;
import eionet.gdem.dcm.conf.LdapTest;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.web.struts.stylesheet.StylesheetForm;

public class LdapAction extends Action {

	private static LoggerIF _logger=GDEMServices.getLogger();

    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		
		ActionMessages errors = new ActionMessages();
        ActionMessages messages = new ActionMessages();		
		
				
		LdapForm form=(LdapForm)actionForm;
		String url= form.getUrl();
		String user = (String)httpServletRequest.getSession().getAttribute("user");
		
		
				
		try{

			if (!SecurityUtil.hasPerm(user, "/" + Names.ACL_CONFIG_PATH, "u")){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.autorization.config.update"));
				httpServletRequest.getSession().setAttribute("dcm.errors", errors);						
				return actionMapping.findForward("success");				
			}
			
			if(url == null || url.equals("")){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.config.ldap.url.validation"));
				httpServletRequest.getSession().setAttribute("dcm.errors", errors);						
				return actionMapping.findForward("success");
			}
			
			
			LdapTest lt = new LdapTest(url);
			if(!lt.test()){
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.editParam.ldap.testFailed"));
				httpServletRequest.getSession().setAttribute("dcm.errors", errors);						
				return actionMapping.findForward("success");				
			}
			
			DcmProperties dcmProp = new DcmProperties();
			
			dcmProp.setLdapParams(url); 			
		}catch(DCMException e){			
			e.printStackTrace();
			_logger.error(e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
			saveErrors(httpServletRequest,errors);
		}catch(Exception e){
			e.printStackTrace();
			_logger.error(e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.exception.unknown"));
			saveErrors(httpServletRequest,errors);
		}
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("label.editParam.ldap.saved"));
		
        httpServletRequest.getSession().setAttribute("dcm.errors", errors);
        httpServletRequest.getSession().setAttribute("dcm.messages", messages);						
        return actionMapping.findForward("success");
	}		
	
	

}