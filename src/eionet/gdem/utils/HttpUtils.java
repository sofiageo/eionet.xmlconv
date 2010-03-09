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

package eionet.gdem.utils;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;

import eionet.gdem.dcm.BusinessConstants;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.services.GDEMServices;
import eionet.gdem.services.LoggerIF;

/**
 * @author Enriko Käsper, Tieto Estonia
 * HttpUtils
 */

public class HttpUtils {

	private static LoggerIF _logger = GDEMServices.getLogger();

	public static byte[] downloadRemoteFile(String url) throws Exception{

    	byte[] responseBody = null;
    	HttpClient client = new HttpClient();

        // Create a method instance.
        GetMethod method = new GetMethod(url);
        
        try {
          // Execute the method.
          int statusCode = client.executeMethod(method);

          if (statusCode != HttpStatus.SC_OK) {
        	_logger.error("Method failed: " + method.getStatusLine());
        	throw new DCMException(BusinessConstants.EXCEPTION_SCHEMAOPEN_ERROR,method.getStatusLine().toString());
          }

          // Read the response body.
          responseBody = method.getResponseBody();

          // Deal with the response.
          // Use caution: ensure correct character encoding and is not binary data
          //System.out.println(new String(responseBody));

        } catch (HttpException e) {
        	_logger.error("Fatal protocol violation: " + e.getMessage());
          e.printStackTrace();
          throw e;
        } catch (IOException e) {
        	_logger.error("Fatal transport error: " + e.getMessage());
          e.printStackTrace();
          throw e;
        } finally {
          // Release the connection.
          method.releaseConnection();
        }  
    	return responseBody;
    }
	
	/**
	 * Method checks whether the resource behind the given URL exist.
	 * The method calls HEAD request and if the resonse code is 200, then returns true.
	 * If exception is thrown or response code is something else, then the result is false.
	 * 
	 * @param url
	 * @return
	 */
	public static boolean urlExists(String url){
		
		
		HttpClient client = new HttpClient();

		// 	Create a method instance.
		HeadMethod method = new HeadMethod(url);
    
		try {
      // 	Execute the method.
			int statusCode = client.executeMethod(method);

			return statusCode == HttpStatus.SC_OK;
		} catch (HttpException e) {
			_logger.error("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			_logger.error("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
    // 	Release the connection.
			method.releaseConnection();
		}
	}
}
