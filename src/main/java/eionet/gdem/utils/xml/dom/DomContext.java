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
 *    Original code: Nedeljko Pavlovic (ED)
 */

package eionet.gdem.utils.xml.dom;

import eionet.gdem.utils.xml.*;
import org.w3c.dom.Document;

/**
 * XML Context Class.
 * @author Unknown
 * @author George Sofianos
 */
public class DomContext extends XmlCommon implements IXmlCtx {
    /**
     * Default constructor
     */
    public DomContext() {
    }

    public XmlUpdater getManager() {
        return new XmlManager(this);
    }

    public DomXpath getQueryManager() {
        return new DomXpath(this);
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

}
