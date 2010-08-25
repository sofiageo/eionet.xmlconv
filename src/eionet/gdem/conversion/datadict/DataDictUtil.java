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
 * The Original Code is XMLCONV.
 * 
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 * 
 * Contributor(s):
 * Enriko Käsper, Tieto Estonia
 */

package eionet.gdem.conversion.datadict;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eionet.gdem.GDEMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.xml.IXQuery;
import eionet.gdem.utils.xml.IXmlCtx;
import eionet.gdem.utils.xml.XmlContext;

/**
 * @author Enriko Käsper, Tieto Estonia
 * DataDictUtil
 */

public class DataDictUtil {
	
	public static final String INSTANCE_SERVLET = "GetXmlInstance";
	public static final String SCHEMA_SERVLET = "GetSchema";
	public static final String CONTAINER_SCHEMA_SERVLET = "GetContainerSchema";

	private static LoggerIF logger=GDEMServices.getLogger();

	public static String getInstanceUrl(String schema_url) throws GDEMException {

		try {
			
			//throws Exception, if not correct URL
			URL schemaURL = new URL(schema_url);

			String id = getSchemaIdParamFromUrl(schema_url);
			
			String type = id.substring(0, 3);
			id = id.substring(3);

			int path_idx = schema_url.toLowerCase().indexOf(
					SCHEMA_SERVLET.toLowerCase());
			String path = schema_url.substring(0, path_idx);

			String instance_url = path + INSTANCE_SERVLET + "?id=" + id
					+ "&type=" + type.toLowerCase();

			//throws Exception, if not correct URL
			URL instanceURL = new URL(instance_url);
			return instance_url;
		} catch (MalformedURLException e) {
			throw new GDEMException("Error getting Instance file URL: "
					+ e.toString() + " - " + schema_url);
		} catch (Exception e) {
			throw new GDEMException("Error getting Instance file URL: "
					+ e.toString() + " - " + schema_url);
		}
	}
	public static String getSchemaIdParamFromUrl(String schema_url) throws GDEMException {
		
		int id_idx = schema_url.indexOf("id=");
		String id = schema_url.substring(id_idx + 3);
		if (id.indexOf("&") > -1)
			id = id.substring(0, id.indexOf("&"));

		return id;
	}

	/**
	 * gather all element definitions
	 * @param instance
	 * @param schemaUrl
	 */
	public static Map<String, DDElement> importDDTableSchemaElemDefs(String schemaUrl){
		InputStream inputStream =null;
		Map<String, DDElement> elemDefs = new HashMap<String, DDElement>();
		try {
			//get element definitions for given schema
			//DataDictUtil.getSchemaElemDefs(elemDefs, schemaUrl);
			
			//load imported schema URLs
			IXmlCtx ctx=new XmlContext();
			URL url = new URL(schemaUrl);
			inputStream = url.openStream();
			ctx.checkFromInputStream(inputStream);
			
			IXQuery xQuery=ctx.getQueryManager();
			
			//run recursively the same function for importing elem defs for imported schemas
			List<String> schemas = xQuery.getSchemaImports();
			Map<String, String> multiValueElements = xQuery.getSchemaElementWithMultipleValues();
			
			for (int i = 0; i < schemas.size(); i++) {
				String schema=(String) schemas.get(i);
				DataDictUtil.importDDElementSchemaDefs(elemDefs, schema);
			}
			
			for(Map.Entry<String, String> entry : multiValueElements.entrySet()){
				DDElement multiValueElement = null;
				if(elemDefs.containsKey(entry.getKey())){
					multiValueElement = elemDefs.get(entry.getKey());
				}
				else{
					multiValueElement = new DDElement(entry.getKey());
				}
				multiValueElement.setHasMultipleValues(true);
				multiValueElement.setDelimiter(entry.getValue());
				elemDefs.put(entry.getKey(), multiValueElement);
			}
		} catch (Exception ex) {
			logger.error("Error reading schema file ", ex);
		}
		finally{
			try{
				inputStream.close();
			}catch(Exception e){}
		}
		return elemDefs;
	}

	public static Map<String, DDElement> importDDElementSchemaDefs(Map<String, DDElement> elemDefs, String schemaUrl){
		InputStream inputStream =null;
		if(elemDefs==null) elemDefs = new HashMap<String, DDElement>();
		
		try {
			IXmlCtx ctx=new XmlContext();
			URL url = new URL(schemaUrl);
			inputStream = url.openStream();
			ctx.checkFromInputStream(inputStream);
			
			IXQuery xQuery=ctx.getQueryManager();
			List<String> elemNames = xQuery.getSchemaElements();
			for (int i = 0; i < elemNames.size(); i++) {
				String elemName = elemNames.get(i);
				DDElement element = elemDefs.containsKey(elemName)?
									elemDefs.get(elemName):
									new DDElement(elemName);
				element.setSchemaDataType(xQuery.getSchemaElementType(elemName));
				elemDefs.put(elemName, element);
			}
		} catch (Exception ex) {
			logger.error("Error reading schema file ", ex);
		}
		finally{
			try{
				inputStream.close();
			}catch(Exception e){}
		}
		return elemDefs;
		
	}
	/**
	 * Returns the DD container schema URL. It holds the elements definitions
	 * @param schema_url
	 * @return
	 * @throws GDEMException
	 */
	public static String getContainerSchemaUrl(String schema_url) throws GDEMException {

		try {
			URL SchemaURL = new URL(schema_url);

			String containerSchemaUrl = schema_url.replace(DataDictUtil.SCHEMA_SERVLET, CONTAINER_SCHEMA_SERVLET);

			URL InstanceURL = new URL(containerSchemaUrl);
			return containerSchemaUrl;
		} catch (MalformedURLException e) {
			throw new GDEMException("Error getting Container Schema URL: "
					+ e.toString() + " - " + schema_url);
		} catch (Exception e) {
			throw new GDEMException("Error getting Container Schema URL: "
					+ e.toString() + " - " + schema_url);
		}
	}

}