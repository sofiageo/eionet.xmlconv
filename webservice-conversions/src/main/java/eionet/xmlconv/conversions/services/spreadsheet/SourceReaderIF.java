package eionet.xmlconv.conversions.services.spreadsheet;

import eionet.xmlconv.conversions.data.ConversionResultDto;
import eionet.xmlconv.conversions.exceptions.XMLConvException;
import eionet.xmlconv.conversions.services.datadict.DD_XMLInstance;

import java.io.File;
import java.util.Map;

/**
 * Generic source file reader interface.
 *
 * @author Enriko Käsper
 * @author George Sofianos
 */
public interface SourceReaderIF {

    /**
     * If the source file is generated from Data Dictionary, then it should contain XML Shema in metada or somewhere in content
     *
     * @return - XML Schema URL
     */
    String getXMLSchema();

    /**
     * Initialize the Source file from InputStream
     *
     * @param input
     *            input Excel or OpenDocument File
     * @throws XMLConvException If an error occurs.
     */
    void initReader(File input) throws XMLConvException;

    /**
     * Goes through the source file and writes the data into DD_XMLInstance as xml
     *
     * @param instance
     *            XML instance file, where the structure xml has been efined before
     * @throws Exception If an error occurs.
     */
    void writeContentToInstance(DD_XMLInstance instance) throws Exception;

    /**
     * Finds the first sheet name, that is not DO_NOT_DELETE_THIS_SHEET
     *
     * @return - sheet name
     */
    String getFirstSheetName();

    /**
     * If the spurce file is generated from Data Dictionary, then it finds the XML Shemas for each spreadsheet
     *
     * @return - Spreadsheet name
     */
    Map<String, String> getSheetSchemas();

    /**
     * Check if sheet has data or not
     *
     * @param sheetName
     *            - sheet name
     * @return boolean - true if has data
     */
    boolean isEmptySheet(String sheetNname);

    /**
     * Source reader ended it's job. Do some closing stuff.
     */
    void closeReader();

    /**
     * Source reader ended it's job. Do some closing stuff.
     * @param resultObject Object for storing conversion log messages.
     */
    void startReader(ConversionResultDto resultObject);
}
