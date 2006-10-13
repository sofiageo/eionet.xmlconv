/**
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
 * The Original Code is "GDEM project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Enriko Käsper (TietoEnator)
 */

package eionet.gdem;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Category;

/**
 * Several settings and properties for GDEM
 */
public class Properties {

	public static String appHome = null;

	public static String tmpFolder = "/tmp";

	//public static String urlPrefix="http://conversions.eionet.eu.int/";

	public static String xslFolder = "/xsl/";

	public static String odsFolder = "/opendoc/ods";

	public static String queriesFolder = "/queries/";

	public static String schemaFolder = "/schema/";

	public static final int CONV_SERVICE = 1; //Conversion service weight
	public static final int QA_SERVICE = 2; //QA service weight
	public static int services_installed = 3; //by default the both services are installed

	//public static String xformsFolder="/xforms/";

	//Database settings from the properties file
	public static String dbUrl = null;
	public static String dbDriver = null;
	public static String dbUser = null;
	public static String dbPwd = null;

	//Edit UI
	public static String uiFolder = null;

	//period for checking new jobs in the workqueue in milliseconds, default 20sec
	public static long wqCheckInterval = 20000L;

	//NB Saxon is the default value, not hard-coded!
	public static String engineClass = "eionet.gdem.qa.engines.SaxonImpl";

	//DCM settings from the properties file
	public static String convFile = null;
	public static String metaXSLFolder = null;
	public static String ddURL = null;
	public static String gdemURL = null;

	//DCM settings from the properties file of incoming services from DD
	public static String invServUrl = null;
	public static String invServName = null;

	//ldap url
	public static String ldapUrl = null;
	public static String ldapContext = null;
	public static String ldapUserDir = null;
	public static String ldapAttrUid = null;

	//DCM settings from the properties file of incoming services from CDR
	//CDR doesn't use Service names
	public static String cdrServUrl = null;

	private static ResourceBundle props;
	private static ResourceBundle ldapProps;
	public static Category logger;

	public static String dateFormatPattern="dd MMM yyyy";
	public static String timeFormatPattern="dd MMM yyyy hh:mm:ss";

	 static {

		if (logger == null) logger = Category.getInstance("gdem");

		if (props == null) {
			props = ResourceBundle.getBundle("gdem");
			try {
				queriesFolder = props.getString("queries.folder");

				//xformsFolder=props.getString("xforms.folder");


				xslFolder=checkPath(props.getString("xsl.folder"));
				tmpFolder=props.getString("tmp.folder");
				odsFolder=checkPath(props.getString("ods.folder"));

				//DB connection settings
				dbDriver = props.getString("db.driver");
				dbUrl = props.getString("db.url");
				dbUser = props.getString("db.user");
				dbPwd = props.getString("db.pwd");

				engineClass = props.getString("xq.engine.implementator");
				//DCM settings
				ddURL = props.getString("dd.url");
				gdemURL = props.getString("gdem.url");

				//settings for incoming services from DD
				invServUrl = props.getString("dd.rpc.url");
				invServName = props.getString("dd.rpcservice.name");

				//settings for incoming services from CDR
				cdrServUrl = props.getString("cdr.url");

				//period in seconds
				String frequency = props.getString("wq.check.interval");
				Float f = new Float(frequency);
				wqCheckInterval = (long) (f.floatValue() * 1000);

		        dateFormatPattern=props.getString("date.format.pattern");
		        timeFormatPattern=props.getString("time.format.pattern");

		        try {
					services_installed = Integer.parseInt(props.getString("gdem.services"));
				} catch (Exception e) { //ignore, use default
				}
				//wqCheckInterval= (Long.getLong(props.getString("wq.check.interval"))).longValue();

				//urlPrefix=props.getString("url.prefix"); //URL where the files can be downloaded
			} catch (MissingResourceException mse) {

				//no error handling? go with the default values??
			} catch (Exception e) {
				//System.out.println("error " + e.toString());
				e.printStackTrace();
			}
		}

		if (ldapProps == null) {
			ldapProps = ResourceBundle.getBundle("eionetdir");
			try {
				ldapUrl = ldapProps.getString("ldap.url");
				ldapContext = ldapProps.getString("ldap.context");
				ldapUserDir = ldapProps.getString("ldap.user.dir");
				ldapAttrUid = ldapProps.getString("ldap.attr.uid");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static String checkPath(String path){
		if(path.endsWith("/")){
			path=path.substring(0, path.length() -1);
		}
		return path;
	}

}
