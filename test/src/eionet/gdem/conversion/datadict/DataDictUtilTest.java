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

import java.util.Map;

import junit.framework.TestCase;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;

/**
 * @author Enriko Käsper, Tieto Estonia
 * DataDictUtilTest
 */

public class DataDictUtilTest extends TestCase {

	/**
	 * Tests convert method - validate the result file and metadata( content type and file name) 
	 */
	public void testGetElementsDefs() throws Exception {
		String schemaUrl =TestUtils.getSeedURL(TestConstants.SEED_GW_CONTAINER_SCHEMA,this);
		Map<String, DDElement> elemDefs = DataDictUtil.importDDElementSchemaDefs(null, schemaUrl);
		assertEquals(elemDefs.size(),43);
		
		String type = elemDefs.get("GWEWN-Code").getSchemaDataType();
		assertEquals("xs:string",type);
		
		String type2 = elemDefs.get("GWArea").getSchemaDataType();
		assertEquals("xs:decimal",type2);
	}
	/**
	 * Tests DD schema URL handling
	 */
    public void testDDUrlhandling() throws Exception{
    	assertEquals("http://dd.eionet.europa.eu/GetXmlInstance?id=3739&type=tbl",
    			DataDictUtil.getInstanceUrl("http://dd.eionet.europa.eu/GetSchema?id=TBL3739"));
    	assertEquals("TBL3739",
    			DataDictUtil.getSchemaIdParamFromUrl(("http://dd.eionet.europa.eu/GetSchema?id=TBL3739")));
    	assertEquals("DST1111",
    			DataDictUtil.getSchemaIdParamFromUrl(("http://dd.eionet.europa.eu/GetSchema?id=DST1111")));
    	assertEquals("http://dd.eionet.europa.eu/GetContainerSchema?id=DST1111",
    			DataDictUtil.getContainerSchemaUrl(("http://dd.eionet.europa.eu/GetSchema?id=DST1111")));
    }
	/**
	 * Tests convert method - validate the result file and metadata( content type and file name) 
	 */
	public void testGetContainerSchemaUrl() throws Exception {
		String url = DataDictUtil.getContainerSchemaUrl("http://dd.eionet.europa.eu/GetSchema?id=TBL4948");
		assertEquals("http://dd.eionet.europa.eu/GetContainerSchema?id=TBL4948",url);
	}

	public void testMultivalueElementsDefs() throws Exception {
		String schemaUrl =TestUtils.getSeedURL(TestConstants.SEED_GW_SCHEMA,this);
		
		Map<String, DDElement> elemDefs = DataDictUtil.importDDTableSchemaElemDefs(schemaUrl);
		
		DDElement stratElement = elemDefs.get("Stratigraphy");
		assertTrue(stratElement.isHasMultipleValues());
		assertEquals(";", stratElement.getDelimiter());

	}
}