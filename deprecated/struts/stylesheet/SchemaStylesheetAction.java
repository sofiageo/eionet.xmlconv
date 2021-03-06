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
 *    Original code: Istvan Alfeldi (ED)
 */

package eionet.gdem.web.struts.stylesheet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;

import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.exceptions.DCMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaStylesheetAction extends Action {

    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaStylesheetAction.class);

    @Override
    public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        StylesheetListHolder st = new StylesheetListHolder();
        ActionMessages messages = new ActionMessages();

        DynaActionForm df = (DynaActionForm) actionForm;
        String schema = (String) df.get("schema");

        if (schema == null || schema.equals("")) {
            schema = (String) httpServletRequest.getAttribute("schema");
        }

        if (schema == null || schema.equals("")) {
            return actionMapping.findForward("home");
        }

        httpServletRequest.setAttribute("schema", schema);

        try {
            SchemaManager sm = new SchemaManager();
            st = sm.getSchemaStylesheetsList(schema);
            httpServletRequest.setAttribute("schema.stylesheets", st);

        } catch (DCMException e) {
            e.printStackTrace();
            LOGGER.error("Error getting stylesheet", e);
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getErrorCode()));
        }
        saveErrors(httpServletRequest, messages);

        return actionMapping.findForward("success");
    }
}
