package eionet.gdem.web.struts.stylesheet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

public class ConvTypeAction  extends Action{

	private static LoggerIF _logger=GDEMServices.getLogger();
	
	   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

			ConvTypeHolder ctHolder = new ConvTypeHolder();
			ActionMessages errors = new ActionMessages();
	        ActionMessages messages = new ActionMessages();		

			String schema= (String)httpServletRequest.getParameter("schema");
			httpServletRequest.setAttribute("schema",schema);
			
			try{
				StylesheetManager sm = new StylesheetManager();
				ctHolder =sm.getConvTypes();				
			}catch(DCMException e){
				e.printStackTrace();
				_logger.error(e);
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
				saveErrors(httpServletRequest, errors);
			}
	        httpServletRequest.getSession().setAttribute("stylesheet.outputtype", ctHolder);
	        return actionMapping.findForward("success");
	    }

}
