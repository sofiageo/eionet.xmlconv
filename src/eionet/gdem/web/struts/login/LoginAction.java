/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * 
 * The Original Code is Web Dashboards Service
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency (EEA).  Portions created by European Dynamics (ED) company are
 * Copyright (C) by European Environment Agency.  All Rights Reserved.
 * 
 * Contributors(s):
 *    Original code: Dusan Popovic (ED)
 *                          Nedeljko Pavlovic (ED) 
 */

package eionet.gdem.web.struts.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import com.tee.uit.security.AppUser;


import eionet.gdem.conversion.ssr.Names;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.SecurityUtil;
import eionet.gdem.utils.Utils;

public class LoginAction extends Action {
    
	 private static LoggerIF _logger=GDEMServices.getLogger();
	 protected final String GDEM_SSAclName = "/stylesheets";
	
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        ActionMessages loginMessages = new ActionMessages();
        ActionErrors errors = new ActionErrors();
        ActionForward ret = null;

        LoginForm loginForm = (LoginForm) actionForm;
        String username = loginForm.getUsername();
        String password = loginForm.getPassword();
        boolean haveCookie = false;

        //UserFacade facade = new UserFacade();
        //User user = facade.authenticate(username, password);
		try{
			doLogin(username, password,httpServletRequest);
			_logger.debug("Success login");
			ret = actionMapping.findForward("home");
			httpServletRequest.getSession().setAttribute("user", username);
		}catch(Exception e){
			_logger.debug("Fail login");
            errors.add("username", new ActionError("label.login.error.invalid"));
            ret = actionMapping.getInputForward();	        		
		}
		

        saveMessages(httpServletRequest, loginMessages);
        saveErrors(httpServletRequest, errors);
        return ret;


    }

    private void SaveCookie(HttpServletResponse httpServletResponse, String username, String password) {
        Cookie cookie = new Cookie("user", username + " " + password);
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        //cookie.setVersion(1);
		//cookie.setPath("");
        httpServletResponse.addCookie(cookie);
    }
    private void doLogin(String username, String password, HttpServletRequest httpServletRequest) throws Exception {


	      try {
	        //DirectoryService.sessionLogin(u, p);
	        AppUser aclUser = new AppUser();

	        if(!Utils.isNullStr(username))
	          aclUser.authenticate(username,password);

	        if (!SecurityUtil.hasPerm(username,GDEM_SSAclName, "v"))//GDEM_readPermission))
	          throw new Exception("Not allowed to use the Styelsheet Repository");

			
	        //session.setAttribute(Names.USER_ATT, aclUser);
			//add object into session becouse of old bussines ligic
			httpServletRequest.getSession().setAttribute(Names.USER_ATT, aclUser);

	         
	    } catch (Exception dire ){
		  _logger.debug( "Authentication failed " + dire.toString());
	      throw new Exception("Authentication failed ");
	    }
	    
	 }

}
