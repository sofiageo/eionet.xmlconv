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

package eionet.xmlconv.conversions.xml;

/**
 * XML Exception class.
 * TODO: Add cause constructor
 * @author Unknown
 * @author George Sofianos
 */
public class XmlException extends Exception {
    /**
     * Default constructor
     */
    public XmlException() {
    }

    /**
     * Constructor
     * @param msg Message
     */
    public XmlException(String msg) {
        super(msg);
    }

    /**
     * Constructor
     * @param e Exception
     */
    public XmlException(Exception e) {
        super(e);
    }
}
