package eionet.gdem.web.struts;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.TilesRequestProcessor;

import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;



public class RequestProcessor extends TilesRequestProcessor {

	private static LoggerIF _logger=GDEMServices.getLogger();
	
	public RequestProcessor() {

		super();

	}

	/**
	 * Preprocess every action that is called from struts framework
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return boolean
	 */
	public boolean processPreprocess(HttpServletRequest request,
			HttpServletResponse response) {

		request.setAttribute("servletPath", request.getServletPath());
		_logger.debug("servletPath ----- " +request.getServletPath());
		return true;
	}

	protected ActionForward processActionPerform(HttpServletRequest request,
			HttpServletResponse response, Action action, ActionForm form,
			ActionMapping mapping) throws IOException, ServletException {

		String path = request.getServletPath();
		_logger.debug("servletPath ----- " +request.getServletPath());

		return super.processActionPerform(request, response, action, form, mapping);
	}
}