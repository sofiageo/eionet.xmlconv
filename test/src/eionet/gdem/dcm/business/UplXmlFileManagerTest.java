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

package eionet.gdem.dcm.business;

import org.dbunit.DBTestCase;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import eionet.gdem.dto.UplXmlFile;
import eionet.gdem.exceptions.DCMException;
import eionet.gdem.test.DbHelper;
import eionet.gdem.test.TestConstants;
import eionet.gdem.test.TestUtils;
import eionet.gdem.test.mocks.MockFormFile;
import eionet.gdem.web.struts.xmlfile.UplXmlFileHolder;

/**
 * @author Enriko Käsper, Tieto Estonia
 * UplXmlFileManagerTest
 */

public class UplXmlFileManagerTest extends DBTestCase{


    /**
     * Provide a connection to the database.
     */
    public UplXmlFileManagerTest(String name)	{
        super( name );
        DbHelper.setUpConnectionProperties();
    }
    /**
     * Set up test case properties
     */
    protected void setUp()throws Exception{
        super.setUp();
        TestUtils.setUpProperties(this);
    }
    /**
     * Load the data which will be inserted for the test
     */
    protected IDataSet getDataSet() throws Exception {
        IDataSet loadedDataSet = new FlatXmlDataSet(
                getClass().getClassLoader().getResourceAsStream(
                        TestConstants.SEED_DATASET_UPLXML_XML));
        return loadedDataSet;
    }

    /**
     * The method adds XML file into DB.
     * After each operation it scheks the properties values.
     *
     * @throws Exception
     */
    public void testAddXml() throws Exception{

        String title = "XLIFF xml file";

        String user = TestConstants.TEST_ADMIN_USER;

        UplXmlFileManager xm = new UplXmlFileManager();

        UplXmlFileHolder holder1 = xm.getUplXmlFiles(user);

        MockFormFile xmlFile = new MockFormFile(getClass().getClassLoader().getResource(TestConstants.SEED_XLIFF_XML)
                .getFile());
        //add xml
        xm.addUplXmlFile(user, xmlFile, title);

        UplXmlFileHolder holder2 = xm.getUplXmlFiles(user);

        assertEquals(holder1.getXmlfiles().size()+1, holder2.getXmlfiles().size());

    }
    /**
     * The test should throw exception, because the file already exists
     * @throws Exception
     */
    public void testAddDuplicateXml() throws Exception{
        Exception dcmException = null;

        String user = TestConstants.TEST_ADMIN_USER;

        MockFormFile xmlFile = new MockFormFile(getClass().getClassLoader().getResource(TestConstants.SEED_OZONE_STATION_XML)
                .getFile());

        UplXmlFileManager xm = new UplXmlFileManager();

        try{
            xm.addUplXmlFile(user, xmlFile, "title");
        } catch (DCMException e) {
            dcmException = e;
        }

        // asset the exception object
        assertNotNull("No expected exception", dcmException);

    }

    /**
     * The method checks if xml file exists
     *
     * @throws Exception
     */
    public void testQAScriptFileExists() throws Exception{

        UplXmlFileManager xm = new UplXmlFileManager();

        //exists in DB and in filesystem
        boolean exists = xm.fileExists(TestConstants.SEED_OZONE_STATION_XML);
        assertTrue(exists);

        //exists in the DB
        boolean exists2 = xm.fileExists("some.xml");
        assertTrue(exists2);

        //does not exist
        boolean exists3 = xm.fileExists("unknown.xml");
        assertFalse(exists3);
}
    /**
     * The method updates xml file properties and verifies the values in database afterwards.
     *
     * @throws Exception
     */
    public void testUpdateXml() throws Exception{

        String xmlFileId = "1";
        String title ="Title2";
        String curFileName = TestConstants.SEED_OZONE_STATION_XML;

        String user = TestConstants.TEST_ADMIN_USER;

        MockFormFile xmlFile = new MockFormFile(getClass().getClassLoader().getResource(TestConstants.SEED_XLIFF_XML)
                .getFile());

        UplXmlFileManager xm = new UplXmlFileManager();

        //update xml file properties
        xm.updateUplXmlFile(user, xmlFileId, title, curFileName, xmlFile);

        // query xml file by id and compare fields
        UplXmlFile xmlfile = xm.getUplXmlFileById(xmlFileId);
        assertEquals(title,xmlfile.getTitle());
        assertEquals(curFileName,xmlfile.getFileName());
        assertEquals(xmlFileId,xmlfile.getId());

    }
    /**
     * The method deletes a xml and checks if it succeeded
     *
     * @throws Exception
     */
    public void testDeleteXml() throws Exception{

        String user = TestConstants.TEST_ADMIN_USER;
        String uplXmlFileId = "2";

        UplXmlFileManager xm = new UplXmlFileManager();
        UplXmlFileHolder holder1 = xm.getUplXmlFiles(user);

        //delete xml
        xm.deleteUplXmlFile(user, uplXmlFileId);

        UplXmlFileHolder holder2 = xm.getUplXmlFiles(user);

        assertEquals(holder1.getXmlfiles().size()-1, holder2.getXmlfiles().size());
    }
}
