package eionet.gdem.web.struts.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import eionet.gdem.conversion.ConversionService;
import eionet.gdem.conversion.ssr.InputAnalyser;
import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.dcm.business.SchemaManager;
import eionet.gdem.dcm.business.StylesheetManager;
import eionet.gdem.dto.Schema;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.DbModuleIF;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;
import eionet.gdem.utils.Utils;
import eionet.gdem.validation.ValidationService;

public class TestConvFormAction  extends Action{

	private static LoggerIF _logger=GDEMServices.getLogger();
	
	   public ActionForward execute(ActionMapping actionMapping, ActionForm actionForm, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

			
			ActionErrors errors = new ActionErrors();

			System.out.println("-------------TestConvFormAction-  start--------------");
			
			ArrayList schemas = new ArrayList();
			
			String schema= (String)httpServletRequest.getAttribute("schema");
			//getParameter("schema");
			if (schema == null){
				schema= (String)httpServletRequest.getParameter("schema");
			}
			String xmlUrl= (String)httpServletRequest.getAttribute("xmlUrl");
			// getParameter("xmlUrl");
			if(xmlUrl==null){
				xmlUrl= (String)httpServletRequest.getParameter("xmlUrl");
			}

			
			TestConvForm form=(TestConvForm)actionForm;
			form.setUrl(xmlUrl);
			
			
			if(schema.equals("")){schema=null;}
			
			String validate= (String)httpServletRequest.getAttribute("validate");
			System.out.println("schema="+schema);
			System.out.println("xmlUrl="+xmlUrl);
			System.out.println("validate="+validate);
						
			
			try{
				SchemaManager sm = new SchemaManager();
				ConversionService cs = new ConversionService();
				if (!Utils.isNullStr(schema)){				
					if (!cs.existsXMLSchema(schema)){
						throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);        					
					}
					ArrayList stylesheets = null;
					stylesheets = sm.getSchemaStylesheets(schema);
					Schema scObj = new Schema();
					scObj.setSchema(schema);
					scObj.setStylesheets(stylesheets);
					schemas.add(scObj);
				}else{
					if (!Utils.isNullStr(xmlUrl)){
						InputAnalyser analyser = new InputAnalyser();
						try{
							analyser.parseXML(xmlUrl);
						}
						catch(Exception e){
							throw new DCMException(BusinessConstants.EXCEPTION_GENERAL);        
						}
						// schema or dtd found from header
						String schemaOrDTD = analyser.getSchemaOrDTD();
						
						if (schemaOrDTD!=null){
							ArrayList stylesheets = null;
							stylesheets = sm.getSchemaStylesheets(schemaOrDTD);
							Schema scObj = new Schema();
							scObj.setSchema(schemaOrDTD);
							scObj.setStylesheets(stylesheets);
							schemas.add(scObj);							
						}
						// did not find schema or dtd from xml header
						else{
							String root_elem = analyser.getRootElement();
							String namespace = analyser.getNamespace();
							
							DbModuleIF dbM= GDEMServices.getDbModule();
							Vector matchedSchemas = dbM.getRootElemMatching(root_elem, namespace);
							for (int k=0; k<matchedSchemas.size();k++){
								HashMap schemaHash = (HashMap)matchedSchemas.get(k);
								String schema_name = (String)schemaHash.get("xml_schema");
								ArrayList stylesheets = null;
								stylesheets = sm.getSchemaStylesheets(schema_name);
								Schema scObj = new Schema();
								scObj.setSchema(schema_name);
								scObj.setStylesheets(stylesheets);
								schemas.add(scObj);								
							}
							
						}
					}
					
				}
			
				if(validate != null){
					ArrayList valid;
					if (schema==null){			//schema defined in header
						valid = validate(xmlUrl);
					}
					else{
						valid = validateSchema(xmlUrl, schema);
					}
					//httpServletRequest.getSession().setAttribute("conversion.valid", valid);
					//httpServletRequest.getSession().setAttribute("conversion.valid", valid);
					httpServletRequest.setAttribute("conversion.valid", valid);
				}
				
				
			}catch(DCMException e){			
				System.out.println(e.toString());
				e.printStackTrace();
				_logger.debug(e.toString());
				errors.add("conversion", new ActionError(e.getErrorCode()));
			}catch(Exception e){
				System.out.println(e.toString());
				e.printStackTrace();
				_logger.debug(e.toString());				
				errors.add("conversion", new ActionError(BusinessConstants.EXCEPTION_GENERAL));				
			}
	        saveErrors(httpServletRequest, errors);
			
	        httpServletRequest.getSession().setAttribute("conversion.schemas", schemas);
			System.out.println("-------------TestConvFormAction---------------");
			
			
	        return actionMapping.findForward("success");
			
			
	    }

	   private ArrayList validate(String url) throws DCMException{
			
			try {

			    //ValidationService v = new ValidationService();
				ValidationService v = new ValidationService(true);
				
				String ret =v.validate(url);
				
				return v.getErrorList();
				
		   
			} catch (Exception e) {				
				throw new DCMException(BusinessConstants.EXCEPTION_VALIDATION_ERROR);
			}
		}
		private ArrayList validateSchema(String url, String schema) throws DCMException{
			
			try {

			    //ValidationService v = new ValidationService();
				ValidationService v = new ValidationService(true);
				String ret = v.validateSchema(url, schema);
				//return v.validateSchema(url, schema);
				v.printList();
				//return ret;
				return v.getErrorList();
		   
			} catch (Exception e) {
				throw new DCMException(BusinessConstants.EXCEPTION_VALIDATION_ERROR);
			}
		}	   

}